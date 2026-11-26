# controller

Menerima request dari browser, langsung memanggil repository (database), lalu mengembalikan nama template Thymeleaf.
Alur: **Frontend (Thymeleaf) → Controller → Repository → Database MySQL**. Tidak ada layer service.
Semua controller memakai *constructor injection* (tanpa `@Autowired` di field).

| File | URL | Keterangan |
|---|---|---|
| `AuthController` | `GET/POST /login`, `GET/POST /daftar`, `GET /logout` | Login, registrasi, logout. ID user disimpan di session `userId`. |
| `BerandaController` | `GET /` | Beranda. Parameter: `tab` (`terbaru`/`hilang`/`temuan`), `kategori`, `q` (pencarian). |
| `LaporanController` | `GET /laporan`, `GET/POST /laporan/kehilangan`, `GET/POST /laporan/temuan` | Menu laporan & form laporan. Foto wajib untuk laporan temuan. |
| `KlaimController` | `GET /klaim`, `GET/POST /klaim/ajukan/{barangId}`, `GET /klaim/verifikasi/{klaimId}`, `POST .../terima`, `POST .../tolak` | User mengajukan klaim; admin memverifikasi. |
| `InformasiController` | `GET /informasi`, `GET /barang/{id}`, `POST /barang/{id}/perbarui` | Daftar & detail barang. Admin melihat halaman kelola (bisa ubah lokasi & durasi simpan). |
| `SessionHelper` | - | Helper untuk mengambil `User` yang sedang login dari session. |
| `UploadHelper` | - | Menyimpan foto upload ke folder `uploads/` dengan nama acak (UUID). Hanya menerima jpg, jpeg, png, webp, gif. Mengembalikan URL `/uploads/...` atau `null`. Folder diatur lewat `app.upload-dir`. |

`AuthController.hash(text)` (static) meng-hash password dengan SHA-256; dipakai juga oleh `DataSeeder`.

## Alur halaman
- **User**: Beranda → detail barang → (Temuan) Ajukan Klaim / (Hilang) Kirim Laporan.
- **Admin**: Menu Klaim → kartu barang dengan klaim menunggu → Verifikasi / Tolak.
- Pesan sukses dikirim lewat `RedirectAttributes.addFlashAttribute("pesan", ...)` dan tampil sebagai toast.

## Format input form
- `tanggal`: `yyyy-MM-dd` (diisi dari kalender JS)
- `waktu`: `HH:mm` (diisi dari pemilih waktu JS)
