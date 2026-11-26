package com.example.temuin.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.temuin.entity.Barang;
import com.example.temuin.repository.BarangRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class LaporanController {

    private BarangRepository barangRepository;
    private UploadHelper uploadHelper;
    private SessionHelper sessionHelper;

    public LaporanController(BarangRepository barangRepository, UploadHelper uploadHelper, SessionHelper sessionHelper) {
        this.barangRepository = barangRepository;
        this.uploadHelper = uploadHelper;
        this.sessionHelper = sessionHelper;
    }

    @GetMapping("/laporan")
    public String menuLaporan(HttpSession session, Model model) {
        model.addAttribute("user", sessionHelper.userLogin(session));
        model.addAttribute("nav", "laporan");
        return "laporan/menu";
    }

    @GetMapping("/laporan/kehilangan")
    public String formKehilangan() {
        return "laporan/kehilangan";
    }

    @GetMapping("/laporan/temuan")
    public String formTemuan() {
        return "laporan/temuan";
    }

    @PostMapping("/laporan/kehilangan")
    public String kirimKehilangan(@RequestParam String nama, @RequestParam(defaultValue = "") String tanggal,
            @RequestParam(defaultValue = "") String kategori, @RequestParam(required = false) MultipartFile foto,
            @RequestParam(defaultValue = "") String deskripsi, @RequestParam(defaultValue = "") String lokasi,
            @RequestParam(defaultValue = "") String waktu, HttpSession session, Model model,
            RedirectAttributes redirect) {
        if (!validasiForm(nama, tanggal, kategori, lokasi, waktu, deskripsi, null, model)) {
            return "laporan/kehilangan";
        }
        Barang barang = buatBarang(nama, tanggal, kategori, deskripsi, lokasi, waktu, session);
        barang.setStatus(Barang.HILANG);
        barang.setLokasiSimpan(lokasi);
        barang.aturPenyimpananAwal();
        String urlFoto = uploadHelper.simpan(foto);
        if (urlFoto != null) {
            barang.setFoto(urlFoto);
        }
        barangRepository.save(barang);
        redirect.addFlashAttribute("pesan", "Laporan kehilangan berhasil dikirim.");
        return "redirect:/";
    }

    @PostMapping("/laporan/temuan")
    public String kirimTemuan(@RequestParam String nama, @RequestParam(defaultValue = "") String tanggal,
            @RequestParam(defaultValue = "") String kategori, @RequestParam(required = false) MultipartFile foto,
            @RequestParam(defaultValue = "") String deskripsi, @RequestParam(defaultValue = "") String lokasi,
            @RequestParam(defaultValue = "") String waktu, HttpSession session, Model model,
            RedirectAttributes redirect) {
        String urlFoto = uploadHelper.simpan(foto);
        String errorFoto = urlFoto == null ? "Foto barang temuan wajib diisi (jpg/png)." : null;
        if (!validasiForm(nama, tanggal, kategori, lokasi, waktu, deskripsi, errorFoto, model)) {
            return "laporan/temuan";
        }
        Barang barang = buatBarang(nama, tanggal, kategori, deskripsi, lokasi, waktu, session);
        barang.setStatus(Barang.TEMUAN);
        barang.setFoto(urlFoto);
        barang.setLokasiSimpan(lokasi);
        barang.aturPenyimpananAwal();
        barangRepository.save(barang);
        redirect.addFlashAttribute("pesan", "Laporan temuan berhasil dikirim.");
        return "redirect:/";
    }

    private boolean validasiForm(String nama, String tanggal, String kategori, String lokasi, String waktu,
            String deskripsi, String errorFoto, Model model) {
        String errorField = cekFieldKosong(nama, tanggal, kategori, lokasi, waktu);
        String error = errorField != null ? pesanError(errorField) : errorFoto;
        if (error == null) {
            return true;
        }
        model.addAttribute("error", error);
        model.addAttribute("errorField", errorField);
        model.addAttribute("nama", nama);
        model.addAttribute("tanggal", tanggal);
        model.addAttribute("kategori", kategori);
        model.addAttribute("deskripsi", deskripsi);
        model.addAttribute("lokasi", lokasi);
        model.addAttribute("waktu", waktu);
        return false;
    }

    private String cekFieldKosong(String nama, String tanggal, String kategori, String lokasi, String waktu) {
        if (nama.trim().isEmpty()) {
            return "nama";
        }
        if (tanggal.isEmpty()) {
            return "tanggal";
        }
        if (kategori.isEmpty()) {
            return "kategori";
        }
        if (lokasi.isEmpty()) {
            return "lokasi";
        }
        if (waktu.isEmpty()) {
            return "waktu";
        }
        return null;
    }

    private String pesanError(String field) {
        return switch (field) {
            case "nama" -> "Nama barang wajib diisi.";
            case "tanggal" -> "Tanggal wajib dipilih.";
            case "kategori" -> "Kategori barang wajib dipilih.";
            case "lokasi" -> "Lokasi wajib dipilih.";
            case "waktu" -> "Waktu wajib dipilih.";
            default -> "Data laporan belum lengkap.";
        };
    }

    private Barang buatBarang(String nama, String tanggal, String kategori, String deskripsi, String lokasi,
            String waktu, HttpSession session) {
        Barang barang = new Barang();
        barang.setNama(nama.trim());
        barang.setTanggal(LocalDate.parse(tanggal));
        barang.setWaktu(LocalTime.parse(waktu));
        barang.setKategori(kategori);
        barang.setDeskripsi(deskripsi);
        barang.setLokasi(lokasi);
        barang.setPelapor(sessionHelper.userLogin(session));
        return barang;
    }
}
