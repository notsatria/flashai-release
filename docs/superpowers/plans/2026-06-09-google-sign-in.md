# Google Sign-In Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Authenticate users through Google Credential Manager and Firebase from the existing login screen.

**Architecture:** Compose launches the Android Credential Manager chooser and passes its Google ID token to `LoginViewModel`. The ViewModel calls the existing `AuthRepository` Google sign-in contract, while Firebase credential exchange remains in `FirebaseAuthRepository`.

**Tech Stack:** Android, Kotlin, Jetpack Compose, Android Credential Manager, Google ID, Firebase Authentication, Koin.

---

### Task 1: Complete Google Sign-In Configuration

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/java/com/notsatria/flashai/di/AppModule.kt`

- [x] Add the core Credential Manager dependency.
- [x] Expose `GOOGLE_WEB_CLIENT_ID` from `local.properties` through BuildConfig.
- [x] Remove the incomplete Google service binding from Koin.

### Task 2: Add Login State And Firebase Trigger

**Files:**
- Modify: `app/src/main/java/com/notsatria/flashai/ui/screens/login/LoginViewModel.kt`

- [x] Add a Google ID-token login function using the existing loading, success, logging, and snackbar patterns.

### Task 3: Add Credential Manager UI Flow

**Files:**
- Modify: `app/src/main/java/com/notsatria/flashai/ui/screens/login/LoginScreen.kt`
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values-in/strings.xml`

- [x] Add localized Google sign-in and error strings.
- [x] Launch Credential Manager when the Google button is tapped.
- [x] Parse Google ID credentials and forward the token to the ViewModel.
- [x] Add the Google action and divider to the existing form.

### Task 4: Verify

- [x] Run `./gradlew :app:compileDebugKotlin`.
- [x] Run `./gradlew :app:testDebugUnitTest`.
- [x] Confirm configuration instructions for `GOOGLE_WEB_CLIENT_ID`.
