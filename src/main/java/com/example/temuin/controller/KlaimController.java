package com.example.temuin.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.temuin.entity.Barang;
import com.example.temuin.entity.Klaim;
import com.example.temuin.entity.User;
import com.example.temuin.service.BarangService;
import com.example.temuin.service.FileService;
import com.example.temuin.service.KlaimService;

import jakarta.servlet.http.HttpSession;

@Controller
public class KlaimController {

    private BarangService barangService;
    private KlaimService klaimService;
    private FileService fileService;
    private SessionHelper sessionHelper;

    public KlaimController(BarangService barangService, KlaimService klaimService, FileService fileService,
            SessionHelper sessionHelper) {
        this.barangService = barangService;
        this.klaimService = klaimService;
        this.fileService = fileService;
        this.sessionHelper = sessionHelper;
    }

    @GetMapping("/klaim")
    public String menuKlaim(HttpSession session, Model model) {
        User user = sessionHelper.userLogin(session);
        List<Barang> daftar = barangService.barangUntukKlaim();

        // link tiap kartu: user -> form klaim, admin -> verifikasi (jika ada klaim menunggu)
        List<String> link = new ArrayList<String>();
        for (int i = 0; i < daftar.size(); i++) {
            Barang b = daftar.get(i);
            String url = "/barang/" + b.getId();
            if (Barang.TEMUAN.equals(b.getStatus())) {
                if (user.isAdmin()) {
                    Klaim k = klaimService.klaimMenunggu(b);
                    if (k != null) {
                        url = "/klaim/verifikasi/" + k.getId();
                    }
                } else {
                    url = "/klaim/ajukan/" + b.getId();
                }
            }
            link.add(url);
        }

        model.addAttribute("user", user);
        model.addAttribute("daftarBarang", daftar);
        model.addAttribute("daftarLink", link);
        model.addAttribute("jumlahLaporan", barangService.jumlahStatus(Barang.HILANG));
        model.addAttribute("jumlahTemuan", barangService.jumlahStatus(Barang.TEMUAN));
        model.addAttribute("jumlahKlaim", barangService.jumlahStatus(Barang.KLAIM));
        model.addAttribute("nav", "klaim");
        return "klaim/menu";
    }

    @GetMapping("/klaim/ajukan/{id}")
    public String formKlaim(@PathVariable Long id, HttpSession session, Model model) {
        Barang barang = barangService.cariById(id);
        if (barang == null || !Barang.TEMUAN.equals(barang.getStatus())) {
            return "redirect:/klaim";
        }
        model.addAttribute("barang", barang);
        model.addAttribute("user", sessionHelper.userLogin(session));
        return "klaim/ajukan";
    }

    @PostMapping("/klaim/ajukan/{id}")
    public String kirimKlaim(@PathVariable Long id, @RequestParam String nama, @RequestParam String email,
            @RequestParam String telepon, @RequestParam(defaultValue = "") String detail,
            @RequestParam(required = false) MultipartFile bukti, HttpSession session, Model model,
            RedirectAttributes redirect) {
        Barang barang = barangService.cariById(id);
        if (barang == null || !Barang.TEMUAN.equals(barang.getStatus())) {
            return "redirect:/klaim";
        }
        String urlBukti = fileService.simpan(bukti);
        String error = null;
        if (nama.trim().isEmpty() || email.trim().isEmpty() || telepon.trim().isEmpty()) {
            error = "Nama, email, dan nomor telepon wajib diisi.";
        } else if (urlBukti == null) {
            error = "Bukti kepemilikan wajib diunggah (jpg/png).";
        }
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("barang", barang);
            model.addAttribute("user", sessionHelper.userLogin(session));
            return "klaim/ajukan";
        }

        Klaim klaim = new Klaim();
        klaim.setBarang(barang);
        klaim.setUser(sessionHelper.userLogin(session));
        klaim.setNama(nama.trim());
        klaim.setEmail(email.trim());
        klaim.setTelepon(telepon.trim());
        klaim.setDetail(detail);
        klaim.setBukti(urlBukti);
        klaim.setStatus(Klaim.MENUNGGU);
        klaim.setTanggalPengajuan(LocalDate.now());
        klaimService.simpan(klaim);

        redirect.addFlashAttribute("pesan", "Klaim berhasil dikirim, menunggu verifikasi admin.");
        return "redirect:/klaim";
    }

    @GetMapping("/klaim/verifikasi/{id}")
    public String halamanVerifikasi(@PathVariable Long id, HttpSession session, Model model) {
        User user = sessionHelper.userLogin(session);
        Klaim klaim = klaimService.cariById(id);
        if (!user.isAdmin() || klaim == null) {
            return "redirect:/klaim";
        }
        model.addAttribute("klaim", klaim);
        model.addAttribute("barang", klaim.getBarang());
        return "klaim/verifikasi";
    }

    @PostMapping("/klaim/verifikasi/{id}/terima")
    public String terima(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        User user = sessionHelper.userLogin(session);
        Klaim klaim = klaimService.cariById(id);
        if (user.isAdmin() && klaim != null && Klaim.MENUNGGU.equals(klaim.getStatus())) {
            klaimService.terima(klaim);
            redirect.addFlashAttribute("pesan", "Klaim berhasil diverifikasi.");
        }
        return "redirect:/klaim";
    }

    @PostMapping("/klaim/verifikasi/{id}/tolak")
    public String tolak(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        User user = sessionHelper.userLogin(session);
        Klaim klaim = klaimService.cariById(id);
        if (user.isAdmin() && klaim != null && Klaim.MENUNGGU.equals(klaim.getStatus())) {
            klaimService.tolak(klaim);
            redirect.addFlashAttribute("pesan", "Klaim ditolak.");
        }
        return "redirect:/klaim";
    }
}
