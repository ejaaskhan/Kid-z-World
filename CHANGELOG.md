# Changelog

All notable changes to **KID'Z WORLD** (by **Dev. E**) are documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and [Semantic Versioning](https://semver.org/).

## [1.1.1] - 2026-11-25

### Added
- **Dedicated 3D Cartoon Settings Screen**: Directly accessible from both the Home header and Profile Hub, neatly organized into **General** (*Theme Mode* & *Language*), **Sound** (*UI Sound On/Off*, *Educational Voice Narration*, *Voice Speed*, and *Live Audio Testers*), and **About** (*App Information* & *Developer Website*).
- **Light Mode, Dark Mode & System Default Theme**: Full Material 3 Light and Cozy Bedtime Dark color schemes plus dynamic **System Default** (`isSystemInDarkTheme()`), persisted locally in Jetpack DataStore with backward-compatible migration and instant switching (including Android status/navigation bar contrast) without restarting the app.
- **Clickable Developer Website Link (`Dev. E`)**: Both the Settings → About card and the About KID'Z WORLD screen display `KID'Z WORLD`, `Dev. E` (with external-link icon), and `Learn • Play • Grow`, opening `https://ebrahimjamansumon.blogspot.com` in the external browser via a crash-safe Android intent launcher with localized error feedback.

### Improved
- **Independent Global UI Sound On/Off**: Centralized in `KidzAudioEngine` + `SettingsDataStore` so turning off UI Sound silences all button taps, navigation clicks, and UI chimes while keeping bundled Bangla MP3 audio, English pronunciation, poems, and story narration completely independent.
- **Natural Bangla & English Voice Quality**: Preserved all 330 bundled offline Bangladeshi Bangla (`bn-BD`) MP3 files at natural `1.0x` studio cadence while upgrading English TTS to prefer Google Speech Services (with automatic default fallback), prioritize local high-quality/neural `en-US`/`en-GB` voices over robotic synthesizers, lock pitch strictly to natural `1.0f` (zero artificial pitch shift), and apply comfortable child-friendly pacing (`0.86x` default) with gentle pauses.
- **Version & CI Workflow Update**: Bumped app version to `1.1.1` (`versionCode = 3`) without resetting user data or progress, and updated `.github/workflows/android-release.yml` defaults and release notes for `v1.1.1`.

## [1.1.0] - 2026-11-25

### Added
- **Bangla Children's Poems (ছড়া) Section**: Dedicated **Poems (`ছড়া`)** tab in the floating 3D dock & tablet rail plus a Home World card featuring **8 classic traditional Bangla rhymes** (*আয় আয় চাঁদ মামা*, *হাট্টিমাটিম টিম*, *আতা গাছে তোতা পাখি*, *আয় বৃষ্টি ঝেঁপে*, *তাই তাই তাই*, *নোটন নোটন পায়রাগুলো*, *খোকন খোকন ডাক পাড়ি*, *আম পাতা জোড়া জোড়া*), illustrated 3D theatre, rhythmic stanza-by-stanza Bangla voice recitation with live glow highlighting, Slow/Normal pace toggle, tap-any-stanza replay, and `+15★` star rewards saved in Room.
- **3 New Child-Friendly Puzzle Games** in Games Arcade:
  - **Picture Jigsaw Puzzle**: `2×2` (Easy) and `3×3` (Fun) tap-to-swap puzzle boards featuring 5 bundled 3D Pixar-style artworks and a helpful **Hint Peek** preview.
  - **Animal & Fruit Sorting Puzzle**: 18 bilingual items sorted into Animals, Fruits, and Vehicles baskets with automatic voice pronunciation.
  - **Pattern & Sequence Puzzle**: 5-round visual logic puzzle (ABAB, AABB, ABC) with glowing mystery slot and instant cheerful audio feedback.
- **Built-In Offline Bangladeshi Bangla Audio Library (Zero Android System TTS)**: Bundled **330 real pre-recorded Bangladeshi Bangla (`bn-BD`) MP3 files** directly inside `app/src/main/assets/audio/bangla/` (with `manifest.json` and uncompressed APK packaging via `noCompress += "mp3"`) covering Bangla alphabets (`অ–ঔ` & `ক–ন`), English `A–Z` Bangla support, numbers `0–100`, fruits, animals, colors, shapes, body/vehicles/nature/vocabulary, learning instructions, math operators, all 8 Bangla poems (title + every stanza), and all 3 illustrated stories (title + every page + moral). Completely removed Android System TTS, Google Speech Services checks, voice-pack install prompts, and Android TTS settings navigation for Bangla; playback uses native Android `MediaPlayer` with zero overlap, replay, stop, and speed control.

### Changed
- **Bangla (`বাংলা`) as Default Language**: Fresh installs launch directly in **বাংলা** (with Bangla alphabet default in Learn Studio and Bangla-first Onboarding) while preserving instant one-tap **বাং / EN** switching across every screen.
- **Child UX Optimizations & CI Asset Verification**: Animated speaking-wave visual indicators (`SpeakingWaveBadge`), larger kid-safe touch targets, top-right Profile quick access on Home, bundled audio health status & test buttons in Parent Zone, and GitHub Actions verification that all 330 Bangla MP3 files are packaged inside every APK.

## [1.0.0] - 2026-11-24

### Added
- **Pixar-Inspired 3D Cartoon UI**: Animated pastel drifting-cloud sky, layered storybook rolling hills, glossy 3D claymorphic cards and buttons, floating 3D pill bottom navigation dock (with tablet side rail), and active profile-matched 3D mascot companion.
- **Module 1 — Alphabet Adventure**: Full English **A–Z** and Bangla **স্বরবর্ণ (অ–ঔ) & ব্যঞ্জনবর্ণ** with **Learn**, **Crayon Tracing Studio**, and **Quiz** modes plus a full-screen active lesson stage with extruded 3D letters, custom glossy artwork, replayable audio, and star reward bursts.
- **Module 2 — Numbers (1–100 / ১–১০০) & Visual Math Lab**: Complete bilingual 1–100 counting, interactive counting tiles, addition/subtraction practice, and auto-scaled difficulty for ages 3–4, 5–6, and 7–8.
- **Module 3 — World Exploration**: Illustrated offline lessons for Animals & Birds, Fruits & Vegetables, Colors, Shapes, Human Body, Vehicles, and Nature & Environment.
- **Module 4 — Language Studio**: Basic English words, everyday vocabulary, simple sentences, and Bangla vocabulary with interactive quizzes.
- **Home World Hub**: Personalized welcome hero, animated Daily Learning Streak badge (Jetpack DataStore), **Continue Learning** spotlight, **Daily Challenge** launcher, 6 learning world cards, and Rewards shelf.
- **Mini-Games & Story World**: 3D Memory Match, Sound & Picture Quiz, and 3 illustrated bilingual moral stories with page-by-page Read Aloud.
- **Multi-Child Profiles & Math-Gated Parent Zone**: Local Room SQLite persistence for profiles, lesson mastery, stories, and quiz scores; bedtime dark mode, speech speed controls, and About App (`Developed by Dev. E`).
- **GitHub Actions CI/CD Release Automation**: Automated unit testing, dual-mode signing validation, `apksigner` verification, downloadable APK & AAB artifacts, and tagged GitHub Releases (`v*`).
