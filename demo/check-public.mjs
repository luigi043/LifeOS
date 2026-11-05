import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import { writeFile } from 'node:fs/promises';
const base = process.env.DEMO_URL;
if (!base) throw new Error('Set DEMO_URL to the public deployment URL.');
async function get(path) {
  const response = await fetch(new URL(path, base), { redirect: 'follow' });
  assert.equal(response.status, 200, `Anonymous GET ${path}`);
  return response;
}
const index = await (await get('/')).text();
assert.ok(index.includes('class="phone-view"'));
const gallery = await (await get('/screenshots.html')).text();
const images = [...gallery.matchAll(/<img src="([^"]+)"/g)].map(m => m[1]);
assert.equal(images.length, 28);
for (let index = 0; index < images.length; index += 4) {
  await Promise.all(images.slice(index, index + 4).map(async image => {
    const data = Buffer.from(await (await get('/' + image)).arrayBuffer());
    assert.equal(data.subarray(0, 8).toString('hex'), '89504e470d0a1a0a');
    assert.equal(data.readUInt32BE(16), 1080);
    assert.equal(data.readUInt32BE(20), 2340);
  }));
}
const metadata = await (await get('/artifact.json')).json();
const response = await get('/lifeos-debug.apk');
assert.ok(response.headers.get('content-disposition')?.includes('attachment'));
const apk = Buffer.from(await response.arrayBuffer());
const sha256 = createHash('sha256').update(apk).digest('hex');
assert.equal(apk.length, metadata.bytes);
assert.equal(sha256, metadata.sha256);
await writeFile(new URL('../app/build/public-download.apk', import.meta.url), apk);
console.log(JSON.stringify({ anonymousAccess: true, defaultView: 'phone', nativeImages: images.length, apkBytes: apk.length, apkSha256: sha256, base }));
