# config

Konfigurasi aplikasi yang jalan otomatis saat Spring Boot start.

| File | Fungsi |
|---|---|
| `WebConfig.java` | Mendaftarkan folder `uploads/` agar bisa diakses lewat URL `/uploads/**`, dan memasang `LoginInterceptor` ke semua halaman. |
| `LoginInterceptor.java` | Mengecek session `userId`. Jika belum login, user diarahkan ke `/login`. |
| `DataSeeder.java` | Mengisi data awal (akun & 5 barang contoh + 1 klaim) **hanya jika** tabel `users` masih kosong. |

## Halaman yang bebas tanpa login
`/login`, `/daftar`, `/css/**`, `/js/**`, `/img/**`, `/uploads/**`, `/error`.
Tambahkan path baru di `excludePathPatterns` pada `WebConfig` jika perlu.

## Akun bawaan (dari DataSeeder)
| Role | Email | Password |
|---|---|---|
| USER | kelompok1@gmail.com | password |
| ADMIN | admin@temuin.com | admin123 |
| ADMIN_UTAMA | adminutama@temuin.com | admin123 |
| USER (pengklaim contoh) | rizky.pratama@gmail.com | password |

Untuk mengulang data contoh: kosongkan database `temuin_app`, lalu jalankan ulang aplikasi.
