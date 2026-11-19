package com.example.temuin.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.temuin.entity.Barang;
import com.example.temuin.service.BarangService;

import jakarta.servlet.http.HttpSession;

@Controller
public class BerandaController {

    private BarangService barangService;
    private SessionHelper sessionHelper;

    public BerandaController(BarangService barangService, SessionHelper sessionHelper) {
        this.barangService = barangService;
        this.sessionHelper = sessionHelper;
    }

    @GetMapping("/")
    public String beranda(@RequestParam(defaultValue = "terbaru") String tab,
            @RequestParam(defaultValue = "Semua") String kategori,
            @RequestParam(defaultValue = "") String q,
            HttpSession session, Model model) {
        List<Barang> daftar = barangService.filter(tab, kategori, q);

        model.addAttribute("user", sessionHelper.userLogin(session));
        model.addAttribute("daftarBarang", daftar);
        model.addAttribute("tab", tab);
        model.addAttribute("kategori", kategori);
        model.addAttribute("q", q);
        model.addAttribute("nav", "beranda");
        return "beranda";
    }
}
