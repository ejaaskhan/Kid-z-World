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

object LocalAssetRegistry {

    @DrawableRes
    val heroIslandBanner: Int = R.drawable.img_hero_island_1795533556354

    @DrawableRes
    val appCrestArtwork: Int = R.drawable.img_kidz_icon_1795533561471

    val avatars: List<AvatarAsset> = listOf(
        AvatarAsset("owl", "🦉", "Wise Owl", "জ্ঞানী পেঁচা", Color(0xFF4CC3FF), Color(0xFF198AD1)),
        AvatarAsset("tiger", "🐯", "Brave Tiger", "সাহসী বাঘ", Color(0xFFFF9A2E), Color(0xFFD66A00)),
        AvatarAsset("rabbit", "🐰", "Sunny Bunny", "মিষ্টি খরগোশ", Color(0xFFFF6B9E), Color(0xFFD1346A)),
        AvatarAsset("elephant", "🐘", "Kind Ellie", "দয়ালু হাতি", Color(0xFF9C72FF), Color(0xFF6B3FD9)),
        AvatarAsset("panda", "🐼", "Happy Panda", "খুশি পান্ডা", Color(0xFF34D484), Color(0xFF129E58)),
        AvatarAsset("fox", "🦊", "Clever Fox", "চালাক শিয়াল", Color(0xFFFFB830), Color(0xFFCC8400))
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
}
