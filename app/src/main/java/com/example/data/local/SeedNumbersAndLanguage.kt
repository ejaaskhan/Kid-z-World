package com.example.data.local

import com.example.core.localization.AppLanguage
import com.example.core.localization.toLocalizedDigits
import com.example.data.local.entity.LessonItemEntity
import com.example.domain.model.ModuleType

object SeedNumbersAndLanguage {

    private val palette = listOf(
        0xFF2BB3FFL,
        0xFFFF8C1AL,
        0xFF2AD17BL,
        0xFFFF5C8AL,
        0xFF8E5CFFL,
        0xFFFFB800L
    )

    private val englishOnes = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    )

    private val englishTens = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    // Complete authentic Bengali number words for 1..100
    private val banglaNumbers1To100 = listOf(
        "এক", "দুই", "তিন", "চার", "পাঁচ", "ছয়", "সাত", "আট", "নয়", "দশ",
        "এগারো", "বারো", "তেরো", "চৌদ্দ", "পনেরো", "ষোলো", "সতেরো", "আঠারো", "উনিশ", "বিশ",
        "একুশ", "বাইশ", "তেইশ", "চব্বিশ", "পঁচিশ", "ছাব্বিশ", "সাতাশ", "আটাশ", "ঊনত্রিশ", "ত্রিশ",
        "একত্রিশ", "বত্রিশ", "তেত্রিশ", "চৌত্রিশ", "পঁয়ত্রিশ", "ছত্রিশ", "সাঁইত্রিশ", "আটত্রিশ", "ঊনচল্লিশ", "চল্লিশ",
        "একচল্লিশ", "বিয়াল্লিশ", "তেতাল্লিশ", "চুয়াল্লিশ", "পঁয়তাল্লিশ", "ছেচল্লিশ", "সাতচল্লিশ", "আটচল্লিশ", "ঊনপঞ্চাশ", "পঞ্চাশ",
        "একান্ন", "বাহান্ন", "তিপ্পান্ন", "চুয়ান্ন", "পঞ্চান্ন", "ছাপ্পান্ন", "সাতান্ন", "আটান্ন", "ঊনষাট", "ষাট",
        "একষট্টি", "বাষট্টি", "তেষট্টি", "চৌষট্টি", "পঁয়ষট্টি", "ছেষট্টি", "সাতষট্টি", "আটষট্টি", "ঊনসত্তর", "সত্তর",
        "একাত্তর", "বাহাত্তর", "তিয়াত্তর", "চুয়াত্তর", "পঁচাত্তর", "ছিয়াত্তর", "সাতাত্তর", "আটাত্তর", "ঊনআশি", "আশি",
        "একাশি", "বিরাশি", "তিরাশি", "চুরাশি", "পঁচাশি", "ছিয়াশি", "সাতাশি", "আটাশি", "ঊননব্বই", "নব্বই",
        "একানব্বই", "বিরানব্বই", "তিরানব্বই", "চুরানব্বই", "পঁচানব্বই", "ছিয়ানব্বই", "সাতানব্বই", "আটানব্বই", "নিরানব্বই", "একশো"
    )

    fun englishNumberWord(n: Int): String = when {
        n == 100 -> "One Hundred"
        n in 1..19 -> englishOnes[n]
        n in 20..99 -> {
            val tens = englishTens[n / 10]
            val ones = englishOnes[n % 10]
            if (ones.isEmpty()) tens else "$tens-$ones"
        }
        else -> n.toString()
    }

    fun banglaNumberWord(n: Int): String =
        banglaNumbers1To100.getOrElse(n - 1) { n.toLocalizedDigits(AppLanguage.BANGLA) }

    fun buildNumbers1To100(): List<LessonItemEntity> {
        val emojis = listOf("⭐", "🍎", "🎈", "🦋", "🌸", "🐠")
        return (1..100).map { n ->
            val bnDigits = n.toLocalizedDigits(AppLanguage.BANGLA)
            val wordEn = englishNumberWord(n)
            val wordBn = banglaNumberWord(n)
            val tens = n / 10
            val ones = n % 10
            val groupEn = if (n <= 20) "$n $wordEn" else "$tens tens + $ones ones"
            val groupBn = if (n <= 20) "$bnDigits $wordBn" else "${tens.toLocalizedDigits(AppLanguage.BANGLA)} দশ + ${ones.toLocalizedDigits(AppLanguage.BANGLA)} একক"
            LessonItemEntity(
                id = "num_$n",
                moduleType = ModuleType.NUMBERS.key,
                symbol = n.toString(),
                symbolBn = bnDigits,
                titleEn = wordEn,
                titleBn = wordBn,
                phoneticEn = "Number $n ($wordEn)",
                phoneticBn = "সংখ্যা $bnDigits ($wordBn)",
                exampleEn = groupEn,
                exampleBn = groupBn,
                visualBadge = emojis[(n - 1) % emojis.size],
                accentColorHex = palette[(n - 1) % palette.size],
                countValue = n,
                sortOrder = n - 1
            )
        }
    }

    fun buildWorldExtraModules(): List<LessonItemEntity> =
        buildFruitsAndVeggies() + buildHumanBody() + buildVehicles() + buildNature()

    private fun buildFruitsAndVeggies(): List<LessonItemEntity> {
        data class Item(val id: String, val en: String, val bn: String, val exEn: String, val exBn: String, val emoji: String, val hex: Long)
        val items = listOf(
            Item("mango", "Mango", "আম", "Sweet golden king of fruits", "মিষ্টি রসে ভরা ফলের রাজা", "🥭", 0xFFFFB800L),
            Item("apple", "Apple", "আপেল", "Crunchy red fruit that keeps us strong", "কচকচে লাল আপেল শরীর সুস্থ রাখে", "🍎", 0xFFFF4D5AL),
            Item("banana", "Banana", "কলা", "Soft yellow energy booster", "নরম হলুদ কলা শক্তি জোগায়", "🍌", 0xFFFFC107L),
            Item("watermelon", "Watermelon", "তরমুজ", "Juicy red summer cooler", "গরমের রসালো মিষ্টি তরমুজ", "🍉", 0xFFFF5C8AL),
            Item("grapes", "Grapes", "আঙুর", "Tiny sweet bunches on vines", "থোকায় থোকায় মিষ্টি আঙুর", "🍇", 0xFF8E5CFFL),
            Item("carrot", "Carrot", "গাজর", "Crunchy orange veggie great for eyes", "চোখের জন্য উপকারী কমলা গাজর", "🥕", 0xFFFF8C1AL),
            Item("tomato", "Tomato", "টমেটো", "Bright red juicy vegetable", "লাল টুকটুকে রসালো টমেটো", "🍅", 0xFFFF5252L),
            Item("broccoli", "Broccoli", "ব্রকলি", "Little green tree vegetable", "ছোট্ট সবুজ গাছের মতো ব্রকলি", "🥦", 0xFF2AD17BL),
            Item("potato", "Potato", "আলু", "Yummy golden underground veggie", "মাটির নিচে জন্মানো মজার আলু", "🥔", 0xFFD99B52L),
            Item("corn", "Sweet Corn", "ভুট্টা", "Sunny yellow crunchy kernels", "হলুদ দানাদার মিষ্টি ভুট্টা", "🌽", 0xFFFFC82CL)
        )
        return items.mapIndexed { idx, i ->
            LessonItemEntity(
                id = "fruitveg_${i.id}",
                moduleType = ModuleType.FRUITS_VEGGIES.key,
                symbol = i.en,
                symbolBn = i.bn,
                titleEn = i.en,
                titleBn = i.bn,
                phoneticEn = i.en,
                phoneticBn = i.bn,
                exampleEn = i.exEn,
                exampleBn = i.exBn,
                visualBadge = i.emoji,
                accentColorHex = i.hex,
                sortOrder = idx
            )
        }
    }

    private fun buildHumanBody(): List<LessonItemEntity> {
        data class Part(val id: String, val en: String, val bn: String, val exEn: String, val exBn: String, val emoji: String, val hex: Long)
        val parts = listOf(
            Part("eyes", "Eyes", "চোখ", "We see rainbows and books with our eyes", "চোখ দিয়ে আমরা সুন্দর পৃথিবী দেখি", "👀", 0xFF2BB3FFL),
            Part("ears", "Ears", "কান", "We listen to music and stories with our ears", "কান দিয়ে আমরা গান ও গল্প শুনি", "👂", 0xFFFF8C1AL),
            Part("nose", "Nose", "নাক", "We smell sweet flowers with our nose", "নাক দিয়ে আমরা ফুলের গন্ধ নিই", "👃", 0xFFFF5C8AL),
            Part("mouth", "Smile & Mouth", "মুখ ও হাসি", "We speak kindly and eat healthy food", "মুখ দিয়ে আমরা মিষ্টি কথা বলি ও খাই", "👄", 0xFFFF4D7EL),
            Part("hands", "Hands", "হাত", "We clap, draw, and help friends", "হাত দিয়ে আমরা ছবি আঁকি ও সাহায্য করি", "🙌", 0xFF2AD17BL),
            Part("feet", "Feet", "পা", "We walk, jump, and play outside", "পা দিয়ে আমরা হাঁটি ও খেলাধুলা করি", "🦶", 0xFF8E5CFFL),
            Part("heart", "Heart", "হৃদপিণ্ড", "Beats softly all day to keep us alive", "সারাদিন ধুকপুক করে আমাদের সুস্থ রাখে", "❤️", 0xFFFF4D5AL),
            Part("brain", "Brain", "মস্তিষ্ক", "Helps us think, learn, and dream big", "ভাবতে, শিখতে ও স্বপ্ন দেখতে সাহায্য করে", "🧠", 0xFFFFB800L)
        )
        return parts.mapIndexed { idx, p ->
            LessonItemEntity(
                id = "body_${p.id}",
                moduleType = ModuleType.HUMAN_BODY.key,
                symbol = p.en,
                symbolBn = p.bn,
                titleEn = p.en,
                titleBn = p.bn,
                phoneticEn = p.en,
                phoneticBn = p.bn,
                exampleEn = p.exEn,
                exampleBn = p.exBn,
                visualBadge = p.emoji,
                accentColorHex = p.hex,
                sortOrder = idx
            )
        }
    }

    private fun buildVehicles(): List<LessonItemEntity> {
        data class Vehicle(val id: String, val en: String, val bn: String, val exEn: String, val exBn: String, val emoji: String, val hex: Long)
        val items = listOf(
            Vehicle("car", "Car", "গাড়ি", "Zoom zoom on four shiny wheels", "চার চাকায় রাস্তায় ছোটে গাড়ি", "🚗", 0xFFFF4D5AL),
            Vehicle("bus", "School Bus", "স্কুল বাস", "Takes happy friends to school", "বন্ধুদের নিয়ে স্কুলে যায় বাস", "🚌", 0xFFFFB800L),
            Vehicle("train", "Train", "ট্রেন", "Chugga chugga along steel tracks", "ঝিকঝিক শব্দে রেললাইনে চলে ট্রেন", "🚂", 0xFF2AD17BL),
            Vehicle("boat", "Boat", "নৌকা", "Sails gently across sparkling rivers", "নদীর ঢেউয়ে ভেসে চলে নৌকা", "⛵", 0xFF2BB3FFL),
            Vehicle("airplane", "Airplane", "উড়োজাহাজ", "Soars high above the fluffy clouds", "মেঘের ওপরে আকাশে ওড়ে উড়োজাহাজ", "✈️", 0xFF8E5CFFL),
            Vehicle("bicycle", "Bicycle", "সাইকেল", "Pedal with two wheels for fun exercise", "দুই চাকায় প্যাডেল ঘুরিয়ে চলে সাইকেল", "🚲", 0xFFFF8C1AL),
            Vehicle("rocket", "Space Rocket", "রকেট", "Blasts off to visit the moon and stars", "চাঁদ আর তারার দেশে ওড়ে রকেট", "🚀", 0xFFFF5C8AL),
            Vehicle("rickshaw", "Rickshaw", "রিকশা", "Colorful three-wheeler on city streets", "রঙিন তিন চাকার চেনা রিকশা", "🛺", 0xFF1CC5B7L)
        )
        return items.mapIndexed { idx, v ->
            LessonItemEntity(
                id = "vehicle_${v.id}",
                moduleType = ModuleType.VEHICLES.key,
                symbol = v.en,
                symbolBn = v.bn,
                titleEn = v.en,
                titleBn = v.bn,
                phoneticEn = v.en,
                phoneticBn = v.bn,
                exampleEn = v.exEn,
                exampleBn = v.exBn,
                visualBadge = v.emoji,
                accentColorHex = v.hex,
                sortOrder = idx
            )
        }
    }

    private fun buildNature(): List<LessonItemEntity> {
        data class NatureItem(val id: String, val en: String, val bn: String, val exEn: String, val exBn: String, val emoji: String, val hex: Long)
        val items = listOf(
            NatureItem("sun", "Sun", "সূর্য", "Gives warm daylight to the whole Earth", "পৃথিবীকে আলো আর উষ্ণতা দেয় সূর্য", "☀️", 0xFFFFB800L),
            NatureItem("moon", "Moon", "চাঁদ", "Glows gently in the starry night sky", "রাতের আকাশে মিষ্টি আলো ছড়ায় চাঁদ", "🌙", 0xFF8E5CFFL),
            NatureItem("tree", "Tree", "গাছ", "Gives us shade, fruit, and fresh air", "আমাদের ছায়া, ফল আর তাজা বাতাস দেয়", "🌳", 0xFF2AD17BL),
            NatureItem("river", "River", "নদী", "Flowing fresh water home to little fish", "বয়ে চলা স্বচ্ছ জলের সুন্দর নদী", "🌊", 0xFF2BB3FFL),
            NatureItem("rain", "Rain", "বৃষ্টি", "Waters green fields with cool drops", "সবুজ প্রকৃতিকে সতেজ করে বৃষ্টি", "🌧️", 0xFF4A90E2L),
            NatureItem("rainbow", "Rainbow", "রংধনু", "Seven bright colors painted in the sky", "আকাশে আঁকা সাত রঙের রংধনু", "🌈", 0xFFFF5C8AL),
            NatureItem("flower", "Flower", "ফুল", "Colorful petals loved by busy bees", "মৌমাছি ও প্রজাপতির প্রিয় রঙিন ফুল", "🌸", 0xFFFF66A3L),
            NatureItem("mountain", "Mountain", "পাহাড়", "Tall green peaks touching the clouds", "মেঘ ছোঁয়া উঁচু সবুজ পাহাড়", "⛰️", 0xFF28A76FL)
        )
        return items.mapIndexed { idx, n ->
            LessonItemEntity(
                id = "nature_${n.id}",
                moduleType = ModuleType.NATURE.key,
                symbol = n.en,
                symbolBn = n.bn,
                titleEn = n.en,
                titleBn = n.bn,
                phoneticEn = n.en,
                phoneticBn = n.bn,
                exampleEn = n.exEn,
                exampleBn = n.exBn,
                visualBadge = n.emoji,
                accentColorHex = n.hex,
                sortOrder = idx
            )
        }
    }

    fun buildLanguageModules(): List<LessonItemEntity> =
        buildBasicWords() + buildEverydayVocab() + buildSimpleSentences() + buildBanglaVocab()

    private fun buildBasicWords(): List<LessonItemEntity> {
        val list = listOf(
            Triple("Book", "বই", "I read a fun storybook.") to ("📚" to "আমি মজার গল্পের বই পড়ি।"),
            Triple("Water", "পানি", "Drink clean water every day.") to ("💧" to "প্রতিদিন বিশুদ্ধ পানি পান করো।"),
            Triple("Home", "বাড়ি", "Our home is warm and cozy.") to ("🏡" to "আমাদের বাড়ি সুন্দর ও আরামদায়ক।"),
            Triple("Friend", "বন্ধু", "Playing with a friend is joyful.") to ("🤝" to "বন্ধুর সাথে খেলতে খুব ভালো লাগে।"),
            Triple("Smile", "হাসি", "A kind smile brightens the day.") to ("😊" to "একটি মিষ্টি হাসি দিনটা সুন্দর করে।"),
            Triple("Star", "তারা", "Little stars twinkle high above.") to ("⭐" to "আকাশে ছোট্ট তারাগুলো ঝিকমিক করে।"),
            Triple("Ball", "বল", "Bounce the colorful ball high.") to ("⚽" to "রঙিন বলটি উঁচু করে বাউন্স করো।"),
            Triple("Music", "সংগীত", "Sweet music makes us dance.") to ("🎵" to "মিষ্টি গান আমাদের আনন্দ দেয়।")
        )
        return list.mapIndexed { idx, (t, pair) ->
            LessonItemEntity(
                id = "lang_word_$idx",
                moduleType = ModuleType.BASIC_WORDS.key,
                symbol = t.first,
                symbolBn = t.second,
                titleEn = t.first,
                titleBn = t.second,
                phoneticEn = "${t.first} = ${t.second}",
                phoneticBn = "${t.second} = ${t.first}",
                exampleEn = t.third,
                exampleBn = pair.second,
                visualBadge = pair.first,
                accentColorHex = palette[idx % palette.size],
                sortOrder = idx
            )
        }
    }

    private fun buildEverydayVocab(): List<LessonItemEntity> {
        val list = listOf(
            Triple("Good Morning", "শুভ সকাল", "Say Good Morning with a bright smile!") to ("🌅" to "সকালে হাসিমুখে বলো শুভ সকাল!"),
            Triple("Thank You", "ধন্যবাদ", "Say Thank You when someone helps you.") to ("🙏" to "কেউ সাহায্য করলে বলো ধন্যবাদ।"),
            Triple("Please", "দয়া করে", "Use Please when asking nicely.") to ("💛" to "ভদ্রভাবে কিছু চাইতে বলো দয়া করে।"),
            Triple("Sorry", "দুঃখিত", "Say Sorry if you make a mistake.") to ("🤗" to "ভুল হলে মিষ্টি করে বলো দুঃখিত।"),
            Triple("Family", "পরিবার", "My family loves and cares for me.") to ("👨‍👩‍👧‍👦" to "আমার পরিবার আমাকে খুব ভালোবাসে।"),
            Triple("School", "বিদ্যালয়", "We learn exciting new things at school.") to ("🏫" to "বিদ্যালয়ে আমরা নতুন অনেক কিছু শিখি।"),
            Triple("Share", "ভাগাভাগি", "Sharing toys brings double the fun!") to ("🎁" to "খেলনা ভাগাভাগি করলে আনন্দ দ্বিগুণ হয়!"),
            Triple("Good Night", "শুভরাত্রি", "Wish everyone Good Night before sleep.") to ("🌙" to "ঘুমানোর আগে সবাইকে বলো শুভরাত্রি।")
        )
        return list.mapIndexed { idx, (t, pair) ->
            LessonItemEntity(
                id = "lang_vocab_$idx",
                moduleType = ModuleType.EVERYDAY_VOCAB.key,
                symbol = t.first,
                symbolBn = t.second,
                titleEn = t.first,
                titleBn = t.second,
                phoneticEn = t.first,
                phoneticBn = t.second,
                exampleEn = t.third,
                exampleBn = pair.second,
                visualBadge = pair.first,
                accentColorHex = palette[(idx + 2) % palette.size],
                sortOrder = idx
            )
        }
    }

    private fun buildSimpleSentences(): List<LessonItemEntity> {
        val list = listOf(
            Triple("I love to learn.", "আমি শিখতে ভালোবাসি।", "Learning new words is fun!") to "🎓",
            Triple("The sun is bright.", "সূর্য খুব উজ্জ্বল।", "Morning sunlight wakes the flowers.") to "☀️",
            Triple("This is a sweet mango.", "এটি একটি মিষ্টি আম।", "Mangoes taste yummy in summer.") to "🥭",
            Triple("Birds fly in the sky.", "পাখিরা আকাশে ওড়ে।", "Look up at the singing birds!") to "🐦",
            Triple("I help my friends.", "আমি আমার বন্ধুদের সাহায্য করি।", "Kindness makes great friendships.") to "🤝",
            Triple("The moon shines at night.", "রাতে চাঁদ আলো দেয়।", "Stars twinkle beside the moon.") to "🌙",
            Triple("I brush my teeth daily.", "আমি প্রতিদিন দাঁত ব্রাশ করি।", "Healthy habits keep smiles bright.") to "🪥",
            Triple("Green trees give us air.", "সবুজ গাছ আমাদের বাতাস দেয়।", "Let's care for every little plant.") to "🌳"
        )
        return list.mapIndexed { idx, (t, emoji) ->
            LessonItemEntity(
                id = "lang_sent_$idx",
                moduleType = ModuleType.SIMPLE_SENTENCES.key,
                symbol = t.first,
                symbolBn = t.second,
                titleEn = t.first,
                titleBn = t.second,
                phoneticEn = t.third,
                phoneticBn = t.first,
                exampleEn = t.second,
                exampleBn = t.first,
                visualBadge = emoji,
                accentColorHex = palette[(idx + 3) % palette.size],
                sortOrder = idx
            )
        }
    }

    private fun buildBanglaVocab(): List<LessonItemEntity> {
        val list = listOf(
            Triple("ভালোবাসা", "Love (Bhalobasha)", "পরিবার ও বন্ধুদের প্রতি ভালোবাসা।") to ("❤️" to "Love for family and friends."),
            Triple("আকাশ", "Sky (Akash)", "নীল আকাশে সাদা মেঘ ভাসে।") to ("☁️" to "White clouds float in the blue sky."),
            Triple("পাখি", "Bird (Pakhi)", "ভোরবেলা পাখিরা মিষ্টি গান গায়।") to ("🦜" to "Birds sing sweetly in the morning."),
            Triple("ফুল", "Flower (Phul)", "বাগানে রঙিন ফুল ফুটেছে।") to ("🌺" to "Colorful flowers bloom in the garden."),
            Triple("নদী", "River (Nodi)", "বাংলাদেশ সুন্দর নদীর দেশ।") to ("🛶" to "Bangladesh is a land of lovely rivers."),
            Triple("বৃষ্টি", "Rain (Brishti)", "টাপুর টুপুর বৃষ্টি পড়ে।") to ("☔" to "Pitter-patter falls the cool rain."),
            Triple("স্বপ্ন", "Dream (Shopno)", "বড় হয়ে ভালো মানুষ হওয়ার স্বপ্ন।") to ("✨" to "Dreaming big to grow kind and wise."),
            Triple("আনন্দ", "Joy (Anondo)", "একসাথে শিখতে অনেক আনন্দ! ") to ("🎉" to "Learning together brings so much joy!")
        )
        return list.mapIndexed { idx, (t, pair) ->
            LessonItemEntity(
                id = "lang_bn_$idx",
                moduleType = ModuleType.BANGLA_VOCAB.key,
                symbol = t.first,
                symbolBn = t.first,
                titleEn = "${t.first} — ${t.second}",
                titleBn = t.first,
                phoneticEn = t.second,
                phoneticBn = t.second,
                exampleEn = pair.second,
                exampleBn = t.third,
                visualBadge = pair.first,
                accentColorHex = palette[(idx + 4) % palette.size],
                sortOrder = idx
            )
        }
    }
}
