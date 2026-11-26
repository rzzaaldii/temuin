package com.example.temuin.config;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.temuin.controller.AuthController;
import com.example.temuin.entity.Barang;
import com.example.temuin.entity.User;
import com.example.temuin.repository.BarangRepository;
import com.example.temuin.repository.UserRepository;

/**
 * Isi data awal (akun & barang contoh) saat database masih kosong.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private UserRepository userRepository;
    private BarangRepository barangRepository;

    public DataSeeder(UserRepository userRepository, BarangRepository barangRepository) {
        this.userRepository = userRepository;
        this.barangRepository = barangRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User user = new User("Kelompok 1", "kelompok1@gmail.com", AuthController.hash("password"), User.ROLE_USER);
        user.setFoto("/img/avatar.png");
        user = userRepository.save(user);

        buatBarang("AirPods Pro", "Elektronik", LocalDate.of(2026, 11, 4), LocalTime.of(14, 20),
                "Gedung F 2.6", "AirPods Pro tertinggal di meja ruang kelas.", "/img/item-airpods.png",
                Barang.HILANG, user);
        buatBarang("Tas Ransel Hitam", "Aksesoris", LocalDate.of(2026, 11, 4), LocalTime.of(9, 0),
                "GKM FILKOM", "Tas ransel hitam berisi laptop dan buku catatan.", "/img/item-tas.png",
                Barang.HILANG, user);
        buatBarang("Botol Minum", "Aksesoris", LocalDate.of(2026, 11, 3), LocalTime.of(12, 45),
                "Gedung G 1.4", "Ditemukan botol minum tumbler warna navy di meja kelas.", "/img/item-botol.png",
                Barang.TEMUAN, user);
        buatBarang("Dompet Hitam", "Aksesoris", LocalDate.of(2026, 11, 2), LocalTime.of(10, 40),
                "Junction", "Ditemukan dompet berwana hitam memiliki merk X tergeletak di area Junction pagi ini.",
                "/img/item-dompet.png", Barang.TEMUAN, user);
    }

    private Barang buatBarang(String nama, String kategori, LocalDate tanggal, LocalTime waktu, String lokasi,
            String deskripsi, String foto, String status, User pelapor) {
        Barang b = new Barang();
        b.setNama(nama);
        b.setKategori(kategori);
        b.setTanggal(tanggal);
        b.setWaktu(waktu);
        b.setLokasi(lokasi);
        b.setDeskripsi(deskripsi);
        b.setFoto(foto);
        b.setStatus(status);
        b.setPelapor(pelapor);
        b.aturPenyimpananAwal();
        return barangRepository.save(b);
    }
}
