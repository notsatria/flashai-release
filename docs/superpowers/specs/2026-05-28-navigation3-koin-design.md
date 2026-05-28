# Navigation 3 with Koin Design

## Goal

Set up a minimal, extensible Navigation 3 foundation for the Android Compose app and bootstrap Koin dependency injection.

## Architecture

The app uses a single Android `Application` class to start Koin. Koin owns app-level dependencies, starting with a singleton navigator.

Navigation is centered in the `navigation` package:

- `AppRoute` defines serializable, type-safe Navigation 3 route keys.
- `Navigator` defines app navigation operations.
- `AppNavigator` mutates the Navigation 3 back stack through the `Navigator` interface.
- `AppNavigation` creates the saveable back stack, binds it to the navigator, and renders destinations through `NavDisplay`.

The first destination is `Home`, rendered by `ui/screens/HomeScreen.kt`.

## Data Flow

`MainActivity` renders the theme and delegates app content to `AppNavigation`.

`AppNavigation` creates a `rememberNavBackStack(AppRoute.Home)` and passes it to `AppNavigator`. Screens receive navigation callbacks from Koin navigation entries, keeping UI code decoupled from the back stack list.

## State and ViewModels

`NavDisplay` uses Navigation 3 state decorators for saveable Compose state and per-entry ViewModel scoping. This keeps future screen ViewModels scoped to the destination entry instead of the entire activity.

## Testing and Verification

The setup is verified by compiling the Android app. No feature-specific unit tests are needed yet because this change only wires initial app structure and a placeholder screen.
