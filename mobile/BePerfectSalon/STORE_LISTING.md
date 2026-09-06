# Play Store Listing Content

Copy these into Play Console → Grow → Store presence → Main store listing. Edit anything in
`[BRACKETS]` before you publish - fill in the salon's real details.

## App details

| Field | Value |
|---|---|
| App name | Be Perfect Unisex Salon |
| Category | Lifestyle (or Beauty, if offered in your region) |
| Tags | salon, beauty, hair, booking, appointment |
| Contact email | [FILL IN] |
| Contact phone (optional) | [FILL IN] |
| Website (optional) | [FILL IN] |
| Privacy policy URL | [URL where you hosted PRIVACY_POLICY.md] |

## Short description (max 80 characters)

```
Book your salon appointment in seconds - services, stylists, offers & more.
```
(76 characters)

## Full description (max 4000 characters)

```
Be Perfect Unisex Salon puts the salon in your pocket. Browse our full service menu,
pick your favorite stylist, and book your next appointment in under a minute - no phone
call needed.

WHAT YOU CAN DO
• Browse services by category (hair, skin, nails, grooming, bridal and more) with clear
  pricing and duration
• Book an appointment: choose your date, pick a stylist (or let us assign one), and select
  an available time slot
• View and manage your bookings, with the option to cancel if your plans change
• Check out current offers and promotions before you book
• Browse our photo gallery to see our work and salon space
• Get directions and call the salon directly from the app

WHY BE PERFECT
[FILL IN 2-3 sentences about what makes this salon distinctive - years in business,
specialties, awards, standout stylists, etc.]

Download the app and book your next visit with Be Perfect Unisex Salon today.
```

## Graphic assets you still need to create

Play Console requires actual image files - these are not something written in code:

- **App icon** (512x512 PNG): replace the placeholder ring emblem
  (`app/src/main/res/drawable/ic_launcher_*.xml`) with real branding via Android Studio's
  Image Asset tool, then export a 512x512 PNG for the store listing.
- **Feature graphic** (1024x500 PNG/JPEG): a banner image shown at the top of the listing.
- **Phone screenshots** (min 2, recommended 4-8): take these from the running app once you
  have it built - Home, Services, Book Appointment, and My Bookings are good choices.

## Data Safety form answers (Play Console → App content → Data safety)

Based on what the app's code actually collects and sends (see `data/repository/` and
`data/model/Models.kt`):

| Data type | Collected? | Shared with third parties? | Purpose |
|---|---|---|---|
| Name | Yes | No | Account management, App functionality |
| Email address | Yes | No | Account management, App functionality |
| Phone number | Yes | No | Account management, App functionality |
| App activity (booking history) | Yes | No | App functionality |
| Precise/approximate location | No | - | - |
| Photos/videos/audio | No | - | - |
| Financial info | No | - | - |

- Data is encrypted in transit (Firebase enforces TLS).
- Users can request account and data deletion in-app (Profile → Delete My Account) - Play
  Console's Data Safety form has a specific question for this; answer "Yes" and link to the
  in-app flow (no separate web form is needed since deletion works from inside the app).
- Third-party processor: Google Firebase (Authentication + Cloud Firestore) - the app's own
  developer/company is the only party controlling this data beyond that; no ad networks or
  analytics SDKs are integrated.

## Content rating questionnaire

This is a business utility app with no user-generated public content, violence, or mature
themes - expect an "Everyone" rating. Answer the Play Console questionnaire honestly; it's
short for an app like this.
