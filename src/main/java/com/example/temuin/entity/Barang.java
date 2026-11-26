package com.example.temuin.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "barang")
public class Barang {

    // status barang
    public static final String HILANG = "HILANG";
    public static final String TEMUAN = "TEMUAN";
    public static final String KLAIM = "KLAIM";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nama;

    private String kategori;

    private LocalDate tanggal;

    private LocalTime waktu;

    private String lokasi;

    @Column(length = 1000)
    private String deskripsi;

    private String foto;

    @Column(nullable = false)
    private String status;

    // diisi admin
    private String lokasiSimpan;

    private LocalDate tanggalMulai;

    private LocalDate tanggalAkhir;

    private LocalDate tanggalKlaim;

    @ManyToOne
    @JoinColumn(name = "pelapor_id")
    private User pelapor;

    public Barang() {
    }

    public String getTanggalText() {
        return FormatTanggal.tanggal(tanggal);
    }

    public String getWaktuText() {
        return FormatTanggal.waktu(waktu);
    }

    public String getTanggalMulaiText() {
        return FormatTanggal.tanggal(tanggalMulai);
    }

    public String getTanggalAkhirText() {
        return FormatTanggal.tanggal(tanggalAkhir);
    }

    public String getTanggalKlaimText() {
        return FormatTanggal.tanggal(tanggalKlaim);
    }

    public String getStatusText() {
        if (HILANG.equals(status)) {
            return "Hilang";
        }
        if (KLAIM.equals(status)) {
            return "Klaim";
        }
        return "Temuan";
    }

    public String getBadgeClass() {
        if (HILANG.equals(status)) {
            return "badge badge-hilang";
        }
        if (KLAIM.equals(status)) {
            return "badge badge-klaim";
        }
        return "badge badge-temuan";
    }

    public String getFotoUrl() {
        if (foto == null || foto.isEmpty()) {
            return "/img/item-botol.png";
        }
        return foto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalTime getWaktu() {
        return waktu;
    }

    public void setWaktu(LocalTime waktu) {
        this.waktu = waktu;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLokasiSimpan() {
        return lokasiSimpan;
    }

    public void setLokasiSimpan(String lokasiSimpan) {
        this.lokasiSimpan = lokasiSimpan;
    }

    public LocalDate getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(LocalDate tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public LocalDate getTanggalAkhir() {
        return tanggalAkhir;
    }

    public void setTanggalAkhir(LocalDate tanggalAkhir) {
        this.tanggalAkhir = tanggalAkhir;
    }

    public LocalDate getTanggalKlaim() {
        return tanggalKlaim;
    }

    public void setTanggalKlaim(LocalDate tanggalKlaim) {
        this.tanggalKlaim = tanggalKlaim;
    }

    public User getPelapor() {
        return pelapor;
    }

    public void setPelapor(User pelapor) {
        this.pelapor = pelapor;
    }

    /** dipanggil saat barang temuan baru dilaporkan: default disimpan 30 hari */
    public void aturPenyimpananAwal() {
        LocalDate mulai = tanggal;
        if (mulai == null) {
            mulai = LocalDate.now();
        }
        tanggalMulai = mulai;
        tanggalAkhir = mulai.plusDays(30);
        if (lokasiSimpan == null) {
            lokasiSimpan = "Satpam Gedung F Lantai 1";
        }
    }
}
