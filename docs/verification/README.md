# LifeOS verification

Verified on 5 November 2026. This is a personal lifestyle-management prototype with working local profile and finance workflows. It is not a business accounting product or a health assessment system.

## Public results

- [Interactive phone demo](https://lifeos-phone-demo.vercel.app), phone view is the default. Wide view is optional.
- [28 real Android screenshots](https://lifeos-phone-demo.vercel.app/screenshots.html).
- [Installable Android debug APK](https://lifeos-phone-demo.vercel.app/lifeos-debug.apk).
- [Public artifact metadata](https://lifeos-phone-demo.vercel.app/artifact.json).
- [Committed screen index](SCREEN-INDEX.md), with routes, actions and separate browser evidence.
- [Machine-readable verification results](results.json).

All public links were requested anonymously. All 28 native PNGs returned successfully with the expected 1080x2340 dimensions. The downloaded APK matched the local build byte-for-byte by SHA-256. The GitHub repository remains private; the public demo does not require repository access.

The browser companion is a separate JavaScript simulation backed by local storage. It uses fictional examples and does not execute Kotlin, connect to the Room database, or share data with the Android app. The APK is the actual native application. No personal entries are bundled in it.

## Changes and evidence

| Problem found | Change | Verification |
| --- | --- | --- |
| App entry-point processing failed under the pinned Hilt/KSP2 setup | Use KSP1 without upgrading dependencies | Debug application and test APKs build |
| Splash-screen extension was called as a nonexistent static function | Import and call the Activity extension | Compilation and native cold start |
| Floating-point conversion truncated cents; comma input was silently changed | Exact decimal parser and visible validation | Money tests and native 0,29 entry displays EUR 0.29 |
| Save errors could leave the form busy; rapid saves could duplicate entries | Recovery message, finally cleanup, synchronous save guard | Injected persistence failure/retry and duplicate-save tests |
| Delete callback had no reachable control | Delete button and confirmation/cancel dialog | Native and browser create/cancel/delete lifecycle |
| Budget totals mixed currencies and profile budgets were unused | Selected-currency totals and profile-budget fallback | Finance use-case test and native budget readback |
| Calendar query omitted the final 999 milliseconds and used UTC instead of device calendar | Next local month start minus one millisecond | Lisbon DST month-boundary test |
| Most modules were unreachable | Dashboard directory and navigation callbacks | Native walkthrough of all destinations |
| Tab navigation targeted removed onboarding or restored stale detail stacks for Today | Anchor tabs to Today; pop directly for the home action | Home transitions and system Back from Profile pass |
| Profile/settings were placeholders despite existing data/preferences | Profile readback, saved dark mode and currency controls | Native profile/dark captures and browser persistence tests |
| Setup accepted malformed values and hid long goal content | Validation, inline feedback and scrolling around the keyboard | Onboarding unit cases and native six-step completion |
| UI/readme implied unavailable health, AI and automatic recurrence features | Explicit development status; separate roadmap from implementation | Screen review and English copy audit |

## Test results

| Check | Result |
| --- | --- |
| Android debug application build | Pass, Gradle 8.10.2, Java 17, SDK 35 |
| Android instrumentation APK build | Pass |
| Kotlin unit tests | 10 passed, 0 failed |
| Native instrumented workflow | 1 end-to-end test passed, 58.11 seconds |
| Native screen captures | 27 from the instrumented workflow plus 1 independent cold launch |
| Process cold launch | Pass, launch reported COLD and persisted Alex Demo profile was visible |
| Browser model tests | 4 passed |
| Chromium workflow suite against deployed demo | Pass, including phone view, optional wide view and 375px layout |
| Browser runtime errors | None observed during the suite |
| Public gallery and download | Anonymous access passed, 28 native images validated, APK hash matched |

The native workflow covers all six onboarding steps, a disabled invalid-budget state, empty finance, invalid expense input, recurring-label state, save, cancel/confirm deletion, income empty state, every module, dark mode, profile readback, system Back and activity recreation. The independent force-stop/cold-launch check confirms process-level persistence. Synthetic save-failure recovery is a ViewModel test, not a claim that real storage failure was induced on the emulator.

The browser workflow covers exact-cent entry, validation, delete cancellation/confirmation, reload persistence, profile setup and budget errors, currency selection, dark mode, every module destination, no horizontal overflow at 375px, and no JavaScript runtime errors. Browser images in the screen index are genuine Chromium captures of the simulation, not native Android evidence.

## Artifact

- Package: `com.luigi.lifeos.debug`
- Version: `0.1.0`
- Minimum Android: API 26. Tested runtime: API 35 emulator only.
- Size: 22,179,272 bytes.
- SHA-256: `6845d05b457c191e7e4c77609ed9cbef717a5f862d401df8e8555fa803785544`
- Debug-signed evaluation build, not a store release. No release key was created or changed.

## Reproduce

Use the existing JDK 17 and Android SDK 35. From the repository root:

```powershell
.\gradlew.bat :app:assembleDebug :app:assembleDebugAndroidTest :core:domain:test :feature:finance:testDebugUnitTest :feature:onboarding:testDebugUnitTest --no-daemon --console=plain
node --test demo/model.test.mjs
```

Run `WorkflowScreenshotTest` on a fresh synthetic-data debug installation. Use an explicitly selected test emulator serial with ADB; never clear an installation containing personal data. The walkthrough expects onboarding to be incomplete. Screenshots are written to the debug app's external-files `screenshots/` directory; pull them into `docs/verification/android/`.

After retrieving native evidence, `node demo/prepare-demo.mjs` copies images and the built APK into ignored distribution paths and generates the public gallery and artifact metadata. This keeps a single committed copy of the native PNGs.

Start the browser companion with `node demo/server.mjs`. With the pinned Playwright dependency installed, run `node demo/browser-test.mjs`. Set `DEMO_URL` to the public URL to test deployment; `CHROMIUM_EXECUTABLE` can reuse an installed Chromium binary. `node demo/check-public.mjs` validates anonymous gallery access and downloads the APK for a hash comparison. The latter requires `DEMO_URL`.

`scripts/verification-report.mjs` exports the screen index and results from JUnit XML plus local verification logs. Logs and build products are ignored. The raw logs retain detailed local diagnostics; the committed result summary avoids machine-specific paths.

The deployment is a static Vercel Hobby project named `lifeos-phone-demo`. Only the browser companion, native screenshots and debug APK were published there. The private native source repository and its default branch were preserved. No subscription or paid service was purchased.

## Git sequence

Published branch: `improvements/lifeos-verification`.

1. `79274a8` Restore Android startup, compatible processing and saved appearance.
2. `2aa1bf4` Exact money handling, recoverable finance workflows and regression tests.
3. `6a72aa7` Reachable modules, local profile/settings, validated setup and honest feature status.
4. `2e6ad0f` Reliable home/Back navigation and the instrumented screenshot walkthrough.
5. `5045831` Phone-first interactive companion, browser tests, gallery and artifact preparation.
6. Verification documentation commit containing this report, screen index, native captures, public-access checks and the original roadmap.

Each commit has problem, implementation and validation details. The author identity matches the repository's established `luigi043` identity and connected GitHub account. No history rewriting, force push, default-branch merge or app-store release was performed. Incidental assistant product names were absent from tracked content; the working branch now has a neutral name.

## Known limits

Nutrition, workout logging, grocery editing, mental wellness, habits and analytics remain unfinished module surfaces. Recurring expense flags do not schedule new entries. Income entry, profile editing, currency conversion, cloud sync, authentication, notifications, data export and AI features are unavailable. The Retrofit endpoint is a placeholder.

No physical device, real account, bank connection, camera, BLE, RFID, wearable, payment, nutrition provider or live backend was used. No personal Samsung data was accessed. Minimum-API, hardware, release-optimization, accessibility-service and database-upgrade paths were not certified by this emulator run.

Room and DataStore are local persistence, not encrypted storage. Android backup follows the manifest/rules and device settings. The saved profile budget is used as a recurring monthly baseline when there is no explicit period budget; changing the display currency does not convert existing amounts. The browser data is fictional and remains local to that browser, with a visible session-only notice if storage is unavailable.
