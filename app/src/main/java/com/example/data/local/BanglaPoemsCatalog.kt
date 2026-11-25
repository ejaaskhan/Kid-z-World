package com.example.data.local

import com.example.domain.model.BanglaPoem

/**
 * Curated collection of classic, beloved traditional Bangla children's rhymes (বাংলা লোকছড়া)
 * crafted for clear, rhythmic Bangla voice recitation and early childhood enjoyment (ages 3–8).
 */
object BanglaPoemsCatalog {

    val poems: List<BanglaPoem> = listOf(
        BanglaPoem(
            id = "poem_chand_mama",
            titleBn = "আয় আয় চাঁদ মামা",
            titleEn = "Come, Uncle Moon",
            traditionBn = "প্রচলিত বাংলা ছড়া",
            emoji = "🌙",
            accentColorHex = 0xFF6B5CE7,
            artworkKey = "poem_moonlit_boat",
            stanzasBn = listOf(
                "আয় আয় চাঁদ মামা, টিপ দিয়ে যা।\nচাঁদের কপালে চাঁদ, টিপ দিয়ে যা।",
                "মাছ কাটলে মুড়ো দেবো,\nধান ভানলে কুঁড়ো দেবো।",
                "কালো গরুর দুধ দেবো,\nদুধ খাবার বাটি দেবো।",
                "চাঁদের কপালে চাঁদ,\nটিপ দিয়ে যা।"
            ),
            meaningEn = "A loving traditional lullaby inviting Uncle Moon to place a gentle glowing blessing on the little child's forehead."
        ),
        BanglaPoem(
            id = "poem_hattimatim",
            titleBn = "হাট্টিমাটিম টিম",
            titleEn = "Hattimatim Tim",
            traditionBn = "প্রচলিত বাংলা ছড়া",
            emoji = "🥚",
            accentColorHex = 0xFFFF8F2B,
            artworkKey = "poem_orchard",
            stanzasBn = listOf(
                "হাট্টিমাটিম টিম,\nতারা মাঠে পাড়ে ডিম।",
                "তাদের খাড়া দুটো শিং,\nতারা হাট্টিমাটিম টিম।",
                "খেলছে মাঠে সারাবেলা,\nআনন্দময় ছড়ার মেলা।"
            ),
            meaningEn = "A joyful whimsical rhythm about funny imaginary creatures with upright horns playing happily in the sunny meadow."
        ),
        BanglaPoem(
            id = "poem_ata_gachhe",
            titleBn = "আতা গাছে তোতা পাখি",
            titleEn = "Parrot on the Custard Apple Tree",
            traditionBn = "প্রচলিত বাংলা ছড়া",
            emoji = "🦜",
            accentColorHex = 0xFF2CC877,
            artworkKey = "poem_orchard",
            stanzasBn = listOf(
                "আতা গাছে তোতা পাখি,\nডালিম গাছে মৌ।",
                "এত ডাকি তবু কথা,\nকও না কেন বউ?",
                "গাছের ডালে মিষ্টি গান,\nজুড়িয়ে দেয় সবার প্রাণ।"
            ),
            meaningEn = "A playful classic rhyme painting a vibrant orchard picture with a green parrot on the custard apple tree and sweet honey on the pomegranate tree."
        ),
        BanglaPoem(
            id = "poem_ay_brishti",
            titleBn = "আয় বৃষ্টি ঝেঁপে",
            titleEn = "Come Down, Rain",
            traditionBn = "প্রচলিত বাংলা ছড়া",
            emoji = "🌧️",
            accentColorHex = 0xFF39B5F5,
            artworkKey = "poem_moonlit_boat",
            stanzasBn = listOf(
                "আয় বৃষ্টি ঝেঁপে,\nধান দেবো মেপে।",
                "লেবুর পাতা করমচা,\nযা বৃষ্টি ধরে যা।",
                "টাপুর টুপুর বৃষ্টি পড়ে,\nছোট্ট খুকু হাসে ঘরে।"
            ),
            meaningEn = "A cheerful monsoon rhyme celebrating cool raindrops falling over golden paddy fields and fresh lemon leaves."
        ),
        BanglaPoem(
            id = "poem_tai_tai",
            titleBn = "তাই তাই তাই",
            titleEn = "Clap, Clap, Going to Uncle's House",
            traditionBn = "প্রচলিত বাংলা ছড়া",
            emoji = "👏",
            accentColorHex = 0xFFFF5C8A,
            artworkKey = "poem_rabbit",
            stanzasBn = listOf(
                "তাই তাই তাই,\nমামা বাড়ি যাই।",
                "মামি দিলো দুধ-ভাত,\nদুয়ারে বসে খাই।",
                "মামা এলো লাঠি হাতে,\nহাসি খুশি সবাই সাথে।"
            ),
            meaningEn = "A clapping rhyme toddlers love to act out with their hands while imagining a joyful visit to their uncle's warm home."
        ),
        BanglaPoem(
            id = "poem_noton_payra",
            titleBn = "নোটন নোটন পায়রাগুলি",
            titleEn = "Crested Little Pigeons",
            traditionBn = "প্রচলিত বাংলা ছড়া",
            emoji = "🕊️",
            accentColorHex = 0xFF8B62F7,
            artworkKey = "poem_moonlit_boat",
            stanzasBn = listOf(
                "নোটন নোটন পায়রাগুলি,\nঝোঁটন বেঁধেছে।",
                "বড় সাহেবের বিবিগুলি,\nনাইতে এসেছে।",
                "দুই পারে দুই রুই কাতলা,\nভেসে উঠেছে।",
                "কে দেখেছে কে দেখেছে,\nদাদা দেখেছে।"
            ),
            meaningEn = "A musical riverside rhyme describing crested pigeons fluttering playfully near bright fish splashing in the river."
        ),
        BanglaPoem(
            id = "poem_khokon_khokon",
            titleBn = "খোকন খোকন ডাক পাড়ি",
            titleEn = "Calling Little Khokon",
            traditionBn = "প্রচলিত বাংলা ছড়া",
            emoji = "🚣",
            accentColorHex = 0xFFFFB627,
            artworkKey = "poem_elephant",
            stanzasBn = listOf(
                "খোকন খোকন ডাক পাড়ি,\nখোকন মোদের কার বাড়ি?",
                "আয় রে খোকন ঘরে আয়,\nদুধ-মাখা ভাত কাকে খায়।",
                "খোকন যাবে নায়ে,\nলাল জুতো পায়ে।"
            ),
            meaningEn = "An affectionate rhyme calling a playful little child home for dinner and imagining a joyful boat ride wearing shiny red shoes."
        ),
        BanglaPoem(
            id = "poem_aam_pata",
            titleBn = "আম পাতা জোড়া জোড়া",
            titleEn = "Mango Leaves in Pairs",
            traditionBn = "প্রচলিত বাংলা ছড়া",
            emoji = "🥭",
            accentColorHex = 0xFF20B89E,
            artworkKey = "poem_tiger",
            stanzasBn = listOf(
                "আম পাতা জোড়া জোড়া,\nমারবো চাবুক চড়বো ঘোড়া।",
                "ওরে বুবু সরে দাঁড়া,\nআসছে আমার পাগলা ঘোড়া।",
                "পাগলা ঘোড়া ক্ষেপেছে,\nআনন্দে মন মেতেছে।"
            ),
            meaningEn = "An energetic pretend-play rhyme where kids gallop like a playful pony beneath lush green mango trees."
        )
    )
}
