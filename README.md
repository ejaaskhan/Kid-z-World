# KID'Z WORLD — Bilingual 3D Educational Android App

> **Learn • Play • Grow** — *Developed by Dev. E*

**KID'Z WORLD** is an **offline-first, bilingual (English & বাংলা)** native Android educational application designed for children ages **3–8**. Crafted with a Pixar-inspired **3D cartoon claymorphic** interface, drifting pastel skies, rolling hills, floating 3D pill navigation, and a friendly profile-matched mascot companion, KID'Z WORLD turns early childhood learning into a playful daily adventure with zero internet connection required.

---

## Key Features

- **100% Bilingual UI & Built-In Offline Bangladeshi Bangla Audio (Zero System TTS)**: Launches in **বাংলা (Bangla)** by default with an instant **বাং / EN** toggle. Bundles **330 real Bangladeshi Bangla (`bn-BD`) MP3 clips** directly inside the APK (`app/src/main/assets/audio/bangla/`) for instant offline playback via native `MediaPlayer` — no internet, no Android Bangla TTS voice packs, and no phone language setup required. English mode uses lightweight on-device English speech.
- **Bangla Children's Poems (ছড়া)**: Dedicated **Poems** tab & Home card featuring **8 classic traditional Bangla rhymes** with illustrated 3D theatre, rhythmic stanza-by-stanza Bangla recitation with live glow highlighting, Slow/Normal speed toggle, and `+15★` star rewards.
- **4 Complete Educational Learning Modules (247+ Offline Lessons)**:
  1. **Alphabet Adventure**: Full Bangla **স্বরবর্ণ (অ–ঔ) & ব্যঞ্জনবর্ণ** and English **A–Z** with **Learn**, **Crayon Tracing Studio**, and **Quiz** modes, spacious 3D illustrated letter cards, and a full-screen active lesson stage with extruded 3D letters, replayable audio, and star reward bursts.
  2. **Numbers (১–১০০ / 1–100) & Visual Math Lab**: Complete bilingual counting from **1 to 100**, interactive tap-to-count boards, addition & subtraction practice, and auto-scaled difficulty for ages **3–4**, **5–6**, and **7–8**.
  3. **World Exploration**: 7 rich illustrated categories — *Animals & Birds, Fruits & Vegetables, Colors, Shapes, Human Body, Vehicles, and Nature & Environment*.
  4. **Language Studio**: *Bangla Vocabulary, Basic English Words, Everyday Vocabulary, and Simple Sentences* with voice read-aloud and quizzes.
- **Interactive Puzzles & Mini-Games Arcade**: **Picture Jigsaw Puzzle** (`2×2` & `3×3` with Hint Peek), **Animal & Fruit Sorting Puzzle**, **Pattern & Sequence Puzzle**, **3D Memory Match**, and **Sound & Picture Quiz** with star rewards and local high-score tracking.
- **Illustrated Story World**: Curated multi-page bilingual moral stories with page-by-page voice narration and completion badges.
- **Daily Learning Streak & Rewards**: Local **Jetpack DataStore** streak tracker with an animated flame badge, daily bonus stars, and unlockable trophy medallions.
- **Multi-Child Profiles & Math-Gated Parent Zone**: Create independent child profiles (custom name, age 3–8, selectable 3D animal mascot avatar) with a parent multiplication gate guarding analytics, built-in offline Bangla audio status & tester, speech speed controls, bedtime dark mode, and progress reset.
- **Zero Dangerous Permissions & Kid-Safe**: Completely offline; declares only install-time `VIBRATE` and offline English `TTS_SERVICE` discovery.

---

## Screens & Modules

| Area | Screens & Capabilities |
| :--- | :--- |
| **Launch & Setup** | Animated 3D Splash Screen (`KID'Z WORLD` • `Developed by Dev. E`) & 3-step First-Time Onboarding (Language, Feature Tour, Child Profile Setup — replayable anytime). |
| **Home Dashboard** | Animated profile mascot hero, Daily Streak flame badge, **Continue Learning** spotlight, **Daily Challenge** launcher, 7 colorful 3D learning world cards (including **Bangla Poems**), top-right Profile shortcut, and Rewards shelf. |
| **Learn Studio** | 4-Module Hub, **Alphabet Adventure** (`Learn` / `Trace` / `Quiz`), **Full-Screen Active Lesson Stage**, **1–100 Numbers & Math Lab**, **World Exploration**, and **Language Studio**. |
| **Bangla Poems (ছড়া)** | 8 classic Bangla children's rhymes with illustrated 3D stage, stanza-by-stanza built-in Bangla MP3 recitation & live highlight, and Slow/Normal speed toggle. |
| **Games & Puzzles Arcade** | **Picture Jigsaw Puzzle**, **Animal & Fruit Sorting Puzzle**, **Pattern & Sequence Puzzle**, **3D Memory Match**, **Picture & Sound Quiz**, and local Recent High Scores board. |
| **Story World** | Illustrated story shelf & interactive page-turning reader with bundled Bangla MP3 & English **Read Aloud** and moral takeaways. |
| **Profile, Parents & About** | Child switcher, trophy badges, mastery tracker, math-gated **Parent Zone** (with Built-In Offline Bangla Audio status & tester), and **About App** (`Dev. E` studio credits & onboarding replay). |

---

## Technology Stack

- **Language & UI**: Kotlin, Jetpack Compose, Material Design 3, Custom Canvas 3D Artwork
- **Architecture**: MVVM + Clean Modular Layering (`core`, `data`, `di`, `domain`, `ui`), StateFlow & Kotlin Coroutines
- **Local Persistence**: Room SQLite (`KSP`) + Jetpack DataStore Preferences (100% Offline)
- **Audio Engine**: Built-in Offline Bangladeshi Bangla (`bn-BD`) MP3 Library (330 bundled assets via native `MediaPlayer` + `AssetFileDescriptor` with `noCompress += "mp3"`) + English-only `TextToSpeech` + Synthesized PCM Sound FX
- **Navigation**: Jetpack Navigation Compose + Floating 3D Pill Dock (Phones) & Side Navigation Rail (Tablets)
- **Build & CI/CD**: Gradle `9.3.1` (Kotlin DSL), Android Gradle Plugin `9.1.1`, Temurin JDK `21`, GitHub Actions (`minSdk 24`, `targetSdk 36`)

---

## Local Development Instructions

> **Note:** You do **not** need Android Studio on your computer to build releases—GitHub Actions builds APKs and AABs automatically in the cloud. Local instructions below are optional for developers.

### Prerequisites
- **JDK 21** (Eclipse Temurin recommended)
- **Android SDK 36** (`ANDROID_HOME` configured)
- **Gradle 9.3.1** (or Android Studio Meerkat / Narwhal+)

### Commands
```bash
# Run automated unit tests
gradle :app:testDebugUnitTest

# Build Debug APK
gradle :app:assembleDebug

# Build Signed Release APK & AAB (requires upload keystore env vars)
export KEYSTORE_PATH="/path/to/my-upload-key.jks"
export STORE_PASSWORD="your-store-password"
export KEY_PASSWORD="your-key-password"
gradle :app:assembleRelease :app:bundleRelease
```

---

## GitHub Actions Automated Builds

This repository includes a ready-to-run workflow at `.github/workflows/android-release.yml`:

1. **Push to `main` (or manual Run Workflow)**:
   - Runs automated unit tests (`:app:testDebugUnitTest`).
   - If Android signing secrets are **not yet configured**, builds a clearly labeled **Debug Testing APK** (`kidz-world-<version>-debug-testing.apk`) and uploads it under **Actions → Artifacts**.
   - If Android signing secrets **are configured**, builds and cryptographically verifies a signed **Release APK** and **Release AAB** and uploads both under **Actions → Artifacts**.
2. **Push a Version Tag (e.g., `v1.1.0`)**:
   - When signing secrets are configured: builds, verifies (`apksigner`), hashes (`SHA-256`), and automatically publishes a **Signed Production GitHub Release** with both **APK** and **AAB** attached.
   - Before signing secrets are configured: builds a clearly labeled **Debug Testing APK & AAB** and publishes a **GitHub Pre-Release** (`NOT production signed`) so you can still test on a device immediately.

See [`docs/BUILD_AND_RELEASE.md`](docs/BUILD_AND_RELEASE.md) and [`docs/ANDROID_SIGNING.md`](docs/ANDROID_SIGNING.md) for complete setup steps.

---

## Downloading the APK

- **From GitHub Releases (Signed Production APK & AAB)**:
  1. Open your repository on GitHub and click **Releases** on the right sidebar.
  2. Open the latest version (e.g., **KID'Z WORLD v1.1.0**).
  3. Download **`kidz-world-v1.1.0-release-signed.apk`** to install on Android, or **`kidz-world-v1.1.0-release-signed.aab`** to upload to Google Play Console.
- **From GitHub Actions Artifacts (Branch Builds)**:
  1. Open the **Actions** tab and click the latest **Android Build & Release** run.
  2. Scroll to **Artifacts** and download **`kidz-world-apk-*`** and **`kidz-world-aab-*`**.

---

## Creating a Release

```bash
# 1. Commit your finalized changes to main
git add .
git commit -m "Release v1.1.0"
git push origin main

# 2. Tag and push to trigger an automatic signed GitHub Release
git tag v1.1.0
git push origin v1.1.0
```

---

## License

Released under the **MIT License** — see [`LICENSE`](LICENSE).  
**Developed by Dev. E**
