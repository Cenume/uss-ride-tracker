import express from 'express';
import path from 'path';
import { fileURLToPath } from 'url';
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

/** App Initialization */
const app = express();
const port = 3000

console.log(__dirname);

app.use(express.static(path.join(__dirname, 'static'))); 

/** Serve SPA */
app.get(['/'], function(req, res, next) {
  res.sendFile(path.join(__dirname, '../dist/static/index.html'));
});

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`)
})