# Build & Release Guide — KID'Z WORLD

This guide explains how to build, test, and publish **KID'Z WORLD** directly from GitHub Actions—**without requiring Android Studio on your personal computer**.

---

## 1. Release Configuration Summary

| Setting | Value |
| :--- | :--- |
| **App Name** | `Kid'z World` (Launcher) / `KID'Z WORLD` (In-App Brand) |
| **Developer** | `Dev. E` |
| **Application ID** | `com.aistudio.kidzworld.devekw` |
| **Version** | `1.1.0` (`versionCode = 2`) |
| **Min / Target / Compile SDK** | `minSdk = 24` (Android 7.0) / `targetSdk = 36` / `compileSdk = 36` |
| **Toolchain** | Temurin JDK `21` + Gradle `9.3.1` + Android Gradle Plugin `9.1.1` |
| **R8 Code Shrinking** | Disabled (`isMinifyEnabled = false`) for safe `v1.1.0` release; keep rules prepared in `app/proguard-rules.pro` |

---

## 2. How GitHub Actions Works (`.github/workflows/android-release.yml`)

Every workflow run executes on `ubuntu-latest` with pinned Node 24 official actions (`actions/checkout@v5`, `actions/setup-java@v5`, `gradle/actions/setup-gradle@v6`, `actions/upload-artifact@v6`) and GitHub's built-in `gh` CLI for attaching `.apk` and `.aab` release assets:

| Trigger | Signing Secrets Configured? | Result |
| :--- | :--- | :--- |
| Push to `main` or Manual Dispatch | **No** | Runs tests + builds **Debug Testing APK & AAB** uploaded as separate **Actions → Artifacts** (`kidz-world-apk-*` and `kidz-world-aab-*`). |
| Push to `main` or Manual Dispatch | **Yes** | Runs tests + builds & verifies **Signed Release APK & AAB** uploaded as separate **Actions → Artifacts**. |
| Push Version Tag (`v1.0.0`, etc.), Publish Release in GitHub UI, or Manual Dispatch (`publish_release = true`) | **No** | Runs tests + builds **Debug Testing APK & AAB** and attaches both files to a clearly labeled **GitHub Pre-Release** (`NOT production signed`). |
| Push Version Tag (`v1.0.0`, etc.), Publish Release in GitHub UI, or Manual Dispatch (`publish_release = true`) | **Yes** | Runs tests + builds & verifies **Signed Release APK & AAB** + attaches downloadable APK, AAB, and `SHA256SUMS.txt` directly to the **Production GitHub Release**. |

> **Tip:** After creating or publishing a Release in the GitHub UI, wait **~3–5 minutes** for the **Actions → Android Build & Release** run to turn green, then refresh the Release page to see the `.apk` and `.aab` under **Assets**.

---

## 3. Uploading the Project to GitHub

1. Create a new empty repository on GitHub (e.g., `kidz-world-android`). Do **not** initialize with a README or license (this project already includes them).
2. From the root folder of your project, run:

```bash
git init
git branch -M main
git add .
git commit -m "Initial release-ready KID'Z WORLD Android app (v1.0.0)"
git remote add origin https://github.com/<YOUR_USERNAME>/<YOUR_REPO>.git
git push -u origin main
```

3. Open the **Actions** tab on GitHub to watch your first automated build complete.

---

## 4. Publishing an Automatic GitHub Release

1. Ensure your 3 signing secrets are added to GitHub (see [`ANDROID_SIGNING.md`](ANDROID_SIGNING.md)).
2. For future updates, bump `versionCode` (integer) and `versionName` (e.g., `"1.0.1"`) in `app/build.gradle.kts`, update `CHANGELOG.md`, and push to `main`.
3. Create and push the matching semantic version tag:

```bash
git tag v1.1.0
git push origin v1.1.0
```

4. Wait ~3–5 minutes for the **Android Build & Release** workflow to finish.
5. Open **GitHub → Releases → KID'Z WORLD v1.1.0** to download:
   - `kidz-world-v1.1.0-release-signed.apk` (install directly on Android devices)
   - `kidz-world-v1.1.0-release-signed.aab` (upload to Google Play Console)
   - `SHA256SUMS.txt` (cryptographic checksums)

---

## 5. Exact Output Paths (Local Gradle Builds)

When building locally with Gradle, outputs are written to:

- **Debug Testing APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Signed Release APK**: `app/build/outputs/apk/release/app-release.apk`
- **Signed Release AAB**: `app/build/outputs/bundle/release/app-release.aab`
