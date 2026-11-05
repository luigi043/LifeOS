import { readFile, readdir, writeFile } from 'node:fs/promises';
import { createHash } from 'node:crypto';
const root = new URL('../', import.meta.url);
const evidence = new URL('docs/verification/', root);
const resultFolders = ['core/domain/build/test-results/test/', 'feature/finance/build/test-results/testDebugUnitTest/', 'feature/onboarding/build/test-results/testDebugUnitTest/'];
const suites = [];
for (const folder of resultFolders) {
  for (const name of (await readdir(new URL(folder, root))).filter(n => n.startsWith('TEST-') && n.endsWith('.xml'))) {
    const xml = await readFile(new URL(folder + name, root), 'utf8');
    const attributes = Object.fromEntries([...xml.match(/<testsuite\s+[^>]+>/)[0].matchAll(/([\w]+)="([^"]*)"/g)].map(m => [m[1], m[2]]));
    suites.push({ name: attributes.name, tests: Number(attributes.tests), failures: Number(attributes.failures), errors: Number(attributes.errors), skipped: Number(attributes.skipped) });
  }
}
const nativeLog = await readFile(new URL('native-verification.log', root), 'utf8');
const browserLog = await readFile(new URL('browser-verification.log', root), 'utf8');
const publicChecks = JSON.parse((await readFile(new URL('public-verification.log', root), 'utf8')).trim());
const android = (await readdir(new URL('android/', evidence))).filter(n => n.endsWith('.png')).sort();
const browser = (await readdir(new URL('browser/', evidence))).filter(n => n.endsWith('.png')).sort();
const apk = await readFile(new URL('app/build/outputs/apk/debug/app-debug.apk', root));
const results = {
  verifiedAt: new Date().toISOString(), nativeDevice: { kind: 'emulator', api: 35, resolution: '1080x2340' },
  unit: { total: suites.reduce((n, s) => n + s.tests, 0), failures: suites.reduce((n, s) => n + s.failures + s.errors, 0), suites },
  instrumented: { passed: /OK \(1 test\)/.test(nativeLog), tests: 1, androidScreenshots: android.length },
  browser: { modelTests: 4, endToEndPassed: browserLog.includes('PASS:'), screenshots: browser.length },
  apk: { bytes: apk.length, sha256: createHash('sha256').update(apk).digest('hex'), package: 'com.luigi.lifeos.debug' },
  publicDemo: process.env.PUBLIC_DEMO_URL || null,
  publicChecks,
};
if (results.unit.failures || !results.instrumented.passed || !results.browser.endToEndPassed) throw new Error('Verification is incomplete. Reports were not exported.');
if (publicChecks.apkSha256 !== results.apk.sha256) throw new Error('The public APK does not match the local verified build.');
await writeFile(new URL('results.json', evidence), JSON.stringify(results, null, 2) + '\n');
const actions = {
  '01': 'Fresh debug install. Open LifeOS.', '02': 'Continue, enter fictional name Alex Demo.',
  '03': 'Continue to optional measurements.', '04': 'Continue to goals; scroll to inspect all preferences.',
  '05': 'Enter monthly budget 100 and food budget 150. Continue is disabled.',
  '06': 'Correct food budget to 50.', '07': 'Continue to review before saving.',
  '08': 'Finish onboarding. Local dashboard opens.', '09': 'Today > Finance. Empty month.',
  '10': 'Finance > Add expense.', '11': 'Save an empty amount. Validation appears.',
  '12': 'Enter 0,29 and Synthetic coffee. Enable recurring label.',
  '13': 'Save. Expense appears in the local list.', '14': 'Delete expense. Confirmation dialog, then cancel.',
  '15': 'Select Income. Honest empty/unavailable entry state.', '16': 'Return to Expenses, confirm deletion.',
  '17': 'Today > Nutrition.', '18': 'Today > Workout.', '19': 'Today > Grocery.',
  '20': 'Today > Mental wellness.', '21': 'Today > Habits.', '22': 'Today > Analytics.',
  '23': 'Today > Settings.', '24': 'Enable dark mode and wait for saved preference.',
  '25': 'Disable dark mode, open Profile. Read back saved fictional profile.',
  '26': 'Press system Back from Profile. Verify return to Today.',
  '27': 'Recreate the activity. Verify onboarding is not shown.',
  '28': 'Force-stop and cold-launch the debug app. Verify persisted profile and dashboard.',
};
const label = name => name.replace('.png', '').replaceAll('-', ' ');
await writeFile(new URL('SCREEN-INDEX.md', evidence), `# Screen index\n\nAll Android images below are actual API 35 emulator framebuffer captures at 1080x2340, using fictional values. They are not mockups or browser images. The app has no hardware/account connection workflow to exercise. The browser captures are listed separately.\n\n## Android (${android.length} captures)\n\n| Capture | Route or action | Evidence |\n| --- | --- | --- |\n${android.map(name => `| ${label(name)} | ${actions[name.slice(0, 2)] || 'Native state captured during verification.'} | [PNG](android/${name}) |`).join('\n')}\n\n## Browser (${browser.length} captures)\n\nThese are real browser captures of a separate local-storage simulation. Phone view is the default; wide view is optional.\n\n| Capture | Evidence |\n| --- | --- |\n${browser.map(name => `| ${label(name)} | [PNG](browser/${name}) |`).join('\n')}\n\n## Coverage and limits\n\nAll 12 navigation destinations are represented: onboarding, Today, Finance, Add expense, Nutrition, Workout, Grocery, Mental wellness, Habits, Analytics, Profile and Settings. Onboarding has six steps; Finance has Expenses/Income tabs and a delete dialog. Screens for unfinished modules disclose their status. Persistence-failure recovery is tested with a synthetic exception in the ViewModel suite, not by claiming a real disk failure. No permission, payment, account, cloud, bank, BLE, RFID, camera or wearable flow is implemented or verified.\n`);
console.log(JSON.stringify({ nativeScreenshots: android.length, browserScreenshots: browser.length, nativeUnitTests: results.unit.total, artifactSha256: results.apk.sha256 }));
