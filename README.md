# KID'Z WORLD — Bilingual 3D Educational Android App

> **Learn • Play • Grow** — *Developed by Dev. E*

**KID'Z WORLD** is an **offline-first, bilingual (English & বাংলা)** native Android educational application designed for children ages **3–8**. Crafted with a Pixar-inspired **3D cartoon claymorphic** interface, drifting pastel skies, rolling hills, floating 3D pill navigation, and a friendly profile-matched mascot companion, KID'Z WORLD turns early childhood learning into a playful daily adventure with zero internet connection required.

---

## Key Features

- **100% Bilingual UI & Audio (English & বাংলা)**: Instant in-app **EN / বাং** toggle updates every screen, lesson, story, Bengali numeral (`০–৯`), and offline voice pronunciation without restarting.
- **4 Complete Educational Learning Modules (247+ Offline Lessons)**:
  1. **Alphabet Adventure**: Full English **A–Z** and Bangla **স্বরবর্ণ (অ–ঔ) & ব্যঞ্জনবর্ণ** with **Learn**, **Crayon Tracing Studio**, and **Quiz** modes, spacious 3D illustrated letter cards, and a full-screen active lesson stage with extruded 3D letters, replayable audio, and star reward bursts.
  2. **Numbers (1–100 / ১–১০০) & Visual Math Lab**: Complete bilingual counting from **1 to 100**, interactive tap-to-count boards, addition & subtraction practice, and auto-scaled difficulty for ages **3–4**, **5–6**, and **7–8**.
  3. **World Exploration**: 7 rich illustrated categories — *Animals & Birds, Fruits & Vegetables, Colors, Shapes, Human Body, Vehicles, and Nature & Environment*.
  4. **Language Studio**: *Basic English Words, Everyday Vocabulary, Simple Sentences, and Bangla Vocabulary* with voice read-aloud and quizzes.
- **Interactive Mini-Games Arcade**: **3D Memory Match** (Easy & Challenge modes) and **Sound & Picture Quiz** with star rewards and local high-score tracking.
- **Illustrated Story World**: Curated multi-page bilingual moral stories with page-by-page voice narration and completion badges.
- **Daily Learning Streak & Rewards**: Local **Jetpack DataStore** streak tracker with an animated flame badge, daily bonus stars, and unlockable trophy medallions.
- **Multi-Child Profiles & Math-Gated Parent Zone**: Create independent child profiles (custom name, age 3–8, selectable 3D animal mascot avatar) with a parent multiplication gate guarding analytics, audio/speech rate controls, bedtime dark mode, and progress reset.
- **Zero Dangerous Permissions & Kid-Safe**: Completely offline; declares only install-time `VIBRATE` and offline `TTS_SERVICE` engine discovery.

---

## Screens & Modules

| Area | Screens & Capabilities |
| :--- | :--- |
| **Launch & Setup** | Animated 3D Splash Screen (`KID'Z WORLD` • `Developed by Dev. E`) & 3-step First-Time Onboarding (Language, Feature Tour, Child Profile Setup — replayable anytime). |
| **Home Dashboard** | Animated profile mascot hero, Daily Streak flame badge, **Continue Learning** spotlight, **Daily Challenge** launcher, 6 colorful 3D learning world cards, and Rewards shelf. |
| **Learn Studio** | 4-Module Hub, **Alphabet Adventure** (`Learn` / `Trace` / `Quiz`), **Full-Screen Active Lesson Stage**, **1–100 Numbers & Math Lab**, **World Exploration**, and **Language Studio**. |
| **Games Arcade** | **3D Memory Match**, **Picture & Sound Quiz**, and local Recent High Scores board. |
| **Story World** | Illustrated story shelf & interactive page-turning reader with bilingual **Read Aloud** and moral takeaways. |
| **Profile, Parents & About** | Child switcher, trophy badges, mastery tracker, math-gated **Parent Zone**, and **About App** (`Dev. E` studio credits & onboarding replay). |

---

## Technology Stack

- **Language & UI**: Kotlin, Jetpack Compose, Material Design 3, Custom Canvas 3D Artwork
- **Architecture**: MVVM + Clean Modular Layering (`core`, `data`, `di`, `domain`, `ui`), StateFlow & Kotlin Coroutines
- **Local Persistence**: Room SQLite (`KSP`) + Jetpack DataStore Preferences (100% Offline)
- **Audio Engine**: Android On-Device `TextToSpeech` (`en-US` / `bn-BD`) + Synthesized PCM Sound FX
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
2. **Push a Version Tag (e.g., `v1.0.0`)**:
   - When signing secrets are configured: builds, verifies (`apksigner`), hashes (`SHA-256`), and automatically publishes a **Signed Production GitHub Release** with both **APK** and **AAB** attached.
   - Before signing secrets are configured: builds a clearly labeled **Debug Testing APK** and publishes a **GitHub Pre-Release** (`NOT production signed`) so you can still test on a device immediately.

See [`docs/BUILD_AND_RELEASE.md`](docs/BUILD_AND_RELEASE.md) and [`docs/ANDROID_SIGNING.md`](docs/ANDROID_SIGNING.md) for complete setup steps.

---

## Downloading the APK

- **From GitHub Releases (Signed Production APK & AAB)**:
  1. Open your repository on GitHub and click **Releases** on the right sidebar.
  2. Open the latest version (e.g., **KID'Z WORLD v1.0.0**).
  3. Download **`kidz-world-v1.0.0-release-signed.apk`** to install on Android, or **`kidz-world-v1.0.0-release-signed.aab`** to upload to Google Play Console.
- **From GitHub Actions Artifacts (Branch Builds)**:
  1. Open the **Actions** tab and click the latest **Android Build & Release** run.
  2. Scroll to **Artifacts** and download **`kidz-world-release-signed`** (or **`kidz-world-debug-testing`** before signing secrets are added).

---

## Creating a Release

```bash
# 1. Commit your finalized changes to main
git add .
git commit -m "Release v1.0.0"
git push origin main

# 2. Tag and push to trigger an automatic signed GitHub Release
git tag v1.0.0
git push origin v1.0.0
```

---

## License

Released under the **MIT License** — see [`LICENSE`](LICENSE).  
**Developed by Dev. E**
