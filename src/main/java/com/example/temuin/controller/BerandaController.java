package com.example.temuin.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.temuin.entity.Barang;
import com.example.temuin.repository.BarangRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class BerandaController {

    private BarangRepository barangRepository;
    private SessionHelper sessionHelper;

    public BerandaController(BarangRepository barangRepository, SessionHelper sessionHelper) {
        this.barangRepository = barangRepository;
        this.sessionHelper = sessionHelper;
    }

    @GetMapping("/")
    public String beranda(@RequestParam(defaultValue = "terbaru") String tab,
            @RequestParam(defaultValue = "Semua") String kategori,
            @RequestParam(defaultValue = "") String q,
            HttpSession session, Model model) {
        List<Barang> sumber;
        if ("hilang".equals(tab)) {
            sumber = barangRepository.findByStatusOrderByTanggalDescIdDesc(Barang.HILANG);
        } else if ("temuan".equals(tab)) {
            sumber = barangRepository.findByStatusOrderByTanggalDescIdDesc(Barang.TEMUAN);
        } else {
            sumber = barangRepository.findAllByOrderByTanggalDescIdDesc();
        }

        // filter kategori & kata kunci (nama/lokasi)
        List<Barang> daftar = new ArrayList<Barang>();
        for (int i = 0; i < sumber.size(); i++) {
            Barang b = sumber.get(i);

            if (!kategori.isEmpty() && !kategori.equals("Semua")) {
                if (b.getKategori() == null || !b.getKategori().equalsIgnoreCase(kategori)) {
                    continue;
                }
            }

            if (!q.trim().isEmpty()) {
                String kunci = q.trim().toLowerCase();
                String nama = b.getNama() == null ? "" : b.getNama().toLowerCase();
                String lokasi = b.getLokasi() == null ? "" : b.getLokasi().toLowerCase();
                if (!nama.contains(kunci) && !lokasi.contains(kunci)) {
                    continue;
                }
            }
            daftar.add(b);
        }

        model.addAttribute("user", sessionHelper.userLogin(session));
        model.addAttribute("daftarBarang", daftar);
        model.addAttribute("tab", tab);
        model.addAttribute("kategori", kategori);
        model.addAttribute("q", q);
        model.addAttribute("nav", "beranda");
        return "beranda";
    }
}
