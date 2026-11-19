# static

File statis yang langsung disajikan oleh Spring Boot.

## css/style.css
Satu file CSS untuk semua halaman. Warna mengikuti Design System Figma (variabel di `:root`):

| Variabel | Warna | Dipakai untuk |
|---|---|---|
| `--primary` | #1EB0FF | Tombol, judul, badge Temuan |
| `--success` | #3CC956 | Badge Klaim |
| `--n40` | #E2E2E2 | Badge Hilang, garis |
| `--n60` | #A4A4A4 | Border input, teks abu |
| `--n100` | #101010 | Teks utama |

Font: **Plus Jakarta Sans** (isi) dan **Inter** (status bar, kalender), dari Google Fonts.

## js/app.js
JavaScript biasa (tanpa library, tanpa arrow function). Dipanggil lewat atribut `onclick`.

| Function | Kegunaan |
|---|---|
| `togglePassword(id)` | Lihat/sembunyikan kata sandi |
| `toggleDropdown(idPopup)`, `pilihOpsi(field, nilai)` | Dropdown kategori & lokasi |
| `bukaKalender(field)`, `gantiBulan`, `pilihTanggal` | Kalender pilih tanggal |
| `setWaktu(field)` | Pilih jam & menit |
| `bukaSheet(idInput)`, `ambilGambar()`, `pilihFile()`, `tampilkanPreview(input)` | Upload foto + preview |
| `seretMasuk`, `seretKeluar`, `lepasFile` | Drag & drop gambar |

Pola ID untuk field: `<nama>-value` (input hidden), `<nama>-label` (teks tampil), `<nama>-popup` (popup).

## img/
Semua ikon (SVG) dan gambar contoh (PNG) diunduh langsung dari Figma. Tidak memakai library ikon luar.

| Awalan nama | Isi |
|---|---|
| `nav-*-on/off.svg` | Ikon navbar aktif / tidak aktif |
| `ic-*.svg` | Ikon input & meta (pin, kalender, jam, email, dll) |
| `cat-*.svg` | Ikon chip kategori |
| `hero-*.svg` | Ilustrasi 60px di header menu |
| `sum-*.svg` | Ikon Ringkasan Pelaporan |
| `item-*.png` | Foto barang contoh (data seeder) |
