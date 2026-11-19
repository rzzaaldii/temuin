package com.example.temuin.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.temuin.entity.Barang;
import com.example.temuin.entity.User;
import com.example.temuin.service.BarangService;

import jakarta.servlet.http.HttpSession;

@Controller
public class InformasiController {

    private BarangService barangService;
    private SessionHelper sessionHelper;

    public InformasiController(BarangService barangService, SessionHelper sessionHelper) {
        this.barangService = barangService;
        this.sessionHelper = sessionHelper;
    }

    @GetMapping("/informasi")
    public String menuInformasi(@RequestParam(defaultValue = "terbaru") String tab, HttpSession session,
            Model model) {
        model.addAttribute("user", sessionHelper.userLogin(session));
        model.addAttribute("daftarBarang", barangService.filter(tab, "Semua", ""));
        model.addAttribute("tab", tab);
        model.addAttribute("nav", "informasi");
        return "informasi/menu";
    }

    @GetMapping("/barang/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        Barang barang = barangService.cariById(id);
        if (barang == null) {
            return "redirect:/informasi";
        }
        User user = sessionHelper.userLogin(session);
        model.addAttribute("user", user);
        model.addAttribute("barang", barang);
        if (user.isAdmin()) {
            return "informasi/kelola";
        }
        return "informasi/detail";
    }

    @PostMapping("/barang/{id}/perbarui")
    public String perbarui(@PathVariable Long id, @RequestParam String lokasiSimpan,
            @RequestParam String tanggalMulai, @RequestParam String tanggalAkhir, HttpSession session,
            RedirectAttributes redirect) {
        User user = sessionHelper.userLogin(session);
        Barang barang = barangService.cariById(id);
        if (barang == null || !user.isAdmin()) {
            return "redirect:/informasi";
        }
        barang.setLokasiSimpan(lokasiSimpan);
        if (!tanggalMulai.isEmpty()) {
            barang.setTanggalMulai(LocalDate.parse(tanggalMulai));
        }
        if (!tanggalAkhir.isEmpty()) {
            barang.setTanggalAkhir(LocalDate.parse(tanggalAkhir));
        }
        barangService.simpan(barang);
        redirect.addFlashAttribute("pesan", "Informasi barang berhasil diperbarui.");
        return "redirect:/barang/" + id;
    }
}
