import { createServer } from 'node:http';
import { readFile } from 'node:fs/promises';
import { fileURLToPath } from 'node:url';
import { dirname, resolve, extname, sep } from 'node:path';
const root = dirname(fileURLToPath(import.meta.url));
const types = { '.html': 'text/html', '.css': 'text/css', '.mjs': 'text/javascript', '.png': 'image/png', '.apk': 'application/vnd.android.package-archive' };
createServer(async (req, res) => {
  const target = resolve(root, '.' + decodeURIComponent(new URL(req.url, 'http://localhost').pathname === '/' ? '/index.html' : new URL(req.url, 'http://localhost').pathname));
  if (!target.startsWith(root + sep)) { res.writeHead(403); res.end(); return; }
  try { res.writeHead(200, { 'Content-Type': types[extname(target)] || 'application/octet-stream' }); res.end(await readFile(target)); }
  catch { res.writeHead(404); res.end('Not found'); }
}).listen(4187, '127.0.0.1', () => console.log('LifeOS demo: http://127.0.0.1:4187'));
