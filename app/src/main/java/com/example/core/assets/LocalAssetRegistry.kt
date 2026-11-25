package com.example.core.assets

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R

data class AvatarAsset(
    val key: String,
    val emoji: String,
    val labelEn: String,
    val labelBn: String,
    val primaryColor: Color,
    val rimColor: Color
)

data class PuzzleArtworkAsset(
    val key: String,
    val titleBn: String,
    val titleEn: String,
    val emoji: String,
    @DrawableRes val drawableRes: Int,
    val accentHex: Long
)

object LocalAssetRegistry {

    @DrawableRes
    val heroIslandBanner: Int = R.drawable.img_hero_island_1795533556354

    @DrawableRes
    val appCrestArtwork: Int = R.drawable.img_kidz_icon_1795533561471

    @DrawableRes
    val poemsMoonlitArtwork: Int = R.drawable.img_poems_moonlit_boat_1795605697441

    @DrawableRes
    val puzzleOrchardArtwork: Int = R.drawable.img_puzzle_sunny_orchard_1795605698407

    val avatars: List<AvatarAsset> = listOf(
        AvatarAsset("owl", "🦉", "Wise Owl", "জ্ঞানী পেঁচা", Color(0xFF4CC3FF), Color(0xFF198AD1)),
        AvatarAsset("tiger", "🐯", "Brave Tiger", "সাহসী বাঘ", Color(0xFFFF9A2E), Color(0xFFD66A00)),
        AvatarAsset("rabbit", "🐰", "Sunny Bunny", "মিষ্টি খরগোশ", Color(0xFFFF6B9E), Color(0xFFD1346A)),
        AvatarAsset("elephant", "🐘", "Kind Ellie", "দয়ালু হাতি", Color(0xFF9C72FF), Color(0xFF6B3FD9)),
        AvatarAsset("panda", "🐼", "Happy Panda", "খুশি পান্ডা", Color(0xFF34D484), Color(0xFF129E58)),
        AvatarAsset("fox", "🦊", "Clever Fox", "চালাক শিয়াল", Color(0xFFFFB830), Color(0xFFCC8400))
    )

    val puzzleArtworks: List<PuzzleArtworkAsset> = listOf(
        PuzzleArtworkAsset(
            key = "puzzle_orchard",
            titleBn = "সিংহ ও ফলের বাগান",
            titleEn = "Sunny Lion Orchard",
            emoji = "🦁",
            drawableRes = R.drawable.img_puzzle_sunny_orchard_1795605698407,
            accentHex = 0xFFFF9A2E
        ),
        PuzzleArtworkAsset(
            key = "puzzle_moonlit_boat",
            titleBn = "চাঁদ মামার নৌকা",
            titleEn = "Moonlit Village Boat",
            emoji = "🌙",
            drawableRes = R.drawable.img_poems_moonlit_boat_1795605697441,
            accentHex = 0xFF4CC3FF
        ),
        PuzzleArtworkAsset(
            key = "puzzle_tiger",
            titleBn = "সুন্দরবনের বাঘ",
            titleEn = "Friendly Tiger Cub",
            emoji = "🐯",
            drawableRes = R.drawable.img_story_tiger_1795533555329,
            accentHex = 0xFF34D484
        ),
        PuzzleArtworkAsset(
            key = "puzzle_rabbit",
            titleBn = "মিষ্টি খরগোশ",
            titleEn = "Sunny Bunny Meadow",
            emoji = "🐰",
            drawableRes = R.drawable.img_story_rabbit_1795533557150,
            accentHex = 0xFFFF6B9E
        ),
        PuzzleArtworkAsset(
            key = "puzzle_elephant",
            titleBn = "দয়ালু হাতি বন্ধু",
            titleEn = "Kind Elephant Friend",
            emoji = "🐘",
            drawableRes = R.drawable.img_story_elephant_1795533556171,
            accentHex = 0xFF9C72FF
        )
    )

    fun avatarByKey(key: String): AvatarAsset =
        avatars.firstOrNull { it.key == key } ?: avatars.first()

    @DrawableRes
    fun storyIllustration(assetKey: String): Int = when (assetKey) {
        "story_tiger" -> R.drawable.img_story_tiger_1795533555329
        "story_rabbit" -> R.drawable.img_story_rabbit_1795533557150
        "story_elephant" -> R.drawable.img_story_elephant_1795533556171
        else -> heroIslandBanner
    }

    @DrawableRes
    fun poemIllustration(assetKey: String): Int = when (assetKey) {
        "poem_moonlit_boat" -> R.drawable.img_poems_moonlit_boat_1795605697441
        "poem_orchard" -> R.drawable.img_puzzle_sunny_orchard_1795605698407
        "poem_rabbit" -> R.drawable.img_story_rabbit_1795533557150
        "poem_elephant" -> R.drawable.img_story_elephant_1795533556171
        "poem_tiger" -> R.drawable.img_story_tiger_1795533555329
        else -> heroIslandBanner
    }
}
