import { readdir, readFile, writeFile, mkdir, copyFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import { createHash } from 'node:crypto';
const base = new URL('./', import.meta.url);
const source = new URL('../docs/verification/android/', base);
const target = new URL('android/', base);
await mkdir(target, { recursive: true });
const names = (await readdir(source)).filter(name => name.endsWith('.png')).sort();
for (const name of names) await copyFile(new URL(name, source), new URL(name, target));
await copyFile(new URL('../app/build/outputs/apk/debug/app-debug.apk', base), new URL('lifeos-debug.apk', base));
const apk = await readFile(new URL('lifeos-debug.apk', base));
const metadata = { applicationId: 'com.luigi.lifeos.debug', versionName: '0.1.0', buildType: 'debug', minSdk: 26, targetSdk: 35,
  bytes: apk.length, sha256: createHash('sha256').update(apk).digest('hex'), nativeScreenshots: names.length,
  note: 'Debug-signed evaluation build. Personal finance prototype; unfinished modules are labeled. Synthetic verification data is not bundled in the Android app.' };
await writeFile(new URL('artifact.json', base), JSON.stringify(metadata, null, 2) + '\n');
const captions = names.map(name => ({ name, label: name.replace('.png', '').replaceAll('-', ' ') }));
await writeFile(new URL('screenshots.html', base), `<!doctype html><html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>LifeOS | Real Android screenshots</title><link rel="stylesheet" href="style.css"><style>body{padding:28px;max-width:1200px;margin:auto}header{max-width:780px}section{display:grid;grid-template-columns:repeat(auto-fit,minmax(240px,1fr));gap:28px}figure{margin:0}figure img{width:100%;height:540px;object-fit:contain;background:#e7eeea;border:1px solid #cbdad3;border-radius:10px}figcaption{font-size:14px;line-height:1.6;margin:12px 0 24px;text-transform:capitalize}a{display:inline-block;margin-right:15px}</style></head><body><header><a href="index.html">Open phone demo</a><a href="lifeos-debug.apk">Download Android debug APK</a><h1>Real Android screens</h1><p>These ${names.length} captures come from the native Kotlin/Compose app on an Android API 35 emulator. They show fictional verification values. They are separate from the interactive browser simulation. Select an image for its full resolution.</p><p class="muted">The debug build is for evaluation, not a store release. Unfinished modules are explicitly labeled. Hardware, cloud, bank and health integrations are not connected.</p></header><section>${captions.map(({ name, label }) => `<figure><a href="android/${name}"><img src="android/${name}" alt="LifeOS native Android: ${label}" loading="lazy"></a><figcaption>${label}</figcaption></figure>`).join('')}</section><footer>APK SHA-256: ${metadata.sha256}<br>See <a href="artifact.json">artifact metadata</a> for package details.</footer></body></html>`);
console.log(`Prepared ${names.length} native screenshots and ${apk.length} byte APK in ${fileURLToPath(base)}`);
