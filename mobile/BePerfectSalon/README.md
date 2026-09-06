# Be Perfect Unisex Salon - Android App

Native Android app (Kotlin + Jetpack Compose) for a salon business: browse services, book
appointments with a chosen stylist and time slot, manage bookings, view offers/gallery, and
contact the salon. Backend is Firebase (Auth + Firestore).

## Status / known limitation

This was written in a sandboxed environment with **no Android SDK and no network access to
Google's SDK/Maven servers**, so it could not be compiled or run here. Every file was written
and manually reviewed for correctness, but the first Gradle sync on your machine is the first
real compile — budget time for the normal round of small fixes a first sync surfaces (missing
import, a version mismatch), same as any new checkout.

## Tech stack

- **UI**: Jetpack Compose, Material 3, Navigation-Compose
- **Backend**: Firebase Authentication (email/password) + Cloud Firestore + Firebase Storage
- **Images**: Coil
- **Language**: Kotlin, minSdk 26 (Android 8.0+), compileSdk/targetSdk 35

No DI framework (Hilt/Koin) - the app is small enough that a manual `RepositoryProvider`
singleton (see `data/repository/RepositoryProvider.kt`) is simpler and sufficient.

## Project structure

```
app/src/main/java/com/beperfectsalon/app/
  data/model/         Firestore data models (Service, Stylist, Booking, UserProfile, ...)
  data/repository/     Auth, Service, Booking, Content repositories + RepositoryProvider
  data/firebase/       Firestore collection name constants
  ui/theme/            Material 3 color scheme, typography
  ui/navigation/       Screen routes, NavGraph (top-level nav + nested bottom-tab nav)
  ui/components/       Shared composables (cards, loading/error states, status badges)
  ui/screens/          One package per screen, each with its ViewModel next to it
  util/                Resource<T> wrapper, salon hours / time-slot generation
```

## One-time setup

### 1. Create a Firebase project

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a project.
2. Add an Android app with package name **`com.beperfectsalon.app`**.
3. Download the generated `google-services.json` and place it at `app/google-services.json`
   (this file is git-ignored - `app/google-services.json.example` shows the expected shape).
4. In the console, enable:
   - **Authentication** → Sign-in method → Email/Password
   - **Firestore Database** → Create database (start in production mode)
   - **Storage** (optional, only needed if you later let the salon owner upload gallery
     photos from within the app instead of the console)

### 2. Deploy Firestore security rules

Copy the rules in `firestore.rules` (repo root of this app) into Firestore Console → Rules,
or deploy with the Firebase CLI: `firebase deploy --only firestore:rules`.

Rules summary: `services`, `stylists`, `gallery`, `offers` are public-read/no-app-write
(the salon owner manages these from the console); `users/{uid}` and `bookings/{id}` are
locked to the signed-in owner, and a booking can only ever have its `status` field changed
by the app (cancellation), nothing else.

### 3. Seed starter content

`seed/seed_data.json` has example services, stylists, and offers. Add them as documents in
the `services`, `stylists`, and `offers` collections via the Firestore Console (or a small
Admin SDK script) - edit the names/prices/descriptions to match the real salon first.

### 4. Open in Android Studio

1. Open the `mobile/BePerfectSalon` folder as a project (Android Studio Ladybug/2024.2+
   recommended for compileSdk 35 / AGP 8.6 support).
2. Let Gradle sync - this downloads the Android SDK platform/build tools and all
   dependencies, which needs unrestricted internet access to `dl.google.com` and
   `maven.google.com` (not available in the sandbox this app was written in).
3. Run on an emulator or device (`minSdk 26`, i.e. Android 8.0+).

### 5. Replace placeholder branding

- `app/src/main/res/drawable/ic_launcher_foreground.xml` and `ic_launcher_background.xml`
  are a simple ring-and-dot placeholder emblem. Replace via Android Studio's
  **Image Asset Studio** (right-click `res` → New → Image Asset) once you have real branding.
- `app/src/main/res/values/strings.xml` has the salon's phone number, address, and hours -
  update these to the real values.
- `ui/theme/Color.kt` has the wine + rose-gold palette; change to match real brand colors.

## Core features (v1)

- Email/password sign up, login, and guest browsing (booking requires an account)
- Service catalog with category filter and multi-select ("add to booking")
- Appointment booking: pick date (next 14 days) → stylist (or "Any stylist") → time slot
  (slots already booked for that stylist on that date are excluded; slots in the past today
  are excluded) → review → confirm
- My Bookings: list with status (Pending/Confirmed/Completed/Cancelled), cancel action
- Offers list, photo gallery, About & Contact (call / open in Maps)
- Profile: edit name/phone, log out

## Deliberately out of scope for v1 (natural next steps)

- Push notifications for booking reminders/confirmations (Firebase Cloud Messaging dependency
  is already wired in `app/build.gradle.kts`; no notification-sending logic yet)
- An admin/staff app or web console (the salon owner currently manages services/stylists/
  offers/gallery directly in the Firebase Console, or a Cloud Function admin panel later)
- Phone/OTP login (email/password only, to avoid the SHA-cert + Play Integrity setup that
  phone auth requires - straightforward to add later via `FirebaseAuth.verifyPhoneNumber`)
- Payments (no payment collection in-app; salon collects payment in person as today)
