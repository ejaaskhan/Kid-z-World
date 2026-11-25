# Changelog

All notable changes to **KID'Z WORLD** (by **Dev. E**) are documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and [Semantic Versioning](https://semver.org/).

## [1.1.0] - 2026-11-25

### Added
- **Bangla Children's Poems (ছড়া) Section**: Dedicated **Poems (`ছড়া`)** tab in the floating 3D dock & tablet rail plus a Home World card featuring **8 classic traditional Bangla rhymes** (*আয় আয় চাঁদ মামা*, *হাট্টিমাটিম টিম*, *আতা গাছে তোতা পাখি*, *আয় বৃষ্টি ঝেঁপে*, *তাই তাই তাই*, *নোটন নোটন পায়রাগুলো*, *খোকন খোকন ডাক পাড়ি*, *আম পাতা জোড়া জোড়া*), illustrated 3D theatre, rhythmic stanza-by-stanza Bangla voice recitation with live glow highlighting, Slow/Normal pace toggle, tap-any-stanza replay, and `+15★` star rewards saved in Room.
- **3 New Child-Friendly Puzzle Games** in Games Arcade:
  - **Picture Jigsaw Puzzle**: `2×2` (Easy) and `3×3` (Fun) tap-to-swap puzzle boards featuring 5 bundled 3D Pixar-style artworks and a helpful **Hint Peek** preview.
  - **Animal & Fruit Sorting Puzzle**: 18 bilingual items sorted into Animals, Fruits, and Vehicles baskets with automatic voice pronunciation.
  - **Pattern & Sequence Puzzle**: 5-round visual logic puzzle (ABAB, AABB, ABC) with glowing mystery slot and instant cheerful audio feedback.
- **Natural Bangla & English Offline Voice Pack Engine**: Automatically prioritizes Google Speech Services (`com.google.android.tts`) and selects the highest-quality local offline Bangla (`bn-BD` / `bn-IN`) and English (`en-US` / `en-GB`) voices with warm child-friendly pitch and pacing (`0.84x` default), plus a **one-tap Bangla voice pack installer** (`ACTION_INSTALL_TTS_DATA`) and interactive voice test buttons in Parent Zone.

### Changed
- **Bangla (`বাংলা`) as Default Language**: Fresh installs launch directly in **বাংলা** (with Bangla alphabet default in Learn Studio and Bangla-first Onboarding) while preserving instant one-tap **বাং / EN** switching across every screen.
- **Child UX Optimizations**: Animated speaking-wave visual indicators (`SpeakingWaveBadge`), larger kid-safe touch targets, top-right Profile quick access on Home, and updated GitHub Actions CI/CD workflow defaults for `v1.1.0`.

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
