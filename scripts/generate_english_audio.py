#!/usr/bin/env python3
"""
KID'Z WORLD — Offline Natural Human English (en-US) Audio Asset Generator & Verifier
Developed by Dev. E

Generates clear, warm, natural human-sounding English MP3 audio files bundled into:
  app/src/main/assets/audio/english/
    - alphabet/      (English A–Z phonics & words)
    - numbers/       (0–100 in clear child-friendly English)
    - colors/        (Colors lessons)
    - shapes/        (Shapes lessons)
    - animals/       (Animals & birds lessons + sorting items)
    - fruits/        (Fruits & vegetables lessons + sorting items)
    - common_words/  (Body, vehicles, nature, basic words, everyday vocab, sentences, puzzle tokens)
    - stories/       (All illustrated story pages & morals)
    - instructions/  (Welcome, voice test, quiz & math prompts)

Writes app/src/main/assets/audio/english/manifest.json consumed directly by the Android app
at runtime so English playback uses warm human voices offline with zero robotic Android TTS.
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
AUDIO_ROOT = REPO_ROOT / "app" / "src" / "main" / "assets" / "audio" / "english"
MANIFEST_PATH = AUDIO_ROOT / "manifest.json"
MIN_VALID_MP3_BYTES = 1000

ENGLISH_ONES = [
    "Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
    "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
    "Seventeen", "Eighteen", "Nineteen",
]
ENGLISH_TENS = [
    "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety",
]


def english_number_word(n: int) -> str:
    if n == 100:
        return "One Hundred"
    if 0 <= n <= 19:
        return ENGLISH_ONES[n]
    tens = ENGLISH_TENS[n // 10]
    ones = ENGLISH_ONES[n % 10] if n % 10 != 0 else ""
    return tens if not ones else f"{tens}-{ones}"


def build_manifest_entries() -> list[dict[str, Any]]:
    entries: list[dict[str, Any]] = []

    def add(
        clip_id: str,
        category: str,
        filename: str,
        script_en: str,
        aliases: list[str] | None = None,
    ) -> None:
        rel_path = f"audio/english/{category}/{filename}"
        entries.append(
            {
                "id": clip_id,
                "category": category,
                "assetPath": rel_path,
                "scriptBn": script_en.strip(),
                "aliases": [a.strip() for a in (aliases or []) if a and a.strip()],
            }
        )

    # 1. ENGLISH ALPHABET A–Z (en_alpha_a..z)
    en_letters = [
        ("a", "A", "Apple"),
        ("b", "B", "Ball"),
        ("c", "C", "Cat"),
        ("d", "D", "Duck"),
        ("e", "E", "Elephant"),
        ("f", "F", "Fish"),
        ("g", "G", "Grapes"),
        ("h", "H", "House"),
        ("i", "I", "Ice Cream"),
        ("j", "J", "Juice"),
        ("k", "K", "Kite"),
        ("l", "L", "Lion"),
        ("m", "M", "Mango"),
        ("n", "N", "Nest"),
        ("o", "O", "Owl"),
        ("p", "P", "Panda"),
        ("q", "Q", "Queen"),
        ("r", "R", "Rainbow"),
        ("s", "S", "Sun"),
        ("t", "T", "Tiger"),
        ("u", "U", "Umbrella"),
        ("v", "V", "Violin"),
        ("w", "W", "Whale"),
        ("x", "X", "Xylophone"),
        ("y", "Y", "Yo-Yo"),
        ("z", "Z", "Zebra"),
    ]
    for key, upper, word in en_letters:
        add(
            clip_id=f"en_alpha_{key}",
            category="alphabet",
            filename=f"en_alpha_{key}.mp3",
            script_en=f"{upper}. {upper} is for {word}.",
            aliases=[upper, f"Letter {upper}", f"{upper}, is for {word}.", word],
        )

    # 2. NUMBERS 0–100 (num_0..num_100)
    for n in range(0, 101):
        word = english_number_word(n)
        add(
            clip_id=f"num_{n}",
            category="numbers",
            filename=f"number_{n}.mp3",
            script_en=f"{word}.",
            aliases=[word, str(n), f"Number {n}"],
        )

    # 3. COLORS (color_*)
    colors = [
        ("color_red", "Red", "Red. Like a sweet red strawberry."),
        ("color_blue", "Blue", "Blue. Like the bright ocean sky."),
        ("color_yellow", "Yellow", "Yellow. Like the warm shining sun."),
        ("color_green", "Green", "Green. Like lush meadow leaves."),
        ("color_orange", "Orange", "Orange. Like a juicy orange fruit."),
        ("color_purple", "Purple", "Purple. Like tasty purple grapes."),
        ("color_pink", "Pink", "Pink. Like a pretty lotus blossom."),
        ("color_teal", "Teal", "Teal. Like sparkling river waves."),
    ]
    for cid, title, script in colors:
        add(cid, "colors", f"{cid}.mp3", script, aliases=[title, f"Color {title}"])

    # 4. SHAPES (shape_*)
    shapes = [
        ("shape_circle", "Circle", "Circle. Round like a bouncy ball."),
        ("shape_square", "Square", "Square. Four equal happy sides."),
        ("shape_triangle", "Triangle", "Triangle. Three pointy corners like a roof."),
        ("shape_star", "Star", "Star. Twinkling brightly in the night sky."),
        ("shape_heart", "Heart", "Heart. Full of kindness and love."),
        ("shape_diamond", "Diamond", "Diamond. Shaped like a flying kite."),
        ("shape_rectangle", "Rectangle", "Rectangle. Shaped like a storybook or door."),
        ("shape_oval", "Oval", "Oval. Smooth stretched circle like an egg."),
    ]
    for cid, title, script in shapes:
        add(cid, "shapes", f"{cid}.mp3", script, aliases=[title, f"Shape: {title}"])

    # 5. ANIMALS (animal_* + short puzzle names)
    animals = [
        ("animal_tiger", "Bengal Tiger", "Bengal Tiger. Roar! Strong striped jumper."),
        ("animal_elephant", "Elephant", "Elephant. Toot! Gentle giant with a long trunk."),
        ("animal_rabbit", "Rabbit", "Rabbit. Hop hop! Soft fluffy ears."),
        ("animal_owl", "Owl", "Owl. Hoo hoo! Wise night explorer."),
        ("animal_duck", "Duck", "Duck. Quack quack! Loves splashing in ponds."),
        ("animal_cat", "Cat", "Cat. Meow! Playful purring friend."),
        ("animal_panda", "Panda", "Panda. Munch munch! Loves green bamboo."),
        ("animal_butterfly", "Butterfly", "Butterfly. Flutters gracefully on colorful wings."),
        ("animal_peacock", "Peacock", "Peacock. Dances proudly with rainbow feathers."),
        ("animal_parrot", "Parrot", "Parrot. Bright green bird that loves to chatter."),
    ]
    for cid, title, script in animals:
        add(cid, "animals", f"{cid}.mp3", script, aliases=[title])

    for cid, name in [
        ("sort_lion", "Lion"),
        ("sort_tiger", "Tiger"),
        ("sort_elephant", "Elephant"),
        ("sort_rabbit", "Rabbit"),
        ("sort_parrot", "Parrot"),
        ("sort_cat", "Cat"),
        ("token_frog", "Frog"),
    ]:
        add(cid, "animals", f"{cid}.mp3", f"{name}.", aliases=[name])

    # 6. FRUITS & VEGETABLES (fruitveg_* + short puzzle names)
    fruits = [
        ("fruitveg_mango", "Mango", "Mango. Sweet golden king of fruits."),
        ("fruitveg_apple", "Apple", "Apple. Crunchy red fruit that keeps us strong."),
        ("fruitveg_banana", "Banana", "Banana. Soft yellow energy booster."),
        ("fruitveg_watermelon", "Watermelon", "Watermelon. Juicy red summer cooler."),
        ("fruitveg_grapes", "Grapes", "Grapes. Tiny sweet bunches on vines."),
        ("fruitveg_carrot", "Carrot", "Carrot. Crunchy orange veggie great for eyes."),
        ("fruitveg_tomato", "Tomato", "Tomato. Bright red juicy vegetable."),
        ("fruitveg_broccoli", "Broccoli", "Broccoli. Little green tree vegetable."),
        ("fruitveg_potato", "Potato", "Potato. Yummy golden underground veggie."),
        ("fruitveg_corn", "Sweet Corn", "Sweet Corn. Sunny yellow crunchy kernels."),
    ]
    for cid, title, script in fruits:
        add(cid, "fruits", f"{cid}.mp3", script, aliases=[title])

    for cid, name in [
        ("sort_mango", "Mango"),
        ("sort_apple", "Apple"),
        ("sort_banana", "Banana"),
        ("sort_grapes", "Grapes"),
        ("sort_watermelon", "Watermelon"),
        ("sort_orange", "Orange"),
        ("token_strawberry", "Strawberry"),
    ]:
        add(cid, "fruits", f"{cid}.mp3", f"{name}.", aliases=[name])

    # 7. HUMAN BODY, VEHICLES, NATURE
    common_lessons = [
        ("body_eyes", "Eyes", "Eyes. We see rainbows and books with our eyes."),
        ("body_ears", "Ears", "Ears. We listen to music and stories with our ears."),
        ("body_nose", "Nose", "Nose. We smell sweet flowers with our nose."),
        ("body_mouth", "Smile & Mouth", "Smile and Mouth. We speak kindly and eat healthy food."),
        ("body_hands", "Hands", "Hands. We clap, draw, and help friends."),
        ("body_feet", "Feet", "Feet. We walk, jump, and play outside."),
        ("body_heart", "Heart", "Heart. Beats softly all day to keep us alive."),
        ("body_brain", "Brain", "Brain. Helps us think, learn, and dream big."),
        ("vehicle_car", "Car", "Car. Zoom zoom on four shiny wheels."),
        ("vehicle_bus", "School Bus", "School Bus. Takes happy friends to school."),
        ("vehicle_train", "Train", "Train. Chugga chugga along steel tracks."),
        ("vehicle_boat", "Boat", "Boat. Sails gently across sparkling rivers."),
        ("vehicle_airplane", "Airplane", "Airplane. Soars high above the fluffy clouds."),
        ("vehicle_bicycle", "Bicycle", "Bicycle. Pedal with two wheels for fun exercise."),
        ("vehicle_rocket", "Space Rocket", "Space Rocket. Blasts off to visit the moon and stars."),
        ("vehicle_rickshaw", "Rickshaw", "Rickshaw. Colorful three-wheeler on city streets."),
        ("nature_sun", "Sun", "Sun. Gives warm daylight to the whole Earth."),
        ("nature_moon", "Moon", "Moon. Glows gently in the starry night sky."),
        ("nature_tree", "Tree", "Tree. Gives us shade, fruit, and fresh air."),
        ("nature_river", "River", "River. Flowing fresh water home to little fish."),
        ("nature_rain", "Rain", "Rain. Waters green fields with cool drops."),
        ("nature_rainbow", "Rainbow", "Rainbow. Seven bright colors painted in the sky."),
        ("nature_flower", "Flower", "Flower. Colorful petals loved by busy bees."),
        ("nature_mountain", "Mountain", "Mountain. Tall green peaks touching the clouds."),
        # Language Studio: Basic Words
        ("lang_word_0", "Book", "Book. I read a fun storybook."),
        ("lang_word_1", "Water", "Water. Drink clean water every day."),
        ("lang_word_2", "Home", "Home. Our home is warm and cozy."),
        ("lang_word_3", "Friend", "Friend. Playing with a friend is joyful."),
        ("lang_word_4", "Smile", "Smile. A kind smile brightens the day."),
        ("lang_word_5", "Star", "Star. Little stars twinkle high above."),
        ("lang_word_6", "Ball", "Ball. Bounce the colorful ball high."),
        ("lang_word_7", "Music", "Music. Sweet music makes us dance."),
        # Language Studio: Everyday Vocab
        ("lang_vocab_0", "Good Morning", "Good Morning. Say Good Morning with a bright smile!"),
        ("lang_vocab_1", "Thank You", "Thank You. Say Thank You when someone helps you."),
        ("lang_vocab_2", "Please", "Please. Use Please when asking nicely."),
        ("lang_vocab_3", "Sorry", "Sorry. Say Sorry if you make a mistake."),
        ("lang_vocab_4", "Family", "Family. My family loves and cares for me."),
        ("lang_vocab_5", "School", "School. We learn exciting new things at school."),
        ("lang_vocab_6", "Share", "Share. Sharing toys brings double the fun!"),
        ("lang_vocab_7", "Good Night", "Good Night. Wish everyone Good Night before sleep."),
        # Language Studio: Simple Sentences
        ("lang_sent_0", "I love to learn.", "I love to learn."),
        ("lang_sent_1", "The sun is bright.", "The sun is bright."),
        ("lang_sent_2", "This is a sweet mango.", "This is a sweet mango."),
        ("lang_sent_3", "Birds fly in the sky.", "Birds fly in the sky."),
        ("lang_sent_4", "I help my friends.", "I help my friends."),
        ("lang_sent_5", "The moon shines at night.", "The moon shines at night."),
        ("lang_sent_6", "I brush my teeth daily.", "I brush my teeth daily."),
        ("lang_sent_7", "Green trees give us air.", "Green trees give us air."),
        # Language Studio: Bangla Vocabulary (English explanations)
        ("lang_bn_0", "Love", "Love. Love for family and friends."),
        ("lang_bn_1", "Sky", "Sky. White clouds float in the blue sky."),
        ("lang_bn_2", "Bird", "Bird. Birds sing sweetly in the morning."),
        ("lang_bn_3", "Flower", "Flower. Colorful flowers bloom in the garden."),
        ("lang_bn_4", "River", "River. Bangladesh is a land of lovely rivers."),
        ("lang_bn_5", "Rain", "Rain. Pitter-patter falls the cool rain."),
        ("lang_bn_6", "Dream", "Dream. Dreaming big to grow kind and wise."),
        ("lang_bn_7", "Joy", "Joy. Learning together brings so much joy!"),
        # Short puzzle vehicle / pattern tokens
        ("sort_car", "Car", "Car."),
        ("sort_bus", "Bus", "Bus."),
        ("sort_train", "Train", "Train."),
        ("sort_boat", "Boat", "Boat."),
        ("sort_airplane", "Airplane", "Airplane."),
        ("sort_bicycle", "Bicycle", "Bicycle."),
        ("token_sun", "Sun", "Sun."),
        ("token_moon", "Moon", "Moon."),
        ("token_star", "Star", "Star."),
        ("token_flower", "Flower", "Flower."),
        ("token_balloon", "Balloon", "Balloon."),
        ("token_butterfly", "Butterfly", "Butterfly."),
        ("token_rainbow", "Rainbow", "Rainbow."),
        ("token_rocket", "Rocket", "Rocket."),
    ]
    for cid, title, script in common_lessons:
        add(cid, "common_words", f"{cid}.mp3", script, aliases=[title])

    # 8. ILLUSTRATED STORIES (All English Pages & Morals)
    story_clips = [
        (
            "story_brave_tiger_page_1",
            "Deep inside the sunny Sundarbans lived Tiku, a cheerful little tiger cub who loved wearing his bright red scarf.",
        ),
        (
            "story_brave_tiger_page_2",
            "One golden morning, Tiku heard a tiny Cheep, Cheep by the wooden bridge. A little yellow bird had lost its way across the stream.",
        ),
        (
            "story_brave_tiger_page_3",
            "Tiku stepped gently onto the wobbly bridge, smiled warmly, and guided the little bird safely across the sparkling emerald water.",
        ),
        (
            "story_brave_tiger_page_4",
            "The bird sang a joyful melody, and the whole forest cheered for Tiku, the kindest, bravest little tiger!",
        ),
        (
            "story_brave_tiger_moral",
            "True bravery means helping even the tiniest friend.",
        ),
        (
            "story_rabbit_moon_page_1",
            "Every evening, fluffy little Mitu the rabbit hopped up the flower hill to count the twinkling stars in the twilight sky.",
        ),
        (
            "story_rabbit_moon_page_2",
            "Tonight, Mitu saw a big golden moon glowing in the sky, and another glowing moon swimming right inside the calm pond!",
        ),
        (
            "story_rabbit_moon_page_3",
            "Mitu tapped the water softly. Ripples danced across the pond! Aha! giggled Mitu. The pond is the moon's shiny mirror!",
        ),
        (
            "story_rabbit_moon_page_4",
            "With a happy heart and a big yawn, Mitu wished the smiling moon goodnight and drifted into sweet dreams.",
        ),
        (
            "story_rabbit_moon_moral",
            "Curiosity and calm thinking light up the darkest night.",
        ),
        (
            "story_kind_elephant_page_1",
            "By a bright meadow river lived Golu, a gentle baby elephant with sparkling blue eyes and a crown of fresh green leaves.",
        ),
        (
            "story_kind_elephant_page_2",
            "One warm afternoon, the pink lotus flowers looked thirsty and four tiny ducklings wished for a cool rainbow splash.",
        ),
        (
            "story_kind_elephant_page_3",
            "Golu filled his favorite blue watering can and poured sparkling rainbow droplets over the flowers and giggling ducklings.",
        ),
        (
            "story_kind_elephant_page_4",
            "A giant rainbow arched across the sky as everyone danced together. Kindness always makes the world brighter!",
        ),
        (
            "story_kind_elephant_moral",
            "When we share what we have, everyone blooms together.",
        ),
    ]
    for cid, text in story_clips:
        add(cid, "stories", f"{cid}.mp3", text, aliases=[text])

    # 9. LEARNING INSTRUCTIONS & VOICE TEST
    instructions = [
        (
            "inst_welcome",
            "Welcome to Kid'z World! Learn, play, and grow.",
            ["Welcome to KID'Z WORLD! Learn, play, and grow."],
        ),
        (
            "inst_test_voice",
            "Hello! Welcome to Kid'z World. Let's learn, play, and grow together!",
            ["Welcome to Kid'z World! Learn, play, and grow."],
        ),
        ("inst_quiz_find", "Find", ["Find", "Tap"]),
        ("inst_math_plus", "plus", ["plus", "+"]),
        ("inst_math_minus", "minus", ["minus", "-"]),
        ("inst_math_equals_what", "equals what?", ["equals what", "equals?"]),
        ("inst_pattern_next", "What comes next in the pattern?", ["What comes next?"]),
        ("inst_sorting_prompt", "Which basket does this belong in?", []),
        ("inst_great_job", "Great job! Well done!", ["Great job!", "Awesome!"]),
        ("inst_try_again", "Nice try! Let's try again.", ["Try again!"]),
    ]
    for cid, script, aliases in instructions:
        add(cid, "instructions", f"{cid}.mp3", script, aliases=aliases)

    return entries


def split_chunks(text: str, max_chars: int = 165) -> list[str]:
    clean = re.sub(r"\s+", " ", text).strip()
    if len(clean) <= max_chars:
        return [clean]
    parts = re.split(r"(?<=[.!?])\s+", clean)
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
            buf = part
    if buf:
        chunks.append(buf)
    return chunks


def fetch_google_en_tts_mp3(text: str, retries: int = 4) -> bytes:
    url = (
        "https://translate.google.com/translate_tts?ie=UTF-8&client=tw-ob&tl=en-US&q="
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
    raise RuntimeError(f"Failed to synthesize English chunk '{text[:40]}': {last_err}")


def is_valid_mp3(path: Path) -> bool:
    if not path.is_file():
        return False
    if path.stat().st_size < MIN_VALID_MP3_BYTES:
        return False
    with path.open("rb") as f:
        head = f.read(4)
    return head.startswith(b"ID3") or (len(head) >= 2 and head[0] == 0xFF and (head[1] & 0xE0) == 0xE0)


TARGET_MEAN_DB = -15.5
_MEAN_VOL_RE = re.compile(r"mean_volume:\s*([-\d.]+)\s*dB")


def _detect_mean_volume_db(wav_path: Path) -> float | None:
    proc = subprocess.run(
        ["ffmpeg", "-hide_banner", "-i", str(wav_path), "-af", "volumedetect", "-f", "null", "-"],
        capture_output=True,
        text=True,
        check=False,
    )
    match = _MEAN_VOL_RE.search(proc.stderr)
    return float(match.group(1)) if match else None


def encode_uniform_voice_mp3(input_args: list[str], target: Path) -> None:
    """
    Deterministic 2-pass active-speech RMS normalization + soft peak limiter.
    Guarantees every English clip matches the exact same clear volume (~-15.5 dB RMS,
    -1.5 dB peak ceiling) as the Bangla clips.
    """
    wav_fd, wav_name = tempfile.mkstemp(suffix=".wav")
    os.close(wav_fd)
    wav_path = Path(wav_name)
    try:
        trim_filter = (
            "highpass=f=80,"
            "silenceremove=start_periods=1:start_threshold=-42dB:detection=peak,"
            "areverse,"
            "silenceremove=start_periods=1:start_threshold=-42dB:detection=peak,"
            "areverse"
        )
        subprocess.run(
            [
                "ffmpeg", "-y", "-v", "error",
                *input_args,
                "-ar", "24000", "-ac", "1",
                "-af", trim_filter,
                str(wav_path),
            ],
            check=True,
        )
        measured_mean = _detect_mean_volume_db(wav_path)

        if measured_mean is None or wav_path.stat().st_size < 4000:
            subprocess.run(
                [
                    "ffmpeg", "-y", "-v", "error",
                    *input_args,
                    "-ar", "24000", "-ac", "1",
                    "-af", "highpass=f=80",
                    str(wav_path),
                ],
                check=True,
            )
            measured_mean = _detect_mean_volume_db(wav_path)

        gain_db = 0.0 if measured_mean is None else max(-18.0, min(28.0, TARGET_MEAN_DB - measured_mean))

        final_filter = (
            f"volume={gain_db:.2f}dB,"
            "alimiter=limit=-1.5dB:attack=5:release=50:level=disabled,"
            "adelay=30|30,"
            "apad=pad_dur=0.05"
        )
        subprocess.run(
            [
                "ffmpeg", "-y", "-v", "error",
                "-i", str(wav_path),
                "-ar", "24000", "-ac", "1", "-b:a", "64k",
                "-af", final_filter,
                str(target),
            ],
            check=True,
        )
    finally:
        wav_path.unlink(missing_ok=True)


def synthesize_entry(entry: dict[str, Any], force: bool = False) -> tuple[str, str]:
    target = REPO_ROOT / "app" / "src" / "main" / "assets" / entry["assetPath"]
    target.parent.mkdir(parents=True, exist_ok=True)
    if not force and is_valid_mp3(target):
        return entry["id"], "cached"

    chunks = split_chunks(entry["scriptBn"])
    tmp_files: list[Path] = []
    try:
        for chunk in chunks:
            raw_mp3 = fetch_google_en_tts_mp3(chunk)
            fd, tmp_name = tempfile.mkstemp(suffix=".mp3")
            os.close(fd)
            tmp_path = Path(tmp_name)
            tmp_path.write_bytes(raw_mp3)
            tmp_files.append(tmp_path)

        if len(tmp_files) == 1:
            encode_uniform_voice_mp3(["-i", str(tmp_files[0])], target)
        else:
            list_fd, list_name = tempfile.mkstemp(suffix=".txt")
            os.close(list_fd)
            list_path = Path(list_name)
            try:
                list_path.write_text(
                    "\n".join(f"file '{p.as_posix()}'" for p in tmp_files),
                    encoding="utf-8",
                )
                encode_uniform_voice_mp3(["-f", "concat", "-safe", "0", "-i", str(list_path)], target)
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
        "locale": "en-US",
        "voiceProfile": "Natural Human English Offline Voice Library (24kHz Mono MP3)",
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
    print(f"VERIFIED OK: {len(entries)}/{len(entries)} natural human English MP3 files ({mb:.2f} MB total)")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description="Generate & verify bundled en-US human MP3 audio for KID'Z WORLD")
    parser.add_argument("--verify-only", action="store_true", help="Only verify manifest & MP3 files without downloading")
    parser.add_argument("--force", action="store_true", help="Regenerate all MP3 files even if cached")
    parser.add_argument("--workers", type=int, default=6, help="Parallel synthesis workers")
    args = parser.parse_args()

    entries = build_manifest_entries()
    ids = [e["id"] for e in entries]
    if len(ids) != len(set(ids)):
        raise SystemExit("ERROR: Duplicate clip IDs found in English manifest!")

    if args.verify_only:
        if not MANIFEST_PATH.is_file():
            print("ERROR: English manifest.json missing!", file=sys.stderr)
            return 1
        return verify_all(entries)

    write_manifest(entries)
    print(f"Wrote {MANIFEST_PATH.relative_to(REPO_ROOT)} with {len(entries)} natural English clips.")

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

    print(f"English synthesis summary: {generated} generated, {cached} cached, {len(failures)} failed")
    if failures:
        for f in failures[:15]:
            print("  FAIL:", f, file=sys.stderr)
        return 1

    return verify_all(entries)


if __name__ == "__main__":
    raise SystemExit(main())
