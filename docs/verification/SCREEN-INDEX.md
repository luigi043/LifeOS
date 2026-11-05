# Screen index

All Android images below are actual API 35 emulator framebuffer captures at 1080x2340, using fictional values. They are not mockups or browser images. The app has no hardware/account connection workflow to exercise. The browser captures are listed separately.

## Android (28 captures)

| Capture | Route or action | Evidence |
| --- | --- | --- |
| 01 onboarding welcome | Fresh debug install. Open LifeOS. | [PNG](android/01-onboarding-welcome.png) |
| 02 onboarding profile | Continue, enter fictional name Alex Demo. | [PNG](android/02-onboarding-profile.png) |
| 03 onboarding body | Continue to optional measurements. | [PNG](android/03-onboarding-body.png) |
| 04 onboarding goals | Continue to goals; scroll to inspect all preferences. | [PNG](android/04-onboarding-goals.png) |
| 05 onboarding budget invalid | Enter monthly budget 100 and food budget 150. Continue is disabled. | [PNG](android/05-onboarding-budget-invalid.png) |
| 06 onboarding budget | Correct food budget to 50. | [PNG](android/06-onboarding-budget.png) |
| 07 onboarding review | Continue to review before saving. | [PNG](android/07-onboarding-review.png) |
| 08 today | Finish onboarding. Local dashboard opens. | [PNG](android/08-today.png) |
| 09 finance empty | Today > Finance. Empty month. | [PNG](android/09-finance-empty.png) |
| 10 add expense | Finance > Add expense. | [PNG](android/10-add-expense.png) |
| 11 add expense invalid | Save an empty amount. Validation appears. | [PNG](android/11-add-expense-invalid.png) |
| 12 add expense recurring | Enter 0,29 and Synthetic coffee. Enable recurring label. | [PNG](android/12-add-expense-recurring.png) |
| 13 finance saved | Save. Expense appears in the local list. | [PNG](android/13-finance-saved.png) |
| 14 delete confirmation | Delete expense. Confirmation dialog, then cancel. | [PNG](android/14-delete-confirmation.png) |
| 15 finance income | Select Income. Honest empty/unavailable entry state. | [PNG](android/15-finance-income.png) |
| 16 finance deleted | Return to Expenses, confirm deletion. | [PNG](android/16-finance-deleted.png) |
| 17 nutrition | Today > Nutrition. | [PNG](android/17-nutrition.png) |
| 18 workout | Today > Workout. | [PNG](android/18-workout.png) |
| 19 grocery | Today > Grocery. | [PNG](android/19-grocery.png) |
| 20 mental wellness | Today > Mental wellness. | [PNG](android/20-mental-wellness.png) |
| 21 habits | Today > Habits. | [PNG](android/21-habits.png) |
| 22 analytics | Today > Analytics. | [PNG](android/22-analytics.png) |
| 23 settings | Today > Settings. | [PNG](android/23-settings.png) |
| 24 settings dark | Enable dark mode and wait for saved preference. | [PNG](android/24-settings-dark.png) |
| 25 profile | Disable dark mode, open Profile. Read back saved fictional profile. | [PNG](android/25-profile.png) |
| 26 back to today | Press system Back from Profile. Verify return to Today. | [PNG](android/26-back-to-today.png) |
| 27 relaunch | Recreate the activity. Verify onboarding is not shown. | [PNG](android/27-relaunch.png) |
| 28 cold launch | Force-stop and cold-launch the debug app. Verify persisted profile and dashboard. | [PNG](android/28-cold-launch.png) |

## Browser (5 captures)

These are real browser captures of a separate local-storage simulation. Phone view is the default; wide view is optional.

| Capture | Evidence |
| --- | --- |
| 00 today phone | [PNG](browser/00-today-phone.png) |
| 01 today desktop | [PNG](browser/01-today-desktop.png) |
| 02 finance saved | [PNG](browser/02-finance-saved.png) |
| 03 settings dark | [PNG](browser/03-settings-dark.png) |
| 04 finance mobile | [PNG](browser/04-finance-mobile.png) |

## Coverage and limits

All 12 navigation destinations are represented: onboarding, Today, Finance, Add expense, Nutrition, Workout, Grocery, Mental wellness, Habits, Analytics, Profile and Settings. Onboarding has six steps; Finance has Expenses/Income tabs and a delete dialog. Screens for unfinished modules disclose their status. Persistence-failure recovery is tested with a synthetic exception in the ViewModel suite, not by claiming a real disk failure. No permission, payment, account, cloud, bank, BLE, RFID, camera or wearable flow is implemented or verified.
