# entity

Class JPA yang menjadi tabel di MySQL (database `temuin_app`). Tabel dibuat otomatis oleh Hibernate (`ddl-auto=update`).

## User → tabel `users`
| Kolom | Tipe | Keterangan |
|---|---|---|
| id | Long | Primary key, auto increment |
| namaLengkap | String | |
| email | String | Unik |
| password | String | Hash SHA-256 |
| telepon, foto | String | Opsional |
| role | String | `USER`, `ADMIN`, `ADMIN_UTAMA` |

Method `isAdmin()` bernilai `true` untuk ADMIN dan ADMIN_UTAMA.

## Barang → tabel `barang`
| Kolom | Keterangan |
|---|---|
| nama, kategori, deskripsi, foto | Data barang. Kategori: Elektronik, Pakaian, Aksesoris, Dokumen |
| tanggal, waktu, lokasi | Kapan & di mana hilang/ditemukan |
| status | `HILANG`, `TEMUAN`, `KLAIM` |
| lokasiSimpan, tanggalMulai, tanggalAkhir | Info penyimpanan (diatur admin, default 30 hari) |
| tanggalKlaim | Diisi saat klaim diterima |
| pelapor | Relasi `ManyToOne` ke `User` |

Getter bantu untuk tampilan: `getTanggalText()` ("4 November 2026"), `getWaktuText()` ("09.00"),
`getStatusText()`, `getBadgeClass()`, `getFotoUrl()`.

## Klaim → tabel `klaim`
| Kolom | Keterangan |
|---|---|
| barang | Relasi ke `Barang` yang diklaim |
| user | Relasi ke `User` yang mengajukan |
| nama, email, telepon, detail, bukti | Isi form klaim |
| status | `MENUNGGU`, `DITERIMA`, `DITOLAK` |
| tanggalPengajuan | Tanggal klaim dikirim |
