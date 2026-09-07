# Firebase setup for Kolalagala Temple App

The Android project builds and runs without Firebase configuration, using sample data. Live announcements, events, gallery uploads, admin login, and push notifications become active after Firebase is connected.

## 1. Create the Firebase project
1. Open Firebase Console and create a project.
2. Add an Android app with package name: `com.catools.templeapp`.
3. Download `google-services.json`.
4. Put it at `app/google-services.json`.

## 2. Enable Firebase products
Enable these services in the Firebase Console:
- Authentication -> Sign-in method -> Email/Password
- Cloud Firestore
- Storage
- Cloud Messaging

## 3. Create the temple admin
1. Authentication -> Users -> Add user. Create the admin email/password.
2. Copy that user's UID.
3. Firestore -> create collection `admins`.
4. Create a document whose document ID is exactly the admin UID. It can contain a field such as `role: "admin"`.

The app checks this document before showing the admin management tools.

## 4. Deploy security rules and notification functions
Install Firebase CLI on your computer, sign in, select this Firebase project, then from the repository root run:

```bash
firebase login
firebase use --add
firebase deploy --only firestore:rules,storage,functions
```

The Cloud Functions in `functions/index.js` send push notifications to the `temple_all` topic when a new announcement or event is created with `notifyUsers=true`.

## 5. Build the Firebase-enabled APK
Commit `app/google-services.json` only if the repository is private. If the repository remains public, do not commit that file. Instead configure it securely for CI or build locally in Android Studio.

For a local build:

```bash
./gradlew assembleDebug
```

The APK will be under `app/build/outputs/apk/debug/`.

## Firestore collections used
- `announcements`
- `events`
- `gallery`
- `admins`

## Storage paths used
- `gallery/*`

## Language
The user can switch between Sinhala and English from the top language button or More -> Language. The selected language is stored on the device.
