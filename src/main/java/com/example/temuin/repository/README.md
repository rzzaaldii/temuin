# repository

Interface Spring Data JPA. Query dibuat otomatis dari nama method, jadi tidak perlu menulis SQL.

| File | Method tambahan | Kegunaan |
|---|---|---|
| `UserRepository` | `findByEmail(email)` | Login & cek email sudah terdaftar |
| `BarangRepository` | `findAllByOrderByTanggalDescIdDesc()` | Semua barang, terbaru dulu |
| | `findByStatusOrderByTanggalDescIdDesc(status)` | Filter tab Barang Hilang / Barang Temuan |
| | `countByStatus(status)` | Angka di "Ringkasan Pelaporan" |
| `KlaimRepository` | `findByStatusOrderByIdDesc(status)` | Daftar klaim per status |
| | `findByBarangAndStatus(...)` | Menolak klaim lain saat satu klaim diterima |
| | `findByBarangAndStatusOrderByIdDesc(...)` | Mencari klaim terbaru yang masih menunggu |

Method bawaan `JpaRepository` (`save`, `findById`, `count`, `delete`, ...) juga bisa dipakai.
