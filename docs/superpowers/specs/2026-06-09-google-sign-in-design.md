# Google Sign-In Design

## Goal

Add a "Continue with Google" action to the existing login screen that authenticates users with Firebase Authentication and navigates to Home on success.

## Architecture

The Compose login screen owns the Android Credential Manager account chooser because it requires an Activity context. After Credential Manager returns a Google ID token, the screen passes it to `LoginViewModel`, which delegates Firebase authentication to `AuthRepository`.

The Google web client ID is read from `local.properties` into `BuildConfig.GOOGLE_WEB_CLIENT_ID`. This keeps environment-specific configuration out of source control and allows compilation when Firebase configuration is unavailable. The value must be the OAuth 2.0 **Web application** client ID associated with the Firebase project, not the Android client ID.

## User Flow

1. The user taps "Continue with Google".
2. Credential Manager displays the Google account chooser.
3. The returned credential is parsed as a Google ID token.
4. `LoginViewModel` sends the token to Firebase through `AuthRepository`.
5. Successful authentication uses the existing login-success navigation to Home.
6. Account-chooser cancellation leaves the user on Login; configuration and authentication failures appear in the existing snackbar.

## UI And State

The Google button appears below an "or" divider inside the existing login form. Email and Google sign-in share `LoginUiState.isLoading`, preventing overlapping authentication attempts.

## Verification

Compile the Android app and manually verify successful sign-in, chooser cancellation, and invalid/missing client-ID behavior on a device with Google Play services.
