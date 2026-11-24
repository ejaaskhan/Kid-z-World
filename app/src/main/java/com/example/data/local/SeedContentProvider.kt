package com.example.data.local

import com.example.data.local.entity.LessonItemEntity
import com.example.data.local.entity.StoryEntity
import com.example.data.local.entity.StoryPageEntity
import com.example.domain.model.ModuleType

object SeedContentProvider {

    private val palette = listOf(
        0xFF2BB3FFL, // Sky Cerulean
        0xFFFF8C1AL, // Sunny Mango
        0xFF2AD17BL, // Emerald Meadow
        0xFFFF5C8AL, // Berry Coral
        0xFF8E5CFFL, // Grape Pop
        0xFFFFB800L  // Honey Gold
    )

    fun buildAllLessons(): List<LessonItemEntity> {
        return buildEnglishAlphabets() +
            buildBanglaAlphabets() +
            SeedNumbersAndLanguage.buildNumbers1To100() +
            buildColors() +
            buildShapes() +
            buildAnimals() +
            SeedNumbersAndLanguage.buildWorldExtraModules() +
            SeedNumbersAndLanguage.buildLanguageModules()
    }

    val totalExpectedLessonCount: Int
        get() = buildAllLessons().size

    private fun buildEnglishAlphabets(): List<LessonItemEntity> {
        val items = listOf(
            Triple("A", "Apple", "আপেল") to "🍎",
            Triple("B", "Ball", "বল") to "⚽",
            Triple("C", "Cat", "বিড়াল") to "🐱",
            Triple("D", "Duck", "হাঁস") to "🦆",
            Triple("E", "Elephant", "হাতি") to "🐘",
            Triple("F", "Fish", "মাছ") to "🐠",
            Triple("G", "Grapes", "আঙুর") to "🍇",
            Triple("H", "House", "বাড়ি") to "🏡",
            Triple("I", "Ice Cream", "আইসক্রিম") to "🍦",
            Triple("J", "Juice", "জুস") to "🧃",
            Triple("K", "Kite", "ঘুড়ি") to "🪁",
            Triple("L", "Lion", "সিংহ") to "🦁",
            Triple("M", "Mango", "আম") to "🥭",
            Triple("N", "Nest", "পাখির বাসা") to "🪺",
            Triple("O", "Owl", "পেঁচা") to "🦉",
            Triple("P", "Panda", "পান্ডা") to "🐼",
            Triple("Q", "Queen", "রানি") to "👑",
            Triple("R", "Rainbow", "রংধনু") to "🌈",
            Triple("S", "Sun", "সূর্য") to "☀️",
            Triple("T", "Tiger", "বাঘ") to "🐯",
            Triple("U", "Umbrella", "ছাতা") to "☂️",
            Triple("V", "Violin", "বেহালা") to "🎻",
            Triple("W", "Whale", "তিমি") to "🐳",
            Triple("X", "Xylophone", "জাইলোফোন") to "🎶",
            Triple("Y", "Yo-Yo", "ইয়ো-ইয়ো") to "🪀",
            Triple("Z", "Zebra", "জেব্রা") to "🦓"
        )
        return items.mapIndexed { index, (triple, emoji) ->
            LessonItemEntity(
                id = "en_alpha_${triple.first.lowercase()}",
                moduleType = ModuleType.ALPHABET_EN.key,
                symbol = triple.first,
                symbolBn = triple.first,
                titleEn = "Letter ${triple.first}",
                titleBn = "অক্ষর ${triple.first}",
                phoneticEn = "${triple.first} says /${triple.first.lowercase()}/",
                phoneticBn = "${triple.first} — ${triple.third}",
                exampleEn = triple.second,
                exampleBn = triple.third,
                visualBadge = emoji,
                accentColorHex = palette[index % palette.size],
                sortOrder = index
            )
        }
    }

    private fun buildBanglaAlphabets(): List<LessonItemEntity> {
        // Complete 11 Sworoborno (অ–ঔ) + 15 Banjonborno starter (ক–ণ)
        data class BnLetter(
            val glyph: String,
            val roman: String,
            val wordBn: String,
            val wordEn: String,
            val emoji: String
        )
        val letters = listOf(
            BnLetter("অ", "Aw", "অজগর", "Python", "🐍"),
            BnLetter("আ", "Aa", "আম", "Mango", "🥭"),
            BnLetter("ই", "Hroshwo-i", "ইঁদুর", "Mouse", "🐭"),
            BnLetter("ঈ", "Dirgho-i", "ঈগল", "Eagle", "🦅"),
            BnLetter("উ", "Hroshwo-u", "উট", "Camel", "🐪"),
            BnLetter("ঊ", "Dirgho-u", "ঊষা", "Dawn", "🌅"),
            BnLetter("ঋ", "Ri", "ঋতু", "Seasons", "🌸"),
            BnLetter("এ", "E", "একতারা", "Ektara", "🎵"),
            BnLetter("ঐ", "Oi", "ঐরাবত", "Elephant", "🐘"),
            BnLetter("ও", "O", "ওল", "Yam", "🌱"),
            BnLetter("ঔ", "Ou", "ঔষধ", "Medicine", "💊"),
            BnLetter("ক", "Ko", "কলা", "Banana", "🍌"),
            BnLetter("খ", "Kho", "খরগোশ", "Rabbit", "🐰"),
            BnLetter("গ", "Go", "গরু", "Cow", "🐄"),
            BnLetter("ঘ", "Gho", "ঘুড়ি", "Kite", "🪁"),
            BnLetter("ঙ", "Umo", "ব্যাঙ", "Frog", "🐸"),
            BnLetter("চ", "Cho", "চাঁদ", "Moon", "🌙"),
            BnLetter("ছ", "Chho", "ছাতা", "Umbrella", "☂️"),
            BnLetter("জ", "Jo", "জাহাজ", "Ship", "🚢"),
            BnLetter("ঝ", "Jho", "ঝিনুক", "Seashell", "🐚"),
            BnLetter("ট", "To", "টিয়া", "Parrot", "🦜"),
            BnLetter("ঠ", "Tho", "ঠেলাগাড়ি", "Cart", "🛒"),
            BnLetter("ড", "Do", "ডাব", "Coconut", "🥥"),
            BnLetter("ত", "To", "তারা", "Star", "⭐"),
            BnLetter("ন", "No", "নৌকা", "Boat", "⛵")
        )
        return letters.mapIndexed { index, item ->
            LessonItemEntity(
                id = "bn_alpha_$index",
                moduleType = ModuleType.ALPHABET_BN.key,
                symbol = item.roman,
                symbolBn = item.glyph,
                titleEn = "${item.glyph} (${item.roman})",
                titleBn = "বর্ণ ${item.glyph}",
                phoneticEn = "Sounds like ${item.roman}",
                phoneticBn = "${item.glyph} তে ${item.wordBn}",
                exampleEn = item.wordEn,
                exampleBn = item.wordBn,
                visualBadge = item.emoji,
                accentColorHex = palette[(index + 1) % palette.size],
                sortOrder = index
            )
        }
    }

    private fun buildColors(): List<LessonItemEntity> {
        data class ColorSeed(
            val id: String,
            val en: String,
            val bn: String,
            val exEn: String,
            val exBn: String,
            val badge: String,
            val hex: Long
        )
        val colors = listOf(
            ColorSeed("red", "Red", "লাল", "Like a sweet red strawberry", "মিষ্টি লাল স্ট্রবেরির মতো", "🍓", 0xFFFF4D5AL),
            ColorSeed("blue", "Blue", "নীল", "Like the bright ocean sky", "উজ্জ্বল নীল আকাশের মতো", "🐳", 0xFF2B9BFFL),
            ColorSeed("yellow", "Yellow", "হলুদ", "Like the warm shining sun", "উষ্ণ হলুদ সূর্যের মতো", "🌻", 0xFFFFC107L),
            ColorSeed("green", "Green", "সবুজ", "Like lush meadow leaves", "সবুজ গাছের পাতার মতো", "🍀", 0xFF28C76FL),
            ColorSeed("orange", "Orange", "কমলা", "Like a juicy orange fruit", "রসালো কমলার মতো", "🍊", 0xFFFF8C1AL),
            ColorSeed("purple", "Purple", "বেগুনি", "Like tasty purple grapes", "মিষ্টি বেগুনি আঙুরের মতো", "🍇", 0xFF8E5CFFL),
            ColorSeed("pink", "Pink", "গোলাপি", "Like a pretty lotus blossom", "সুন্দর গোলাপি পদ্মের মতো", "🌸", 0xFFFF66A3L),
            ColorSeed("teal", "Teal", "ফিরোজা", "Like sparkling river waves", "ঝিলমিল নদীর ঢেউয়ের মতো", "🦚", 0xFF1CC5B7L)
        )
        return colors.mapIndexed { idx, c ->
            LessonItemEntity(
                id = "color_${c.id}",
                moduleType = ModuleType.COLORS.key,
                symbol = c.en,
                symbolBn = c.bn,
                titleEn = c.en,
                titleBn = c.bn,
                phoneticEn = "Color ${c.en}",
                phoneticBn = "রং ${c.bn}",
                exampleEn = c.exEn,
                exampleBn = c.exBn,
                visualBadge = c.badge,
                accentColorHex = c.hex,
                sortOrder = idx
            )
        }
    }

    private fun buildShapes(): List<LessonItemEntity> {
        data class ShapeSeed(
            val id: String,
            val en: String,
            val bn: String,
            val exEn: String,
            val exBn: String,
            val badge: String,
            val hex: Long
        )
        val shapes = listOf(
            ShapeSeed("circle", "Circle", "বৃত্ত", "Round like a bouncy ball", "গোল বলের মতো বৃত্ত", "🟠", 0xFFFF8C1AL),
            ShapeSeed("square", "Square", "বর্গক্ষেত্র", "Four equal happy sides", "চারটি সমান বাহু", "🟦", 0xFF2BB3FFL),
            ShapeSeed("triangle", "Triangle", "ত্রিভুজ", "Three pointy corners like a roof", "তিনটি কোণা যেমন পাহাড়", "🔺", 0xFFFF5C8AL),
            ShapeSeed("star", "Star", "তারা", "Twinkling brightly in the night sky", "রাতের আকাশে ঝিকিমিকি তারা", "⭐", 0xFFFFB800L),
            ShapeSeed("heart", "Heart", "হৃদয়", "Full of kindness and love", "ভালোবাসা ও দয়ার প্রতীক", "💖", 0xFFFF4D7EL),
            ShapeSeed("diamond", "Diamond", "হীরক", "Shaped like a flying kite", "উড়ন্ত ঘুড়ির মতো আকৃতি", "🔷", 0xFF8E5CFFL),
            ShapeSeed("rectangle", "Rectangle", "আয়তক্ষেত্র", "Shaped like a storybook or door", "বই বা দরজার মতো আকৃতি", "▬", 0xFF2AD17BL),
            ShapeSeed("oval", "Oval", "ডিম্বাকৃতি", "Smooth stretched circle like an egg", "ডিমের মতো মসৃণ গোল আকৃতি", "🥚", 0xFF1CC5B7L)
        )
        return shapes.mapIndexed { idx, s ->
            LessonItemEntity(
                id = "shape_${s.id}",
                moduleType = ModuleType.SHAPES.key,
                symbol = s.en,
                symbolBn = s.bn,
                titleEn = s.en,
                titleBn = s.bn,
                phoneticEn = "Shape: ${s.en}",
                phoneticBn = "আকৃতি: ${s.bn}",
                exampleEn = s.exEn,
                exampleBn = s.exBn,
                visualBadge = s.badge,
                accentColorHex = s.hex,
                sortOrder = idx
            )
        }
    }

    private fun buildAnimals(): List<LessonItemEntity> {
        data class AnimalSeed(
            val id: String,
            val en: String,
            val bn: String,
            val soundEn: String,
            val soundBn: String,
            val badge: String,
            val hex: Long
        )
        val animals = listOf(
            AnimalSeed("tiger", "Bengal Tiger", "বাঘ", "Roar! Strong striped jumper", "হালুম! ডোরাকাটা সাহসী বাঘ", "🐯", 0xFFFF8C1AL),
            AnimalSeed("elephant", "Elephant", "হাতি", "Toot! Gentle giant with a long trunk", "লম্বা শুঁড়ের দয়ালু হাতি", "🐘", 0xFF6B8BFFL),
            AnimalSeed("rabbit", "Rabbit", "খরগোশ", "Hop hop! Soft fluffy ears", "লাফিয়ে চলা নরম খরগোশ", "🐰", 0xFFFF6B9EL),
            AnimalSeed("owl", "Owl", "পেঁচা", "Hoo hoo! Wise night explorer", "রাতের জ্ঞানী বন্ধু পেঁচা", "🦉", 0xFF8E5CFFL),
            AnimalSeed("duck", "Duck", "হাঁস", "Quack quack! Loves splashing in ponds", "প্যাঁক প্যাঁক! পুকুরে সাঁতার কাটে", "🦆", 0xFFFFB800L),
            AnimalSeed("cat", "Cat", "বিড়াল", "Meow! Playful purring friend", "মিউ মিউ! আদুরে বন্ধু বিড়াল", "🐱", 0xFFFF7A59L),
            AnimalSeed("panda", "Panda", "পান্ডা", "Munch munch! Loves green bamboo", "সবুজ বাঁশ খেতে ভালোবাসে পান্ডা", "🐼", 0xFF2AD17BL),
            AnimalSeed("butterfly", "Butterfly", "প্রজাপতি", "Flutters gracefully on colorful wings", "রঙিন ডানায় উড়ে বেড়ায় প্রজাপতি", "🦋", 0xFF2BB3FFL),
            AnimalSeed("peacock", "Peacock", "ময়ূর", "Dances proudly with rainbow feathers", "রঙিন পেখম মেলে নাচে ময়ূর", "🦚", 0xFF1CC5B7L),
            AnimalSeed("parrot", "Parrot", "টিয়া পাখি", "Bright green bird that loves to chatter", "সবুজ রঙের মিষ্টি কথা বলা টিয়া", "🦜", 0xFF28C76FL)
        )
        return animals.mapIndexed { idx, a ->
            LessonItemEntity(
                id = "animal_${a.id}",
                moduleType = ModuleType.ANIMALS.key,
                symbol = a.en,
                symbolBn = a.bn,
                titleEn = a.en,
                titleBn = a.bn,
                phoneticEn = a.en,
                phoneticBn = a.bn,
                exampleEn = a.soundEn,
                exampleBn = a.soundBn,
                visualBadge = a.badge,
                accentColorHex = a.hex,
                sortOrder = idx
            )
        }
    }

    fun buildStories(): Pair<List<StoryEntity>, List<StoryPageEntity>> {
        val stories = listOf(
            StoryEntity(
                id = "story_brave_tiger",
                titleEn = "The Brave Little Tiger",
                titleBn = "সাহসী ছোট্ট বাঘ",
                subtitleEn = "A jungle adventure about helping friends",
                subtitleBn = "বন্ধুদের সাহায্য করার এক সুন্দর গল্প",
                moralEn = "True bravery means helping even the tiniest friend.",
                moralBn = "সত্যিকারের সাহস হলো ছোট্ট বন্ধুকেও ভালোবাসা ও সাহায্য করা।",
                coverAssetKey = "story_tiger",
                ageTag = "3–7",
                accentColorHex = 0xFFFF8C1AL,
                sortOrder = 0
            ),
            StoryEntity(
                id = "story_rabbit_moon",
                titleEn = "The Rabbit & the Shining Moon",
                titleBn = "চালাক খরগোশ ও চাঁদের হাসি",
                subtitleEn = "A cozy bedtime tale of wonder",
                subtitleBn = "রাতের আকাশ ও কৌতূহলের মিষ্টি গল্প",
                moralEn = "Curiosity and calm thinking light up the darkest night.",
                moralBn = "কৌতূহল আর শান্ত মন অন্ধকার রাতকেও আলোকিত করে।",
                coverAssetKey = "story_rabbit",
                ageTag = "3–8",
                accentColorHex = 0xFF8E5CFFL,
                sortOrder = 1
            ),
            StoryEntity(
                id = "story_kind_elephant",
                titleEn = "The Kind Little Elephant",
                titleBn = "দয়ালু ছোট্ট হাতি",
                subtitleEn = "Sharing joy by the sunny meadow river",
                subtitleBn = "নদীর তীরে আনন্দ ভাগাভাগি করার গল্প",
                moralEn = "When we share what we have, everyone blooms together.",
                moralBn = "আনন্দ ও সাহায্য ভাগাভাগি করলে সবাই একসাথে হাসে।",
                coverAssetKey = "story_elephant",
                ageTag = "3–8",
                accentColorHex = 0xFF2AD17BL,
                sortOrder = 2
            )
        )

        val pages = listOf(
            // Story 1
            StoryPageEntity(
                storyId = "story_brave_tiger",
                pageNumber = 1,
                textEn = "Deep inside the sunny Sundarbans lived Tiku, a cheerful little tiger cub who loved wearing his bright red scarf.",
                textBn = "রোদেলা সুন্দরবনের ভেতরে থাকত টিকু নামের এক হাসিখুশি ছোট্ট বাঘছানা। সে তার লাল স্কার্ফ পরতে খুব ভালোবাসত।",
                sceneAssetKey = "story_tiger"
            ),
            StoryPageEntity(
                storyId = "story_brave_tiger",
                pageNumber = 2,
                textEn = "One golden morning, Tiku heard a tiny 'Cheep! Cheep!' by the wooden bridge. A little yellow bird had lost its way across the stream.",
                textBn = "এক সোনালি সকালে কাঠের সেতুর কাছে টিকু শুনল ছোট্ট 'কিচির মিচির' ডাক। একটি হলুদ পাখি ছানা নদী পার হতে ভয় পাচ্ছিল।",
                sceneAssetKey = "story_tiger"
            ),
            StoryPageEntity(
                storyId = "story_brave_tiger",
                pageNumber = 3,
                textEn = "Tiku stepped gently onto the wobbly bridge, smiled warmly, and guided the little bird safely across the sparkling emerald water.",
                textBn = "টিকু আস্তে আস্তে সেতুতে পা রাখল, মিষ্টি করে হাসল এবং ছোট্ট পাখিটিকে নিরাপদে ঝিলমিল নদী পার করে দিল।",
                sceneAssetKey = "story_tiger"
            ),
            StoryPageEntity(
                storyId = "story_brave_tiger",
                pageNumber = 4,
                textEn = "The bird sang a joyful melody, and the whole forest cheered for Tiku—the kindest, bravest little tiger!",
                textBn = "পাখিটি খুশিতে মিষ্টি গান গাইল, আর পুরো বন হাততালি দিয়ে বলল—টিকু সবচেয়ে দয়ালু আর সাহসী বাঘছানা!",
                sceneAssetKey = "story_tiger"
            ),
            // Story 2
            StoryPageEntity(
                storyId = "story_rabbit_moon",
                pageNumber = 1,
                textEn = "Every evening, fluffy little Mitu the rabbit hopped up the flower hill to count the twinkling stars in the twilight sky.",
                textBn = "প্রতি সন্ধ্যায় তুলতুলে ছোট্ট খরগোশ মিতু ফুলের টিলায় লাফিয়ে উঠত রাতের আকাশের তারা গুনতে।",
                sceneAssetKey = "story_rabbit"
            ),
            StoryPageEntity(
                storyId = "story_rabbit_moon",
                pageNumber = 2,
                textEn = "Tonight, Mitu saw a big golden moon glowing in the sky—and another glowing moon swimming right inside the calm pond!",
                textBn = "আজ রাতে মিতু দেখল আকাশে একটি বড় সোনালি চাঁদ—আর শান্ত পুকুরের জলেও আরেকটি চাঁদ হাসছে!",
                sceneAssetKey = "story_rabbit"
            ),
            StoryPageEntity(
                storyId = "story_rabbit_moon",
                pageNumber = 3,
                textEn = "Mitu tapped the water softly. Ripples danced across the pond! 'Aha!' giggled Mitu. 'The pond is the moon's shiny mirror!'",
                textBn = "মিতু আস্তে করে জলে ছোঁয়া দিল। ঢেউয়ের তালে চাঁদটা নেচে উঠল! মিতু হেসে বলল, 'আহা! পুকুরটা তো চাঁদের আয়না!'",
                sceneAssetKey = "story_rabbit"
            ),
            StoryPageEntity(
                storyId = "story_rabbit_moon",
                pageNumber = 4,
                textEn = "With a happy heart and a big yawn, Mitu wished the smiling moon goodnight and drifted into sweet dreams.",
                textBn = "খুশি মনে মিতু হাসিমুখ চাঁদকে শুভরাত্রি জানিয়ে মিষ্টি স্বপ্নের দেশে ঘুমিয়ে পড়ল।",
                sceneAssetKey = "story_rabbit"
            ),
            // Story 3
            StoryPageEntity(
                storyId = "story_kind_elephant",
                pageNumber = 1,
                textEn = "By a bright meadow river lived Golu, a gentle baby elephant with sparkling blue eyes and a crown of fresh green leaves.",
                textBn = "রোদেলা নদীর তীরে থাকত গোলু নামের এক মিষ্টি হাতিছানা। তার মাথায় ছিল সবুজ পাতার সুন্দর মুকুট।",
                sceneAssetKey = "story_elephant"
            ),
            StoryPageEntity(
                storyId = "story_kind_elephant",
                pageNumber = 2,
                textEn = "One warm afternoon, the pink lotus flowers looked thirsty and four tiny ducklings wished for a cool rainbow splash.",
                textBn = "এক গরম দুপুরে গোলাপি পদ্মফুলগুলো তৃষ্ণার্ত হয়ে পড়ল, আর চারটি ছোট্ট হাঁসছানা ঠান্ডা জলের খেলা চাইল।",
                sceneAssetKey = "story_elephant"
            ),
            StoryPageEntity(
                storyId = "story_kind_elephant",
                pageNumber = 3,
                textEn = "Golu filled his favorite blue watering can and poured sparkling rainbow droplets over the flowers and giggling ducklings.",
                textBn = "গোলু তার প্রিয় নীল ঝাঁঝরি ভরে পদ্মফুল আর হাসিখুশি হাঁসছানাদের ওপর রংধনুর মতো ঝিলমিল জল ছিটিয়ে দিল।",
                sceneAssetKey = "story_elephant"
            ),
            StoryPageEntity(
                storyId = "story_kind_elephant",
                pageNumber = 4,
                textEn = "A giant rainbow arched across the sky as everyone danced together. Kindness always makes the world brighter!",
                textBn = "আকাশ জুড়ে ভেসে উঠল এক বিশাল রংধনু, আর সবাই আনন্দে নেচে উঠল। দয়াই পৃথিবীকে সবচেয়ে সুন্দর করে!",
                sceneAssetKey = "story_elephant"
            )
        )

        return stories to pages
    }
}
