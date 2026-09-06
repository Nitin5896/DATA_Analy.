# Deploying to Google Play

This is a checklist, in order. Steps marked **(you)** need your Google account, your money,
or your own device/keys - nobody else can do them for you. Steps marked **(ready)** are
already prepared in this repo.

## 1. Google Play Console account **(you)**

1. Go to [play.google.com/console](https://play.google.com/console/) and sign up as a
   developer. One-time **$25 USD** registration fee, paid by you.
2. Complete identity verification (can take a day or more the first time).

## 2. Generate your release (upload) keystore **(you)**

```
cd mobile/BePerfectSalon
./scripts/generate_release_keystore.sh
```

This prompts for passwords and your name/organization (standard `keytool` prompts), then
creates `release-keystore.jks` in this folder.

**Back this file up immediately** - a password manager vault or encrypted drive, not just
your laptop. Then:

```
cp keystore.properties.example keystore.properties
```

Edit `keystore.properties` and fill in the store/key passwords you just chose. This file is
git-ignored - never commit it or the `.jks` file.

When you create the app in Play Console, accept enrollment in **Play App Signing** (the
default) - Google then holds the actual signing key and re-signs what you upload with it, so
losing your upload keystore later is recoverable (Google has a key-reset process), unlike the
old model where losing it meant losing the ability to update the app forever.

## 3. Real Firebase project **(you, if not already done)**

Follow `README.md` section "1. Create a Firebase project" if you haven't already: create the
project, add the Android app with package `com.beperfectsalon.app`, download the real
`app/google-services.json`, enable Email/Password auth and Firestore, and deploy
`firestore.rules`. Seed real service/stylist/offer data (`seed/seed_data.json` as a starting
template) - the seed file's placeholder gallery image URL points at `images.example.com` and
must be replaced with real photo URLs before you go live.

## 4. Host the privacy policy **(you)**

Play Console requires a publicly reachable URL, not just a file in this repo. Fill in the
placeholders in `PRIVACY_POLICY.md` (business name, contact email, effective date), then host
it somewhere - the easiest options:

- **GitHub Pages**: enable Pages on this repo (Settings → Pages), pointing at a branch/folder
  containing the policy as an `.html` or `.md` file GitHub Pages can render.
- Any existing website the salon has - just add a `/privacy` page with this content.

Put the resulting URL into Play Console's Store Listing and Data Safety sections.

## 5. Build the release bundle **(ready to run, once 2-3 are done)**

Play Console requires an **Android App Bundle** (`.aab`), not an APK:

```
cd mobile/BePerfectSalon
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`. This only produces a signed,
uploadable bundle once `keystore.properties` exists (step 2) and `app/google-services.json`
is the real one (step 3) - both are git-ignored and must exist locally.

## 6. Fill in the store listing **(you, content ready)**

Use `STORE_LISTING.md` for the app description, category, and Data Safety form answers.
You still need to personally:
- Create the app icon graphic, feature graphic, and phone screenshots (see
  `STORE_LISTING.md` for exact sizes) - these are images, not something generated from code.
- Answer the content rating questionnaire (expect "Everyone").
- Set countries/pricing (this app has no in-app purchases, so pricing is just "free").

## 7. Internal testing track first **(you)**

Don't go straight to production. In Play Console, create an **Internal testing** release,
upload the `.aab`, add yourself (and anyone else helping test) as a tester by email, and
install the app via the opt-in link Play Console gives you. Actually go through: sign up,
browse services, book an appointment, cancel a booking, edit profile, delete account. Fix
anything broken before wider release.

## 8. Roll out to production **(you)**

Once internal testing looks right: Production → Create release → upload the same (or a new)
`.aab` → submit for review. Google's review typically takes anywhere from a few hours to a
few days for a first submission.

## Ongoing: updating the app later

Bump `versionCode` (must always increase) and `versionName` in
`app/build.gradle.kts`, rebuild (`./gradlew bundleRelease`), and upload the new `.aab` as a
new release in Play Console - the same `keystore.properties` signs every future update.

## What's already done for you in this repo

- Release build type configured for signing via `keystore.properties` (never hardcoded)
- Gradle wrapper committed (`./gradlew` works without a system Gradle install)
- `.gitignore` covers the keystore, its passwords, and `google-services.json`
- Firestore security rules, including the account-deletion path
- In-app account deletion (Profile → Delete My Account) satisfying Play's account-deletion
  policy requirement
- `PRIVACY_POLICY.md` and `STORE_LISTING.md` drafted from what the app's code actually
  collects and does - fill in the bracketed placeholders and you're ready to publish
