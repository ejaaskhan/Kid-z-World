#!/usr/bin/env python3
"""
KID'Z WORLD — Offline Bangladeshi Bangla (bn-BD) Audio Asset Generator & Verifier
Developed by Dev. E

Generates real, clear, child-friendly Bangladeshi Bangla MP3 audio files bundled into:
  app/src/main/assets/audio/bangla/
    - alphabet/      (Bangla অ–ঔ, ক–ন + English A–Z with Bangla pronunciation support)
    - numbers/       (0–100 in authentic Bangladeshi Bangla)
    - fruits/        (Fruits & vegetables lessons + sorting puzzle items)
    - animals/       (Animals & birds lessons + sorting puzzle items)
    - colors/        (Colors lessons)
    - shapes/        (Shapes lessons)
    - common_words/  (Body, vehicles, nature, words, vocab, sentences, puzzle tokens)
    - stories/       (All illustrated story pages & morals)
    - poems/         (All 8 classic Bangla children's poems: title + every stanza)
    - instructions/  (Welcome, voice test, math operators, quiz & puzzle cues)

Writes app/src/main/assets/audio/bangla/manifest.json as the single source of truth
consumed directly by the Android app's BanglaAudioManifest at runtime.
"""

from __future__ import annotations

import argparse
import concurrent.futures
import json
import os
import re
import subprocess
import sys
import tempfile
import time
import urllib.parse
import urllib.request
from pathlib import Path
from typing import Any

REPO_ROOT = Path(__file__).resolve().parent.parent
AUDIO_ROOT = REPO_ROOT / "app" / "src" / "main" / "assets" / "audio" / "bangla"
MANIFEST_PATH = AUDIO_ROOT / "manifest.json"
MIN_VALID_MP3_BYTES = 1200


# ==============================================================================
# COMPLETE CURRICULUM CATALOG (Exact 1:1 Match with Kotlin Room & Catalog IDs)
# ==============================================================================

BANGLA_NUMBERS_0_TO_100 = [
    "শূন্য",
    "এক", "দুই", "তিন", "চার", "পাঁচ", "ছয়", "সাত", "আট", "নয়", "দশ",
    "এগারো", "বারো", "তেরো", "চৌদ্দ", "পনেরো", "ষোলো", "সতেরো", "আঠারো", "উনিশ", "বিশ",
    "একুশ", "বাইশ", "তেইশ", "চব্বিশ", "পঁচিশ", "ছাব্বিশ", "সাতাশ", "আটাশ", "ঊনত্রিশ", "ত্রিশ",
    "একত্রিশ", "বত্রিশ", "তেত্রিশ", "চৌত্রিশ", "পঁয়ত্রিশ", "ছত্রিশ", "সাঁইত্রিশ", "আটত্রিশ", "ঊনচল্লিশ", "চল্লিশ",
    "একচল্লিশ", "বিয়াল্লিশ", "তেতাল্লিশ", "চুয়াল্লিশ", "পঁয়তাল্লিশ", "ছেচল্লিশ", "সাতচল্লিশ", "আটচল্লিশ", "ঊনপঞ্চাশ", "পঞ্চাশ",
    "একান্ন", "বাহান্ন", "তিপ্পান্ন", "চুয়ান্ন", "পঞ্চান্ন", "ছাপ্পান্ন", "সাতান্ন", "আটান্ন", "ঊনষাট", "ষাট",
    "একষট্টি", "বাষট্টি", "তেষট্টি", "চৌষট্টি", "পঁয়ষট্টি", "ছেষট্টি", "সাতষট্টি", "আটষট্টি", "ঊনসত্তর", "সত্তর",
    "একাত্তর", "বাহাত্তর", "তিয়াত্তর", "চুয়াত্তর", "পঁচাত্তর", "ছিয়াত্তর", "সাতাত্তর", "আটাত্তর", "ঊনআশি", "আশি",
    "একাশি", "বিরাশি", "তিরাশি", "চুরাশি", "পঁচাশি", "ছিয়াশি", "সাতাশি", "আটাশি", "ঊননব্বই", "নব্বই",
    "একানব্বই", "বিরানব্বই", "তিরানব্বই", "চুরানব্বই", "পঁচানব্বই", "ছিয়ানব্বই", "সাতানব্বই", "আটানব্বই", "নিরানব্বই", "একশো",
]

BANGLA_DIGITS = str.maketrans("0123456789", "০১২৩৪৫৬৭৮৯")


def to_bn_digits(n: int) -> str:
    return str(n).translate(BANGLA_DIGITS)


def build_manifest_entries() -> list[dict[str, Any]]:
    entries: list[dict[str, Any]] = []

    def add(
        clip_id: str,
        category: str,
        filename: str,
        script_bn: str,
        aliases: list[str] | None = None,
    ) -> None:
        rel_path = f"audio/bangla/{category}/{filename}"
        entries.append(
            {
                "id": clip_id,
                "category": category,
                "assetPath": rel_path,
                "scriptBn": script_bn.strip(),
                "aliases": [a.strip() for a in (aliases or []) if a and a.strip()],
            }
        )

    # 1. BANGLA ALPHABET (bn_alpha_0..24: Sworoborno অ–ঔ + Banjonborno ক–ন)
    bn_letters = [
        ("অ", "অজগর"),
        ("আ", "আম"),
        ("ই", "ইঁদুর"),
        ("ঈ", "ঈগল"),
        ("উ", "উট"),
        ("ঊ", "ঊষা"),
        ("ঋ", "ঋতু"),
        ("এ", "একতারা"),
        ("ঐ", "ঐরাবত"),
        ("ও", "ওল"),
        ("ঔ", "ঔষধ"),
        ("ক", "কলা"),
        ("খ", "খরগোশ"),
        ("গ", "গরু"),
        ("ঘ", "ঘুড়ি"),
        ("ঙ", "ব্যাঙ"),
        ("চ", "চাঁদ"),
        ("ছ", "ছাতা"),
        ("জ", "জাহাজ"),
        ("ঝ", "ঝিনুক"),
        ("ট", "টিয়া"),
        ("ঠ", "ঠেলাগাড়ি"),
        ("ড", "ডাব"),
        ("ত", "তারা"),
        ("ন", "নৌকা"),
    ]
    for idx, (glyph, word_bn) in enumerate(bn_letters):
        add(
            clip_id=f"bn_alpha_{idx}",
            category="alphabet",
            filename=f"bn_alpha_{idx}.mp3",
            script_bn=f"{glyph}। {glyph} তে {word_bn}।",
            aliases=[glyph, f"বর্ণ {glyph}", f"{glyph} তে {word_bn}", word_bn],
        )

    # 2. ENGLISH ALPHABET A–Z WITH BANGLA PRONUNCIATION SUPPORT (en_alpha_a..z)
    en_letters_bn = [
        ("a", "A", "এ", "আপেল"),
        ("b", "B", "বি", "বল"),
        ("c", "C", "সি", "বিড়াল"),
        ("d", "D", "ডি", "হাঁস"),
        ("e", "E", "ই", "হাতি"),
        ("f", "F", "এফ", "মাছ"),
        ("g", "G", "জি", "আঙুর"),
        ("h", "H", "এইচ", "বাড়ি"),
        ("i", "I", "আই", "আইসক্রিম"),
        ("j", "J", "জে", "জুস"),
        ("k", "K", "কে", "ঘুড়ি"),
        ("l", "L", "এল", "সিংহ"),
        ("m", "M", "এম", "আম"),
        ("n", "N", "এন", "পাখির বাসা"),
        ("o", "O", "ও", "পেঁচা"),
        ("p", "P", "পি", "পান্ডা"),
        ("q", "Q", "কিউ", "রানি"),
        ("r", "R", "আর", "রংধনু"),
        ("s", "S", "এস", "সূর্য"),
        ("t", "T", "টি", "বাঘ"),
        ("u", "U", "ইউ", "ছাতা"),
        ("v", "V", "ভি", "বেহালা"),
        ("w", "W", "ডাব্লিউ", "তিমি"),
        ("x", "X", "এক্স", "জাইলোফোন"),
        ("y", "Y", "ওয়াই", "ইয়ো-ইয়ো"),
        ("z", "Z", "জেড", "জেব্রা"),
    ]
    for key, upper, bn_name, bn_word in en_letters_bn:
        add(
            clip_id=f"en_alpha_{key}",
            category="alphabet",
            filename=f"en_alpha_{key}.mp3",
            script_bn=f"{bn_name}। {bn_name} ফর {bn_word}।",
            aliases=[f"অক্ষর {upper}", f"{upper} — {bn_word}"],
        )

    # 3. NUMBERS 0–100 (num_0..num_100)
    for n, word_bn in enumerate(BANGLA_NUMBERS_0_TO_100):
        bn_dig = to_bn_digits(n)
        add(
            clip_id=f"num_{n}",
            category="numbers",
            filename=f"number_{n}.mp3",
            script_bn=f"{word_bn}।",
            aliases=[word_bn, bn_dig, str(n), f"সংখ্যা {bn_dig}"],
        )

    # 4. FRUITS & VEGETABLES (fruitveg_* + extra sorting items)
    fruits_veggies = [
        ("fruitveg_mango", "আম", "আম। মিষ্টি রসে ভরা ফলের রাজা।"),
        ("fruitveg_apple", "আপেল", "আপেল। কচকচে লাল আপেল শরীর সুস্থ রাখে।"),
        ("fruitveg_banana", "কলা", "কলা। নরম হলুদ কলা শক্তি জোগায়।"),
        ("fruitveg_watermelon", "তরমুজ", "তরমুজ। গরমের রসালো মিষ্টি তরমুজ।"),
        ("fruitveg_grapes", "আঙুর", "আঙুর। থোকায় থোকায় মিষ্টি আঙুর।"),
        ("fruitveg_carrot", "গাজর", "গাজর। চোখের জন্য উপকারী কমলা গাজর।"),
        ("fruitveg_tomato", "টমেটো", "টমেটো। লাল টুকটুকে রসালো টমেটো।"),
        ("fruitveg_broccoli", "ব্রকলি", "ব্রকলি। ছোট্ট সবুজ গাছের মতো ব্রকলি।"),
        ("fruitveg_potato", "আলু", "আলু। মাটির নিচে জন্মানো মজার আলু।"),
        ("fruitveg_corn", "ভুট্টা", "ভুট্টা। হলুদ দানাদার মিষ্টি ভুট্টা।"),
    ]
    for cid, title_bn, script in fruits_veggies:
        add(cid, "fruits", f"{cid}.mp3", script, aliases=[title_bn])

    # Short crisp fruit names for Sorting & Pattern puzzles
    extra_fruits = [
        ("sort_mango", "আম"),
        ("sort_apple", "আপেল"),
        ("sort_banana", "কলা"),
        ("sort_grapes", "আঙুর"),
        ("sort_watermelon", "তরমুজ"),
        ("sort_orange", "কমলা"),
        ("token_strawberry", "স্ট্রবেরি"),
    ]
    for cid, name_bn in extra_fruits:
        add(cid, "fruits", f"{cid}.mp3", f"{name_bn}।", aliases=[name_bn])

    # 5. ANIMALS & BIRDS (animal_* + sorting animal items)
    animals = [
        ("animal_tiger", "বাঘ", "বাঘ। হালুম! ডোরাকাটা সাহসী বাঘ।"),
        ("animal_elephant", "হাতি", "হাতি। লম্বা শুঁড়ের দয়ালু হাতি।"),
        ("animal_rabbit", "খরগোশ", "খরগোশ। লাফিয়ে চলা নরম খরগোশ।"),
        ("animal_owl", "পেঁচা", "পেঁচা। রাতের জ্ঞানী বন্ধু পেঁচা।"),
        ("animal_duck", "হাঁস", "হাঁস। প্যাঁক প্যাঁক! পুকুরে সাঁতার কাটে।"),
        ("animal_cat", "বিড়াল", "বিড়াল। মিউ মিউ! আদুরে বন্ধু বিড়াল।"),
        ("animal_panda", "পান্ডা", "পান্ডা। সবুজ বাঁশ খেতে ভালোবাসে পান্ডা।"),
        ("animal_butterfly", "প্রজাপতি", "প্রজাপতি। রঙিন ডানায় উড়ে বেড়ায় প্রজাপতি।"),
        ("animal_peacock", "ময়ূর", "ময়ূর। রঙিন পেখম মেলে নাচে ময়ূর।"),
        ("animal_parrot", "টিয়া পাখি", "টিয়া পাখি। সবুজ রঙের মিষ্টি কথা বলা টিয়া।"),
    ]
    for cid, title_bn, script in animals:
        add(cid, "animals", f"{cid}.mp3", script, aliases=[title_bn])

    extra_animals = [
        ("sort_lion", "সিংহ"),
        ("sort_tiger", "বাঘ"),
        ("sort_elephant", "হাতি"),
        ("sort_rabbit", "খরগোশ"),
        ("sort_parrot", "তোতা পাখি"),
        ("sort_cat", "বিড়াল"),
        ("token_frog", "ব্যাঙ"),
    ]
    for cid, name_bn in extra_animals:
        add(cid, "animals", f"{cid}.mp3", f"{name_bn}।", aliases=[name_bn])

    # 6. COLORS (color_*)
    colors = [
        ("color_red", "লাল", "লাল রং। মিষ্টি লাল স্ট্রবেরির মতো।"),
        ("color_blue", "নীল", "নীল রং। উজ্জ্বল নীল আকাশের মতো।"),
        ("color_yellow", "হলুদ", "হলুদ রং। উষ্ণ হলুদ সূর্যের মতো।"),
        ("color_green", "সবুজ", "সবুজ রং। সবুজ গাছের পাতার মতো।"),
        ("color_orange", "কমলা", "কমলা রং। রসালো কমলার মতো।"),
        ("color_purple", "বেগুনি", "বেগুনি রং। মিষ্টি বেগুনি আঙুরের মতো।"),
        ("color_pink", "গোলাপি", "গোলাপি রং। সুন্দর গোলাপি পদ্মের মতো।"),
        ("color_teal", "ফিরোজা", "ফিরোজা রং। ঝিলমিল নদীর ঢেউয়ের মতো।"),
    ]
    for cid, title_bn, script in colors:
        add(cid, "colors", f"{cid}.mp3", script, aliases=[title_bn, f"রং {title_bn}"])

    # 7. SHAPES (shape_*)
    shapes = [
        ("shape_circle", "বৃত্ত", "বৃত্ত। গোল বলের মতো বৃত্ত।"),
        ("shape_square", "বর্গক্ষেত্র", "বর্গক্ষেত্র। চারটি সমান বাহু।"),
        ("shape_triangle", "ত্রিভুজ", "ত্রিভুজ। তিনটি কোণা যেমন পাহাড়।"),
        ("shape_star", "তারা", "তারা। রাতের আকাশে ঝিকিমিকি তারা।"),
        ("shape_heart", "হৃদয়", "হৃদয়। ভালোবাসা ও দয়ার প্রতীক।"),
        ("shape_diamond", "হীরক", "হীরক। উড়ন্ত ঘুড়ির মতো আকৃতি।"),
        ("shape_rectangle", "আয়তক্ষেত্র", "আয়তক্ষেত্র। বই বা দরজার মতো আকৃতি।"),
        ("shape_oval", "ডিম্বাকৃতি", "ডিম্বাকৃতি। ডিমের মতো মসৃণ গোল আকৃতি।"),
    ]
    for cid, title_bn, script in shapes:
        add(cid, "shapes", f"{cid}.mp3", script, aliases=[title_bn, f"আকৃতি: {title_bn}"])

    # 8. COMMON WORDS (Human Body, Vehicles, Nature, Basic Words, Everyday Vocab, Simple Sentences, Bangla Vocab, Puzzle tokens)
    body_items = [
        ("body_eyes", "চোখ", "চোখ। চোখ দিয়ে আমরা সুন্দর পৃথিবী দেখি।"),
        ("body_ears", "কান", "কান। কান দিয়ে আমরা গান ও গল্প শুনি।"),
        ("body_nose", "নাক", "নাক। নাক দিয়ে আমরা ফুলের গন্ধ নিই।"),
        ("body_mouth", "মুখ ও হাসি", "মুখ ও হাসি। মুখ দিয়ে আমরা মিষ্টি কথা বলি ও খাই।"),
        ("body_hands", "হাত", "হাত। হাত দিয়ে আমরা ছবি আঁকি ও সাহায্য করি।"),
        ("body_feet", "পা", "পা। পা দিয়ে আমরা হাঁটি ও খেলাধুলা করি।"),
        ("body_heart", "হৃদপিণ্ড", "হৃদপিণ্ড। সারাদিন ধুকপুক করে আমাদের সুস্থ রাখে।"),
        ("body_brain", "মস্তিষ্ক", "মস্তিষ্ক। ভাবতে, শিখতে ও স্বপ্ন দেখতে সাহায্য করে।"),
    ]
    for cid, title_bn, script in body_items:
        add(cid, "common_words", f"{cid}.mp3", script, aliases=[title_bn])

    vehicles = [
        ("vehicle_car", "গাড়ি", "গাড়ি। চার চাকায় রাস্তায় ছোটে গাড়ি।"),
        ("vehicle_bus", "স্কুল বাস", "স্কুল বাস। বন্ধুদের নিয়ে স্কুলে যায় বাস।"),
        ("vehicle_train", "ট্রেন", "ট্রেন। ঝিকঝিক শব্দে রেললাইনে চলে ট্রেন।"),
        ("vehicle_boat", "নৌকা", "নৌকা। নদীর ঢেউয়ে ভেসে চলে নৌকা।"),
        ("vehicle_airplane", "উড়োজাহাজ", "উড়োজাহাজ। মেঘের ওপরে আকাশে ওড়ে উড়োজাহাজ।"),
        ("vehicle_bicycle", "সাইকেল", "সাইকেল। দুই চাকায় প্যাডেল ঘুরিয়ে চলে সাইকেল।"),
        ("vehicle_rocket", "রকেট", "রকেট। চাঁদ আর তারার দেশে ওড়ে রকেট।"),
        ("vehicle_rickshaw", "রিকশা", "রিকশা। রঙিন তিন চাকার চেনা রিকশা।"),
    ]
    for cid, title_bn, script in vehicles:
        add(cid, "common_words", f"{cid}.mp3", script, aliases=[title_bn])

    extra_vehicles_tokens = [
        ("sort_bus", "বাস"),
        ("sort_train", "ট্রেন"),
        ("sort_boat", "নৌকা"),
        ("sort_plane", "বিমান"),
        ("sort_bike", "সাইকেল"),
        ("sort_car", "গাড়ি"),
        ("token_blue_circle", "নীল বৃত্ত"),
        ("token_sunflower", "সূর্যমুখী"),
    ]
    for cid, name_bn in extra_vehicles_tokens:
        add(cid, "common_words", f"{cid}.mp3", f"{name_bn}।", aliases=[name_bn])

    nature = [
        ("nature_sun", "সূর্য", "সূর্য। পৃথিবীকে আলো আর উষ্ণতা দেয় সূর্য।"),
        ("nature_moon", "চাঁদ", "চাঁদ। রাতের আকাশে মিষ্টি আলো ছড়ায় চাঁদ।"),
        ("nature_tree", "গাছ", "গাছ। আমাদের ছায়া, ফল আর তাজা বাতাস দেয়।"),
        ("nature_river", "নদী", "নদী। বয়ে চলা স্বচ্ছ জলের সুন্দর নদী।"),
        ("nature_rain", "বৃষ্টি", "বৃষ্টি। সবুজ প্রকৃতিকে সতেজ করে বৃষ্টি।"),
        ("nature_rainbow", "রংধনু", "রংধনু। আকাশে আঁকা সাত রঙের রংধনু।"),
        ("nature_flower", "ফুল", "ফুল। মৌমাছি ও প্রজাপতির প্রিয় রঙিন ফুল।"),
        ("nature_mountain", "পাহাড়", "পাহাড়। মেঘ ছোঁয়া উঁচু সবুজ পাহাড়।"),
    ]
    for cid, title_bn, script in nature:
        add(cid, "common_words", f"{cid}.mp3", script, aliases=[title_bn])

    basic_words = [
        ("lang_word_0", "বই", "বই। আমি মজার গল্পের বই পড়ি।"),
        ("lang_word_1", "পানি", "পানি। প্রতিদিন বিশুদ্ধ পানি পান করো।"),
        ("lang_word_2", "বাড়ি", "বাড়ি। আমাদের বাড়ি সুন্দর ও আরামদায়ক।"),
        ("lang_word_3", "বন্ধু", "বন্ধু। বন্ধুর সাথে খেলতে খুব ভালো লাগে।"),
        ("lang_word_4", "হাসি", "হাসি। একটি মিষ্টি হাসি দিনটা সুন্দর করে।"),
        ("lang_word_5", "তারা", "তারা। আকাশে ছোট্ট তারাগুলো ঝিকমিক করে।"),
        ("lang_word_6", "বল", "বল। রঙিন বলটি উঁচু করে বাউন্স করো।"),
        ("lang_word_7", "সংগীত", "সংগীত। মিষ্টি গান আমাদের আনন্দ দেয়।"),
    ]
    for cid, title_bn, script in basic_words:
        add(cid, "common_words", f"{cid}.mp3", script, aliases=[title_bn])

    everyday_vocab = [
        ("lang_vocab_0", "শুভ সকাল", "শুভ সকাল। সকালে হাসিমুখে বলো শুভ সকাল!"),
        ("lang_vocab_1", "ধন্যবাদ", "ধন্যবাদ। কেউ সাহায্য করলে বলো ধন্যবাদ।"),
        ("lang_vocab_2", "দয়া করে", "দয়া করে। ভদ্রভাবে কিছু চাইতে বলো দয়া করে।"),
        ("lang_vocab_3", "দুঃখিত", "দুঃখিত। ভুল হলে মিষ্টি করে বলো দুঃখিত।"),
        ("lang_vocab_4", "পরিবার", "পরিবার। আমার পরিবার আমাকে খুব ভালোবাসে।"),
        ("lang_vocab_5", "বিদ্যালয়", "বিদ্যালয়। বিদ্যালয়ে আমরা নতুন অনেক কিছু শিখি।"),
        ("lang_vocab_6", "ভাগাভাগি", "ভাগাভাগি। খেলনা ভাগাভাগি করলে আনন্দ দ্বিগুণ হয়!"),
        ("lang_vocab_7", "শুভরাত্রি", "শুভরাত্রি। ঘুমানোর আগে সবাইকে বলো শুভরাত্রি।"),
    ]
    for cid, title_bn, script in everyday_vocab:
        add(cid, "common_words", f"{cid}.mp3", script, aliases=[title_bn])

    simple_sentences = [
        ("lang_sent_0", "আমি শিখতে ভালোবাসি।"),
        ("lang_sent_1", "সূর্য খুব উজ্জ্বল।"),
        ("lang_sent_2", "এটি একটি মিষ্টি আম।"),
        ("lang_sent_3", "পাখিরা আকাশে ওড়ে।"),
        ("lang_sent_4", "আমি আমার বন্ধুদের সাহায্য করি।"),
        ("lang_sent_5", "রাতে চাঁদ আলো দেয়।"),
        ("lang_sent_6", "আমি প্রতিদিন দাঁত ব্রাশ করি।"),
        ("lang_sent_7", "সবুজ গাছ আমাদের বাতাস দেয়।"),
    ]
    for cid, sent_bn in simple_sentences:
        add(cid, "common_words", f"{cid}.mp3", sent_bn, aliases=[sent_bn])

    bangla_vocab = [
        ("lang_bn_0", "ভালোবাসা", "ভালোবাসা। পরিবার ও বন্ধুদের প্রতি ভালোবাসা।"),
        ("lang_bn_1", "আকাশ", "আকাশ। নীল আকাশে সাদা মেঘ ভাসে।"),
        ("lang_bn_2", "পাখি", "পাখি। ভোরবেলা পাখিরা মিষ্টি গান গায়।"),
        ("lang_bn_3", "ফুল", "ফুল। বাগানে রঙিন ফুল ফুটেছে।"),
        ("lang_bn_4", "নদী", "নদী। বাংলাদেশ সুন্দর নদীর দেশ।"),
        ("lang_bn_5", "বৃষ্টি", "বৃষ্টি। টাপুর টুপুর বৃষ্টি পড়ে।"),
        ("lang_bn_6", "স্বপ্ন", "স্বপ্ন। বড় হয়ে ভালো মানুষ হওয়ার স্বপ্ন।"),
        ("lang_bn_7", "আনন্দ", "আনন্দ। একসাথে শিখতে অনেক আনন্দ!"),
    ]
    for cid, title_bn, script in bangla_vocab:
        add(cid, "common_words", f"{cid}.mp3", script, aliases=[title_bn])

    # 9. ILLUSTRATED STORIES (All 3 stories x 4 pages + morals)
    stories_data = [
        (
            "story_brave_tiger",
            "সাহসী ছোট্ট বাঘ",
            "সত্যিকারের সাহস হলো ছোট্ট বন্ধুকেও ভালোবাসা ও সাহায্য করা।",
            [
                "রোদেলা সুন্দরবনের ভেতরে থাকত টিকু নামের এক হাসিখুশি ছোট্ট বাঘছানা। সে তার লাল স্কার্ফ পরতে খুব ভালোবাসত।",
                "এক সোনালি সকালে কাঠের সেতুর কাছে টিকু শুনল ছোট্ট 'কিচির মিচির' ডাক। একটি হলুদ পাখি ছানা নদী পার হতে ভয় পাচ্ছিল।",
                "টিকু আস্তে আস্তে সেতুতে পা রাখল, মিষ্টি করে হাসল এবং ছোট্ট পাখিটিকে নিরাপদে ঝিলমিল নদী পার করে দিল।",
                "পাখিটি খুশিতে মিষ্টি গান গাইল, আর পুরো বন হাততালি দিয়ে বলল—টিকু সবচেয়ে দয়ালু আর সাহসী বাঘছানা!",
            ],
        ),
        (
            "story_rabbit_moon",
            "চালাক খরগোশ ও চাঁদের হাসি",
            "কৌতূহল আর শান্ত মন অন্ধকার রাতকেও আলোকিত করে।",
            [
                "প্রতি সন্ধ্যায় তুলতুলে ছোট্ট খরগোশ মিতু ফুলের টিলায় লাফিয়ে উঠত রাতের আকাশের তারা গুনতে।",
                "আজ রাতে মিতু দেখল আকাশে একটি বড় সোনালি চাঁদ—আর শান্ত পুকুরের জলেও আরেকটি চাঁদ হাসছে!",
                "মিতু আস্তে করে জলে ছোঁয়া দিল। ঢেউয়ের তালে চাঁদটা নেচে উঠল! মিতু হেসে বলল, 'আহা! পুকুরটা তো চাঁদের আয়না!'",
                "খুশি মনে মিতু হাসিমুখ চাঁদকে শুভরাত্রি জানিয়ে মিষ্টি স্বপ্নের দেশে ঘুমিয়ে পড়ল।",
            ],
        ),
        (
            "story_kind_elephant",
            "দয়ালু ছোট্ট হাতি",
            "আনন্দ ও সাহায্য ভাগাভাগি করলে সবাই একসাথে হাসে।",
            [
                "রোদেলা নদীর তীরে থাকত গোলু নামের এক মিষ্টি হাতিছানা। তার মাথায় ছিল সবুজ পাতার সুন্দর মুকুট।",
                "এক গরম দুপুরে গোলাপি পদ্মফুলগুলো তৃষ্ণার্ত হয়ে পড়ল, আর চারটি ছোট্ট হাঁসছানা ঠান্ডা জলের খেলা চাইল।",
                "গোলু তার প্রিয় নীল ঝাঁঝরি ভরে পদ্মফুল আর হাসিখুশি হাঁসছানাদের ওপর রংধনুর মতো ঝিলমিল জল ছিটিয়ে দিল।",
                "আকাশ জুড়ে ভেসে উঠল এক বিশাল রংধনু, আর সবাই আনন্দে নেচে উঠল। দয়াই পৃথিবীকে সবচেয়ে সুন্দর করে!",
            ],
        ),
    ]
    for story_id, title_bn, moral_bn, pages in stories_data:
        add(f"{story_id}_title", "stories", f"{story_id}_title.mp3", title_bn, aliases=[title_bn])
        add(f"{story_id}_moral", "stories", f"{story_id}_moral.mp3", moral_bn, aliases=[moral_bn])
        for p_idx, page_text in enumerate(pages, start=1):
            add(
                f"{story_id}_page_{p_idx}",
                "stories",
                f"{story_id}_page_{p_idx}.mp3",
                page_text,
                aliases=[page_text],
            )

    # 10. BANGLA CHILDREN'S POEMS (All 8 poems: title + every individual stanza)
    poems_data = [
        (
            "poem_chand_mama",
            "আয় আয় চাঁদ মামা",
            [
                "আয় আয় চাঁদ মামা, টিপ দিয়ে যা। চাঁদের কপালে চাঁদ, টিপ দিয়ে যা।",
                "মাছ কাটলে মুড়ো দেবো, ধান ভানলে কুঁড়ো দেবো।",
                "কালো গরুর দুধ দেবো, দুধ খাবার বাটি দেবো।",
                "চাঁদের কপালে চাঁদ, টিপ দিয়ে যা।",
            ],
        ),
        (
            "poem_hattimatim",
            "হাট্টিমাটিম টিম",
            [
                "হাট্টিমাটিম টিম, তারা মাঠে পাড়ে ডিম।",
                "তাদের খাড়া দুটো শিং, তারা হাট্টিমাটিম টিম।",
                "খেলছে মাঠে সারাবেলা, আনন্দময় ছড়ার মেলা।",
            ],
        ),
        (
            "poem_ata_gachhe",
            "আতা গাছে তোতা পাখি",
            [
                "আতা গাছে তোতা পাখি, ডালিম গাছে মৌ।",
                "এত ডাকি তবু কথা, কও না কেন বউ?",
                "গাছের ডালে মিষ্টি গান, জুড়িয়ে দেয় সবার প্রাণ।",
            ],
        ),
        (
            "poem_ay_brishti",
            "আয় বৃষ্টি ঝেঁপে",
            [
                "আয় বৃষ্টি ঝেঁপে, ধান দেবো মেপে।",
                "লেবুর পাতা করমচা, যা বৃষ্টি ধরে যা।",
                "টাপুর টুপুর বৃষ্টি পড়ে, ছোট্ট খুকু হাসে ঘরে।",
            ],
        ),
        (
            "poem_tai_tai",
            "তাই তাই তাই",
            [
                "তাই তাই তাই, মামা বাড়ি যাই।",
                "মামি দিলো দুধ-ভাত, দুয়ারে বসে খাই।",
                "মামা এলো লাঠি হাতে, হাসি খুশি সবাই সাথে।",
            ],
        ),
        (
            "poem_noton_payra",
            "নোটন নোটন পায়রাগুলি",
            [
                "নোটন নোটন পায়রাগুলি, ঝোঁটন বেঁধেছে।",
                "বড় সাহেবের বিবিগুলি, নাইতে এসেছে।",
                "দুই পারে দুই রুই কাতলা, ভেসে উঠেছে।",
                "কে দেখেছে কে দেখেছে, দাদা দেখেছে।",
            ],
        ),
        (
            "poem_khokon_khokon",
            "খোকন খোকন ডাক পাড়ি",
            [
                "খোকন খোকন ডাক পাড়ি, খোকন মোদের কার বাড়ি?",
                "আয় রে খোকন ঘরে আয়, দুধ-মাখা ভাত কাকে খায়।",
                "খোকন যাবে নায়ে, লাল জুতো পায়ে।",
            ],
        ),
        (
            "poem_aam_pata",
            "আম পাতা জোড়া জোড়া",
            [
                "আম পাতা জোড়া জোড়া, মারবো চাবুক চড়বো ঘোড়া।",
                "ওরে বুবু সরে দাঁড়া, আসছে আমার পাগলা ঘোড়া।",
                "পাগলা ঘোড়া ক্ষেপেছে, আনন্দে মন মেতেছে।",
            ],
        ),
    ]
    for poem_id, title_bn, stanzas in poems_data:
        add(f"{poem_id}_title", "poems", f"{poem_id}_title.mp3", title_bn, aliases=[title_bn])
        for s_idx, stanza in enumerate(stanzas):
            add(
                f"{poem_id}_stanza_{s_idx}",
                "poems",
                f"{poem_id}_stanza_{s_idx}.mp3",
                stanza,
                aliases=[stanza],
            )

    # 11. LEARNING INSTRUCTIONS, MATH OPERATORS, QUIZZES & PUZZLE CUES
    instructions = [
        (
            "inst_welcome",
            "welcome_bn.mp3",
            "স্বাগতম কিড'জ ওয়ার্ল্ডে! এসো খেলি, শিখি আর বড় হই।",
            ["স্বাগতম কিড'জ ওয়ার্ল্ডে!", "স্বাগতম কিড'জ ওয়ার্ল্ডে"],
        ),
        (
            "inst_test_voice",
            "test_voice_bn.mp3",
            "আয় আয় চাঁদ মামা, টিপ দিয়ে যা। কিড'জ ওয়ার্ল্ডে স্বাগতম!",
            ["আয় আয় চাঁদ মামা, টিপ দিয়ে যা। কিড'জ ওয়ার্ল্ডে স্বাগতম!"],
        ),
        (
            "inst_tap_to_hear",
            "tap_to_hear_bn.mp3",
            "শুনতে ট্যাপ করো!",
            ["শুনতে ট্যাপ করো", "শুনতে ট্যাপ করো!"],
        ),
        (
            "inst_great_job",
            "great_job_bn.mp3",
            "দারুণ করেছ! শাবাশ!",
            ["দারুণ করেছ!", "দারুণ!", "শাবাশ!"],
        ),
        (
            "inst_try_again",
            "try_again_bn.mp3",
            "আবার চেষ্টা করো, তুমি পারবে!",
            ["আবার চেষ্টা করো!", "আবার চেষ্টা করো"],
        ),
        (
            "inst_jigsaw_solved",
            "jigsaw_solved_bn.mp3",
            "বাহ! দারুণ ছবি মিলিয়েছ!",
            ["বাহ! দারুণ ছবি মিলিয়েছ!"],
        ),
        (
            "inst_sorting_prompt",
            "sorting_prompt_bn.mp3",
            "এটি কোন ঝুড়িতে যাবে?",
            ["এটি কোন ঝুড়িতে যাবে?"],
        ),
        (
            "inst_pattern_prompt",
            "pattern_prompt_bn.mp3",
            "এর পরে কী আসবে?",
            ["এর পরে কী আসবে?"],
        ),
        (
            "inst_quiz_find",
            "quiz_find_bn.mp3",
            "খুঁজে বের করো",
            ["খুঁজে বের করো", "খুঁজে বের করো:"],
        ),
        (
            "inst_math_plus",
            "math_plus_bn.mp3",
            "যোগ",
            ["যোগ"],
        ),
        (
            "inst_math_minus",
            "math_minus_bn.mp3",
            "বিয়োগ",
            ["বিয়োগ"],
        ),
        (
            "inst_math_equals_what",
            "math_equals_what_bn.mp3",
            "সমান কত?",
            ["সমান কত?", "সমান কত"],
        ),
    ]
    for cid, fname, script, aliases in instructions:
        add(cid, "instructions", fname, script, aliases=aliases)

    return entries


# ==============================================================================
# REAL BANGLADESHI BANGLA MP3 SYNTHESIS (bn-BD Neural Voice + FFmpeg)
# ==============================================================================

def split_bangla_chunks(text: str, max_chars: int = 165) -> list[str]:
    clean = re.sub(r"\s+", " ", text).strip()
    if len(clean) <= max_chars:
        return [clean]
    # Split on natural Bengali/punctuation sentence breaks
    parts = re.split(r"(?<=[।!?])\s+", clean)
    chunks: list[str] = []
    buf = ""
    for part in parts:
        if not part:
            continue
        if len(buf) + len(part) + 1 <= max_chars:
            buf = f"{buf} {part}".strip() if buf else part
        else:
            if buf:
                chunks.append(buf)
            if len(part) <= max_chars:
                buf = part
            else:
                # Fallback split on comma or spaces
                words = part.split(" ")
                sub = ""
                for w in words:
                    if len(sub) + len(w) + 1 <= max_chars:
                        sub = f"{sub} {w}".strip() if sub else w
                    else:
                        if sub:
                            chunks.append(sub)
                        sub = w
                buf = sub
    if buf:
        chunks.append(buf)
    return chunks


def fetch_google_bn_tts_mp3(text: str, retries: int = 4) -> bytes:
    url = (
        "https://translate.google.com/translate_tts?ie=UTF-8&client=tw-ob&tl=bn-BD&q="
        + urllib.parse.quote(text)
    )
    last_err: Exception | None = None
    for attempt in range(retries):
        try:
            req = urllib.request.Request(
                url,
                headers={
                    "User-Agent": (
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
                        "(KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
                    )
                },
            )
            with urllib.request.urlopen(req, timeout=15) as resp:
                data = resp.read()
                if len(data) < 400:
                    raise RuntimeError(f"Short response ({len(data)} bytes)")
                return data
        except Exception as exc:  # noqa: BLE001
            last_err = exc
            time.sleep(0.6 * (attempt + 1))
    raise RuntimeError(f"Failed to synthesize Bangla chunk '{text[:40]}': {last_err}")


def is_valid_mp3(path: Path) -> bool:
    if not path.is_file():
        return False
    size = path.stat().st_size
    if size < MIN_VALID_MP3_BYTES:
        return False
    with path.open("rb") as f:
        head = f.read(4)
    # Valid MP3 starts with ID3 tag or MPEG frame sync (0xFF 0xE0..0xFF)
    return head.startswith(b"ID3") or (len(head) >= 2 and head[0] == 0xFF and (head[1] & 0xE0) == 0xE0)


def synthesize_entry(entry: dict[str, Any], force: bool = False) -> tuple[str, str]:
    target = REPO_ROOT / "app" / "src" / "main" / "assets" / entry["assetPath"]
    target.parent.mkdir(parents=True, exist_ok=True)
    if not force and is_valid_mp3(target):
        return entry["id"], "cached"

    chunks = split_bangla_chunks(entry["scriptBn"])
    tmp_files: list[Path] = []
    try:
        for chunk in chunks:
            raw_mp3 = fetch_google_bn_tts_mp3(chunk)
            fd, tmp_name = tempfile.mkstemp(suffix=".mp3")
            os.close(fd)
            tmp_path = Path(tmp_name)
            tmp_path.write_bytes(raw_mp3)
            tmp_files.append(tmp_path)

        # Normalize to consistent 24kHz mono 48kbps MP3 via ffmpeg for crisp, lightweight child audio
        if len(tmp_files) == 1:
            cmd = [
                "ffmpeg", "-y", "-v", "error",
                "-i", str(tmp_files[0]),
                "-ar", "24000", "-ac", "1", "-b:a", "48k",
                "-af", "loudnorm=I=-16:TP=-1.5:LRA=11",
                str(target),
            ]
            subprocess.run(cmd, check=True)
        else:
            list_fd, list_name = tempfile.mkstemp(suffix=".txt")
            os.close(list_fd)
            list_path = Path(list_name)
            try:
                list_path.write_text(
                    "\n".join(f"file '{p.as_posix()}'" for p in tmp_files),
                    encoding="utf-8",
                )
                cmd = [
                    "ffmpeg", "-y", "-v", "error",
                    "-f", "concat", "-safe", "0", "-i", str(list_path),
                    "-ar", "24000", "-ac", "1", "-b:a", "48k",
                    "-af", "loudnorm=I=-16:TP=-1.5:LRA=11",
                    str(target),
                ]
                subprocess.run(cmd, check=True)
            finally:
                list_path.unlink(missing_ok=True)

        if not is_valid_mp3(target):
            raise RuntimeError(f"Generated file failed MP3 integrity check: {target}")
        return entry["id"], "generated"
    finally:
        for p in tmp_files:
            p.unlink(missing_ok=True)


def write_manifest(entries: list[dict[str, Any]]) -> None:
    AUDIO_ROOT.mkdir(parents=True, exist_ok=True)
    payload = {
        "version": 1,
        "locale": "bn-BD",
        "voiceProfile": "Bangladeshi Bangla Offline Voice Library (24kHz Mono MP3)",
        "totalClips": len(entries),
        "entries": entries,
    }
    MANIFEST_PATH.write_text(
        json.dumps(payload, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )


def verify_all(entries: list[dict[str, Any]]) -> int:
    missing: list[str] = []
    invalid: list[str] = []
    total_bytes = 0
    for entry in entries:
        target = REPO_ROOT / "app" / "src" / "main" / "assets" / entry["assetPath"]
        if not target.is_file():
            missing.append(entry["assetPath"])
        elif not is_valid_mp3(target):
            invalid.append(f"{entry['assetPath']} ({target.stat().st_size} bytes)")
        else:
            total_bytes += target.stat().st_size

    if missing or invalid:
        print(f"VERIFICATION FAILED: {len(missing)} missing, {len(invalid)} invalid", file=sys.stderr)
        for m in missing[:20]:
            print(f"  MISSING: {m}", file=sys.stderr)
        for inv in invalid[:20]:
            print(f"  INVALID: {inv}", file=sys.stderr)
        return 1

    mb = total_bytes / (1024 * 1024)
    print(f"VERIFIED OK: {len(entries)}/{len(entries)} real Bangladeshi Bangla MP3 files ({mb:.2f} MB total)")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate & verify bundled bn-BD MP3 audio for KID'Z WORLD")
    parser.add_argument("--verify-only", action="store_true", help="Only verify manifest & MP3 files without downloading")
    parser.add_argument("--force", action="store_true", help="Regenerate all MP3 files even if cached")
    parser.add_argument("--workers", type=int, default=6, help="Parallel synthesis workers")
    args = parser.parse_args()

    entries = build_manifest_entries()
    ids = [e["id"] for e in entries]
    if len(ids) != len(set(ids)):
        raise SystemExit("ERROR: Duplicate clip IDs found in manifest!")

    if args.verify_only:
        if not MANIFEST_PATH.is_file():
            print("ERROR: manifest.json missing!", file=sys.stderr)
            return 1
        return verify_all(entries)

    write_manifest(entries)
    print(f"Wrote {MANIFEST_PATH.relative_to(REPO_ROOT)} with {len(entries)} Bangladeshi Bangla clips.")

    generated = 0
    cached = 0
    failures: list[str] = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=args.workers) as pool:
        futs = {pool.submit(synthesize_entry, entry, args.force): entry for entry in entries}
        for fut in concurrent.futures.as_completed(futs):
            entry = futs[fut]
            try:
                _, status = fut.result()
                if status == "generated":
                    generated += 1
                else:
                    cached += 1
            except Exception as exc:  # noqa: BLE001
                failures.append(f"{entry['id']}: {exc}")

    print(f"Synthesis summary: {generated} generated, {cached} cached, {len(failures)} failed")
    if failures:
        for f in failures[:15]:
            print("  FAIL:", f, file=sys.stderr)
        return 1

    return verify_all(entries)


if __name__ == "__main__":
    raise SystemExit(main())
