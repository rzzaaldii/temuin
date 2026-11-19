package com.example.temuin.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.temuin.entity.Barang;
import com.example.temuin.repository.BarangRepository;

@Service
public class BarangService {

    private BarangRepository barangRepository;

    public BarangService(BarangRepository barangRepository) {
        this.barangRepository = barangRepository;
    }

    public List<Barang> semua() {
        return barangRepository.findAllByOrderByTanggalDescIdDesc();
    }

    public Barang cariById(Long id) {
        return barangRepository.findById(id).orElse(null);
    }

    public Barang simpan(Barang barang) {
        return barangRepository.save(barang);
    }

    public long jumlahStatus(String status) {
        return barangRepository.countByStatus(status);
    }

    /**
     * Filter untuk beranda & menu informasi.
     * tab: "terbaru", "hilang", "temuan"
     * kategori: "Semua" atau nama kategori
     * kataKunci: teks pencarian nama/lokasi
     */
    public List<Barang> filter(String tab, String kategori, String kataKunci) {
        List<Barang> sumber;
        if ("hilang".equals(tab)) {
            sumber = barangRepository.findByStatusOrderByTanggalDescIdDesc(Barang.HILANG);
        } else if ("temuan".equals(tab)) {
            sumber = barangRepository.findByStatusOrderByTanggalDescIdDesc(Barang.TEMUAN);
        } else {
            sumber = barangRepository.findAllByOrderByTanggalDescIdDesc();
        }

        List<Barang> hasil = new ArrayList<Barang>();
        for (int i = 0; i < sumber.size(); i++) {
            Barang b = sumber.get(i);

            if (kategori != null && !kategori.isEmpty() && !kategori.equals("Semua")) {
                if (b.getKategori() == null || !b.getKategori().equalsIgnoreCase(kategori)) {
                    continue;
                }
            }

            if (kataKunci != null && !kataKunci.trim().isEmpty()) {
                String kunci = kataKunci.trim().toLowerCase();
                String nama = b.getNama() == null ? "" : b.getNama().toLowerCase();
                String lokasi = b.getLokasi() == null ? "" : b.getLokasi().toLowerCase();
                if (!nama.contains(kunci) && !lokasi.contains(kunci)) {
                    continue;
                }
            }
            hasil.add(b);
        }
        return hasil;
    }

    /** barang yang tampil di menu klaim: barang temuan & barang yang sudah diklaim */
    public List<Barang> barangUntukKlaim() {
        List<Barang> sumber = barangRepository.findAllByOrderByTanggalDescIdDesc();
        List<Barang> hasil = new ArrayList<Barang>();
        for (int i = 0; i < sumber.size(); i++) {
            Barang b = sumber.get(i);
            if (!Barang.HILANG.equals(b.getStatus())) {
                hasil.add(b);
            }
        }
        return hasil;
    }

    /** dipanggil saat barang temuan baru dilaporkan: default disimpan 30 hari */
    public void aturPenyimpananAwal(Barang barang) {
        LocalDate mulai = barang.getTanggal();
        if (mulai == null) {
            mulai = LocalDate.now();
        }
        barang.setTanggalMulai(mulai);
        barang.setTanggalAkhir(mulai.plusDays(30));
        if (barang.getLokasiSimpan() == null) {
            barang.setLokasiSimpan("Satpam Gedung F Lantai 1");
        }
    }
}
