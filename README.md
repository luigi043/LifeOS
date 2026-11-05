# LifeOS

LifeOS is an Android prototype for personal lifestyle management. Its working core is device-local profile setup and expense tracking. It is intended for individuals organizing their own spending and preferences, not business accounting or clinical care.

The original product specification is preserved in [the roadmap](docs/ROADMAP.md). Its future features are not implemented capabilities.

## What works

- Six-step onboarding with optional measurements, goals, dietary preferences, budgets and currency.
- Local profile storage in Room and preferences in DataStore.
- Monthly expense lists, exact-cent entry using dot or comma decimals, categories and notes.
- Expense deletion with confirmation, original-currency display, and totals separated by currency.
- Monthly budget comparison using the profile budget when no period-specific budget exists.
- Profile readback, a dashboard connected to local expenses, dark mode and base-currency settings.
- Navigation to every module.

Nutrition, workouts, grocery editing, habits, mental wellness and analytics are visibly marked as work in progress. Recurring expenses are labels only, not scheduled transactions. Income entry, profile editing, currency conversion, cloud sync, authentication, reminders, AI and health assessments are not available.

## Try it

The demo/ directory contains a separate interactive browser companion. It starts in a phone layout with bottom navigation; the wider layout is optional. It uses fictional sample data and browser-local storage. It does not run the Android app or share its database. Use fictional values only.

Run `node demo/server.mjs` and open http://127.0.0.1:4187. Run model tests with `node --test demo/model.test.mjs`.

For browser regression tests, install the pinned development dependency in demo/, run `npx playwright install chromium`, start the server, then run `npm run test:browser`. An existing browser can be selected with CHROMIUM_EXECUTABLE; DEMO_URL can target a deployed demo. See [verification](docs/verification/README.md) for public links, test results and known limits.

## Build Android

Use JDK 17, Android SDK platform 35 and an emulator or device with Android 8.0 (API 26) or later. Set JAVA_HOME to JDK 17 and ANDROID_HOME to your existing Android SDK, or configure sdk.dir in an untracked local.properties.

Build and test:

    .\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :core:domain:test :feature:finance:testDebugUnitTest :feature:onboarding:testDebugUnitTest --no-daemon --console=plain

The debug APK is written to app/build/outputs/apk/debug/app-debug.apk. It installs as com.luigi.lifeos.debug, separately from a release build. It is a debug-signed evaluation build, not a store release.

The project keeps its existing Kotlin 2.0.21, Compose, Hilt 2.52, Room and Gradle 8.10.2 stack. KSP1 is intentional: the pinned Hilt/KSP combination failed to process the app entry points with KSP2. Gradle uses at most two workers by default to keep memory use reasonable.

## Native walkthrough and screenshots

WorkflowScreenshotTest covers onboarding, expense validation, save/delete/cancel, every module, dark mode, profile readback and activity recreation. Run it only on a fresh test installation with synthetic data. Clear only the debug package on the selected test emulator before a fresh walkthrough. Never clear an app containing personal data.

    .\gradlew.bat :app:connectedDebugAndroidTest --no-daemon --console=plain

Screenshots are generated under the debug app's external-files screenshots/ directory. The committed [screen index](docs/verification/SCREEN-INDEX.md) separates genuine Android captures from browser captures and records routes and states.

## Data and integration limits

Room and DataStore are local storage, not an encrypted vault. Android backup follows application backup rules and device settings. The manifest declares internet, notification and exact-alarm permissions inherited from the foundation; the current workflows do not connect to a live service or schedule recurring expenses. The Retrofit base URL is a placeholder. No account, bank, camera, BLE, RFID, wearable, nutrition provider, payment, or AI integration was exercised.

All UI and documentation in this update are English. Third-party identifiers and required attribution remain intact.
