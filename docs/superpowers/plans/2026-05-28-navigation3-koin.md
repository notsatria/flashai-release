# Navigation 3 Koin Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a minimal Navigation 3 setup backed by Koin dependency injection.

**Architecture:** The app starts Koin from an Android `Application`, registers a singleton navigator, and renders a saveable Navigation 3 back stack from `AppNavigation`. Routes are type-safe serializable keys and screens receive navigation callbacks through Koin navigation entries.

**Tech Stack:** Android, Kotlin, Jetpack Compose, Navigation 3, Koin 4.1.

---

### Task 1: Koin Bootstrap

**Files:**
- Create: `app/src/main/java/com/notsatria/starter/StarterApplication.kt`
- Create: `app/src/main/java/com/notsatria/starter/di/AppModule.kt`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/build.gradle.kts`

- [ ] Add `koin-android` to app dependencies so `androidContext` is available.
- [ ] Create `StarterApplication` and start Koin with `appModule`.
- [ ] Register the application class in the manifest.

### Task 2: Navigation Types

**Files:**
- Create: `app/src/main/java/com/notsatria/starter/navigation/AppRoute.kt`
- Create: `app/src/main/java/com/notsatria/starter/navigation/Navigator.kt`
- Create: `app/src/main/java/com/notsatria/starter/navigation/AppNavigator.kt`

- [ ] Define `AppRoute.Home` as a `@Serializable` Navigation 3 `NavKey`.
- [ ] Define `Navigator` with `setBackStack`, `navigateTo`, and `navigateBack`.
- [ ] Implement `AppNavigator` by mutating the Navigation 3 back stack.

### Task 3: Screen and NavDisplay

**Files:**
- Create: `app/src/main/java/com/notsatria/starter/ui/screens/HomeScreen.kt`
- Create: `app/src/main/java/com/notsatria/starter/navigation/AppNavigation.kt`
- Modify: `app/src/main/java/com/notsatria/starter/di/AppModule.kt`
- Modify: `app/src/main/java/com/notsatria/starter/MainActivity.kt`

- [ ] Create a simple Material 3 home screen.
- [ ] Register the `Home` navigation entry in Koin.
- [ ] Render `NavDisplay` with saveable state and ViewModel store decorators.
- [ ] Replace the default greeting content in `MainActivity`.

### Task 4: Verify

**Files:**
- Verify all changed Kotlin and Gradle files.

- [ ] Run `./gradlew :app:compileDebugKotlin`.
- [ ] If API names differ from the installed alpha artifacts, adjust imports and calls until compilation succeeds.
