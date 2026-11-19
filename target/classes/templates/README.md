# templates

Halaman Thymeleaf. Semua halaman dibungkus `div.phone` (ukuran 390 x 844, sama seperti frame Figma), lengkap dengan status bar dan home indicator.

| File | Layar Figma |
|---|---|
| `fragments/layout.html` | Bagian yang dipakai ulang: `head`, `statusBar`, `homeIndicator`, `toast`, `navbar(aktif)`, `card(b, url)`, `bigCard(b)`, `sheet` (Ambil Gambar / Pilih File) |
| `auth/login.html` | 01_Login_SSO_UB |
| `auth/daftar.html` | 01_Daftar_SSO_UB |
| `beranda.html` | 02_PencarianBarangUI |
| `laporan/menu.html` | #_MenuLaporanUI |
| `laporan/form.html` | Fragment form laporan (dipakai 2 halaman di bawah) |
| `laporan/kehilangan.html` | 03_LaporanKehilanganUI |
| `laporan/temuan.html` | 04_LaporanTemuanUI |
| `klaim/menu.html` | #_MenuKlaimUI (User / Admin) |
| `klaim/ajukan.html` | 05_KlaimBarangUI |
| `klaim/verifikasi.html` | 06_VerifikasiKlaimUI |
| `informasi/menu.html` | #_MenuInformasiBarangUI |
| `informasi/detail.html` | 07.1_PengelolaanBarangUI (tampilan user: Hilang / Temuan / Klaim) |
| `informasi/kelola.html` | 07.2_PengelolaanBarangUI (tampilan admin, bisa diperbarui) |

## Cara memakai fragment
```html
<nav th:replace="~{fragments/layout :: navbar('beranda')}"></nav>
<a th:replace="~{fragments/layout :: card(${b}, @{/barang/{id}(id=${b.id})})}"></a>
```
Nilai `navbar(...)`: `beranda`, `laporan`, `klaim`, `informasi`.
