# service

Logika bisnis aplikasi. Controller tidak langsung memanggil repository, tetapi lewat service.

| File | Isi |
|---|---|
| `UserService` | `login`, `daftar`, `emailSudahAda`, `cariById`, `hash` (SHA-256 untuk password). |
| `BarangService` | `filter(tab, kategori, kataKunci)` untuk beranda/informasi, `barangUntukKlaim()` (barang Temuan & Klaim), `aturPenyimpananAwal()` (lokasi default + durasi 30 hari). |
| `KlaimService` | `klaimMenunggu(barang)`, `terima(klaim)` (status barang jadi KLAIM, klaim lain ditolak), `tolak(klaim)`. |
| `FileService` | Menyimpan foto upload ke folder `uploads/` dengan nama acak (UUID). Hanya menerima jpg, jpeg, png, webp, gif. Mengembalikan URL `/uploads/...` atau `null`. |
| `FormatTanggal` | Helper static: `tanggal(LocalDate)` → "4 November 2026", `waktu(LocalTime)` → "09.00". |

Folder upload diatur di `application.properties` lewat `app.upload-dir=uploads`.
