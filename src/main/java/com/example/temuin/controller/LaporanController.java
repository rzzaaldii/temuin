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
        String error = cekForm(nama, tanggal, kategori, lokasi, waktu);
        if (error != null) {
            model.addAttribute("error", error);
            return "laporan/kehilangan";
        }
        Barang barang = buatBarang(nama, tanggal, kategori, deskripsi, lokasi, waktu, session);
        barang.setStatus(Barang.HILANG);
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
        String error = cekForm(nama, tanggal, kategori, lokasi, waktu);
        String urlFoto = null;
        if (error == null) {
            urlFoto = uploadHelper.simpan(foto);
            if (urlFoto == null) {
                error = "Foto barang temuan wajib diisi (jpg/png).";
            }
        }
        if (error != null) {
            model.addAttribute("error", error);
            return "laporan/temuan";
        }
        Barang barang = buatBarang(nama, tanggal, kategori, deskripsi, lokasi, waktu, session);
        barang.setStatus(Barang.TEMUAN);
        barang.setFoto(urlFoto);
        barang.aturPenyimpananAwal();
        barangRepository.save(barang);
        redirect.addFlashAttribute("pesan", "Laporan temuan berhasil dikirim.");
        return "redirect:/";
    }

    private String cekForm(String nama, String tanggal, String kategori, String lokasi, String waktu) {
        if (nama.trim().isEmpty()) {
            return "Nama barang wajib diisi.";
        }
        if (tanggal.isEmpty()) {
            return "Tanggal wajib dipilih.";
        }
        if (kategori.isEmpty()) {
            return "Kategori barang wajib dipilih.";
        }
        if (lokasi.isEmpty()) {
            return "Lokasi wajib dipilih.";
        }
        if (waktu.isEmpty()) {
            return "Waktu wajib dipilih.";
        }
        return null;
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
