# LifeOS — Architecture & Setup

Companion to `README.md` (which holds the product spec). This doc covers
the **build system, module graph, and developer onboarding**.

---

## Module graph

```
                ┌──────────────────┐
                │       :app       │   single-Activity Compose host
                └────────┬─────────┘
                         │ depends on every module below
       ┌─────────────────┼──────────────────────────────┐
       │                 │                              │
┌──────▼────────┐  ┌─────▼────────┐               ┌─────▼─────┐
│  :feature:*   │  │   :core:*    │               │ :sync     │
│ today, finance│  │ common,      │               │ :notifications
│ workout, ...  │  │ designsystem,│               │ :ai       │
└──────┬────────┘  │ domain,data, │               └───────────┘
       │           │ database,    │
       │           │ network,     │
       │           │ datastore    │
       │           └──────────────┘
       │
       └─→ features depend on  :core:designsystem
                              :core:domain
                              :core:common
                              :core:data
```

Cross-feature data lives in `:core:data` (e.g. `StabilityScoreRepository`
reading from finance + workout + nutrition). Single-feature data lives
inside that feature.

`:core:domain` is **pure Kotlin** (`org.jetbrains.kotlin.jvm`). No Android
imports — this keeps the door open for Kotlin Multiplatform later.

---

## Convention plugins (`build-logic/`)

Per-module `build.gradle.kts` files are kept tiny because the heavy
config lives in `build-logic/convention/`:

| Plugin id                          | Use it on                                |
|------------------------------------|------------------------------------------|
| `lifeos.android.application`       | `:app`                                   |
| `lifeos.android.library`           | any Android library module               |
| `lifeos.android.library.compose`   | layered on top, enables Compose          |
| `lifeos.android.feature`           | feature modules — bundles compose + hilt |
| `lifeos.android.hilt`              | Hilt + KSP wiring                        |
| `lifeos.android.room`              | Room DAO/Database modules                |
| `lifeos.jvm.library`               | `:core:domain` — pure Kotlin             |

Bumping SDK levels, Java version, Kotlin compiler args = **one-line edit**
in `build-logic/convention/src/main/kotlin/com/luigi/lifeos/convention/`.

---

## Tech stack

| Concern         | Library                  | Where it's wired           |
|-----------------|--------------------------|----------------------------|
| UI              | Jetpack Compose + M3     | `:core:designsystem`       |
| DI              | Hilt 2.52                | `lifeos.android.hilt`      |
| Database        | Room 2.6 (KSP)           | `:core:database`           |
| Network         | Retrofit 2.11 + OkHttp   | `:core:network`            |
| Preferences     | DataStore 1.1            | `:core:datastore`          |
| Background      | WorkManager 2.10         | `:notifications`, `:sync`  |
| Serialization   | kotlinx.serialization    | `:core:domain`, `:core:network` |
| Charts          | MPAndroidChart           | `:feature:analytics` (later) |
| Logging         | Timber                   | `LifeOsApplication`        |

`gradle/libs.versions.toml` is the single source of truth for versions —
no inline version strings in any `build.gradle.kts`.

---

## First-time setup

1. **Init git + branches.** From `luigi/` in PowerShell:
   ```powershell
   .\scripts\git-setup.ps1
   ```
   This wipes any half-initialised `.git/`, creates `main`, and lays down
   per-module-group branches (`feat/core-modules`, `feat/feature-modules`,
   `feat/cross-cutting`) merged with `--no-ff`.

2. **Open in Android Studio.** Wait for Gradle sync. The first sync will
   pull KSP / Hilt / Compose compiler artifacts.

3. **Build & install.** Connect a device or start an emulator (API 26+):
   ```bash
   gradlew.bat assembleDebug
   gradlew.bat installDebug
   ```

4. **Run tests.**
   ```bash
   gradlew.bat test                     # unit tests across modules
   gradlew.bat connectedAndroidTest     # instrumented tests (device required)
   ```

---

## How to add a new feature module

1. Add to `settings.gradle.kts`:
   ```kotlin
   include(":feature:my-feature")
   ```
2. Create `feature/my-feature/build.gradle.kts`:
   ```kotlin
   plugins { alias(libs.plugins.lifeos.android.feature) }
   android { namespace = "com.luigi.lifeos.feature.myfeature" }
   ```
3. Implement `MyFeatureScreen.kt`, `MyFeatureViewModel.kt`,
   `MyFeatureNavigation.kt` (exposing a `Route` + `NavGraphBuilder.myFeatureScreen()`).
4. Wire into `:app`'s `LifeOsNavHost`.

That's it — convention plugin handles compose, hilt, navigation,
lifecycle, design-system, common, and domain deps automatically.

---

## Porting code from the existing apps

Source repos:
- `..\Expense Tracker` — Compose + Hilt + Clean Arch, single-module
- `..\LuigisWorkout` — Fragment + LiveData + static `AppContent.kt`

Suggested migration mapping:

| From `Expense Tracker`                                  | To LifeOS                                |
|---------------------------------------------------------|------------------------------------------|
| `domain/model/Models.kt`                                | `:core:domain` (split per aggregate)     |
| `data/local/database/*Entity, Daos.kt, AppDatabase`     | `:core:database`                         |
| `data/remote/api/ExchangeRateApi`                       | `:feature:finance` (or `:core:network`)  |
| `ui/screens/dashboard/*`                                | `:feature:today` (extracted)             |
| `ui/screens/{addexpense,history,budget,recurring,...}*` | `:feature:finance`                       |
| `worker/RecurringExpenseWorker`                         | `:notifications`                         |

| From `LuigisWorkout`                                    | To LifeOS                                |
|---------------------------------------------------------|------------------------------------------|
| `AppContent.kt` (static recipes/workouts)               | `:core:database` seeders + JSON assets   |
| `Entities.kt`                                           | `:core:database` (split per aggregate)   |
| `ViewModels.kt` HomeViewModel                           | `:feature:today`                         |
| `ViewModels.kt` MealPlan/LogMeal                        | `:feature:nutrition`                     |
| `ViewModels.kt` Workout                                 | `:feature:workout`                       |
| `ViewModels.kt` Progress                                | `:feature:analytics`                     |

**Important:** drop `fallbackToDestructiveMigration()` during porting.
Write a proper Room migration for each schema change. Schemas export to
`core/database/schemas/` — commit them.
