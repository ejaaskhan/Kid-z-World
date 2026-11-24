# Android Release Signing Setup — KID'Z WORLD

KID'Z WORLD uses secure environment-variable signing in `app/build.gradle.kts` (`KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`, with fixed alias `upload`) so **no keystore files, passwords, or private keys are ever committed to Git**.

---

## Step 1: Generate Your Upload Keystore (One-Time)

You only need `keytool` (included with any Java JDK installation—no Android Studio required):

```bash
keytool -genkeypair -v \
  -keystore my-upload-key.jks \
  -alias upload \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

> **CRITICAL:** Keep `-alias upload` exact! The release signing block in `app/build.gradle.kts` uses `keyAlias = "upload"`.

When prompted:
- Choose a strong **Keystore Password** (store it safely in a password manager).
- Choose the same or another strong **Key Password**.
- Fill in your developer info (e.g., `Dev. E`) and confirm with `yes`.

**Back up `my-upload-key.jks` safely offline!** If you lose this file after publishing to Google Play, you cannot update the app without requesting a Play App Signing upload key reset.

---

## Step 2: Convert the Keystore to Base64

### macOS
```bash
base64 -i my-upload-key.jks | pbcopy
```

### Linux
```bash
base64 -w 0 my-upload-key.jks > keystore.b64
cat keystore.b64
```

### Windows (PowerShell)
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("my-upload-key.jks")) | Set-Clipboard
```

> Delete any temporary `keystore.b64` text file immediately after copying it to GitHub Secrets.

---

## Step 3: Add Secrets to GitHub Actions

1. Open your GitHub repository in a browser.
2. Go to **Settings → Secrets and variables → Actions**.
3. Click **New repository secret** and add these **exact three secrets**:

| Secret Name | Value |
| :--- | :--- |
| `ANDROID_KEYSTORE_BASE64` | Full single-line Base64 string from Step 2 |
| `ANDROID_STORE_PASSWORD` | The keystore password you entered in Step 1 |
| `ANDROID_KEY_PASSWORD` | The key password you entered in Step 1 |

---

## Step 4: Verify Security & Behavior

- `.gitignore` explicitly blocks `*.jks`, `*.keystore`, `*.p12`, `signing.properties`, `key.properties`, and `.env` from being committed.
- During GitHub Actions builds, the keystore is decoded only into `${{ runner.temp }}/upload-keystore.jks` (outside the repository folder) and deleted automatically in an `if: always()` cleanup step.
- Every signed release APK is verified with `apksigner verify` before being uploaded or attached to a GitHub Release.
- If you push a `v*` tag before adding these three secrets, GitHub Actions will **fail cleanly** instead of publishing an unsigned/debug APK as a production release.
- See `signing.properties.example` at the project root for a secret-free local reference template.
