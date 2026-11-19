package com.example.temuin.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.temuin.entity.Barang;
import com.example.temuin.entity.Klaim;
import com.example.temuin.repository.BarangRepository;
import com.example.temuin.repository.KlaimRepository;

@Service
public class KlaimService {

    private KlaimRepository klaimRepository;
    private BarangRepository barangRepository;

    public KlaimService(KlaimRepository klaimRepository, BarangRepository barangRepository) {
        this.klaimRepository = klaimRepository;
        this.barangRepository = barangRepository;
    }

    public Klaim simpan(Klaim klaim) {
        return klaimRepository.save(klaim);
    }

    public Klaim cariById(Long id) {
        return klaimRepository.findById(id).orElse(null);
    }

    /** klaim terbaru yang masih menunggu verifikasi untuk satu barang */
    public Klaim klaimMenunggu(Barang barang) {
        List<Klaim> daftar = klaimRepository.findByBarangAndStatusOrderByIdDesc(barang, Klaim.MENUNGGU);
        if (daftar.isEmpty()) {
            return null;
        }
        return daftar.get(0);
    }

    public void terima(Klaim klaim) {
        klaim.setStatus(Klaim.DITERIMA);
        klaimRepository.save(klaim);

        Barang barang = klaim.getBarang();
        barang.setStatus(Barang.KLAIM);
        barang.setTanggalKlaim(LocalDate.now());
        barangRepository.save(barang);

        // klaim lain untuk barang yang sama otomatis ditolak
        List<Klaim> lainnya = klaimRepository.findByBarangAndStatus(barang, Klaim.MENUNGGU);
        for (int i = 0; i < lainnya.size(); i++) {
            Klaim k = lainnya.get(i);
            k.setStatus(Klaim.DITOLAK);
            klaimRepository.save(k);
        }
    }

    public void tolak(Klaim klaim) {
        klaim.setStatus(Klaim.DITOLAK);
        klaimRepository.save(klaim);
    }
}
