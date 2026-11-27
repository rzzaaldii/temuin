package com.example.temuin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.temuin.entity.Barang;
import com.example.temuin.repository.BarangRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class InformasiController {

    private BarangRepository barangRepository;
    private SessionHelper sessionHelper;

    public InformasiController(BarangRepository barangRepository, SessionHelper sessionHelper) {
        this.barangRepository = barangRepository;
        this.sessionHelper = sessionHelper;
    }

    @GetMapping("/barang/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        Barang barang = barangRepository.findById(id).orElse(null);
        if (barang == null) {
            return "redirect:/";
        }
        model.addAttribute("user", sessionHelper.userLogin(session));
        model.addAttribute("barang", barang);
        return "informasi/detail";
    }
}
