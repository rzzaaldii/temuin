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
import com.example.temuin.repository.BarangRepository;
import com.example.temuin.repository.KlaimRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class KlaimController {

    private BarangRepository barangRepository;
    private KlaimRepository klaimRepository;
    private UploadHelper uploadHelper;
    private SessionHelper sessionHelper;

    public KlaimController(BarangRepository barangRepository, KlaimRepository klaimRepository,
            UploadHelper uploadHelper, SessionHelper sessionHelper) {
        this.barangRepository = barangRepository;
        this.klaimRepository = klaimRepository;
        this.uploadHelper = uploadHelper;
        this.sessionHelper = sessionHelper;
    }

    @GetMapping("/klaim")
    public String menuKlaim(HttpSession session, Model model) {
        User user = sessionHelper.userLogin(session);

        // barang yang tampil di menu klaim: barang temuan & barang yang sudah diklaim
        List<Barang> semua = barangRepository.findAllByOrderByTanggalDescIdDesc();
        List<Barang> daftar = new ArrayList<Barang>();
        for (int i = 0; i < semua.size(); i++) {
            Barang b = semua.get(i);
            if (!Barang.HILANG.equals(b.getStatus())) {
                daftar.add(b);
            }
        }

        // link tiap kartu: user -> form klaim, admin -> verifikasi (jika ada klaim
        // menunggu)
        List<String> link = new ArrayList<String>();
        for (int i = 0; i < daftar.size(); i++) {
            Barang b = daftar.get(i);
            String url = "/barang/" + b.getId();
            if (Barang.TEMUAN.equals(b.getStatus())) {
                if (user.isAdmin()) {
                    List<Klaim> menunggu = klaimRepository.findByBarangAndStatusOrderByIdDesc(b, Klaim.MENUNGGU);
                    if (!menunggu.isEmpty()) {
                        url = "/klaim/verifikasi/" + menunggu.get(0).getId();
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
        model.addAttribute("jumlahLaporan", daftar.size());
        model.addAttribute("jumlahTemuan", barangRepository.countByStatus(Barang.TEMUAN));
        model.addAttribute("jumlahKlaim", barangRepository.countByStatus(Barang.KLAIM));
        model.addAttribute("nav", "klaim");
        return "klaim/menu";
    }

    @GetMapping("/klaim/ajukan/{id}")
    public String formKlaim(@PathVariable Long id, HttpSession session, Model model) {
        Barang barang = barangRepository.findById(id).orElse(null);
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
        Barang barang = barangRepository.findById(id).orElse(null);
        if (barang == null || !Barang.TEMUAN.equals(barang.getStatus())) {
            return "redirect:/klaim";
        }
        String urlBukti = uploadHelper.simpan(bukti);
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
        klaimRepository.save(klaim);

        redirect.addFlashAttribute("pesan", "Klaim berhasil dikirim, menunggu verifikasi admin.");
        return "redirect:/klaim";
    }

    @GetMapping("/klaim/verifikasi/{id}")
    public String halamanVerifikasi(@PathVariable Long id, HttpSession session, Model model) {
        User user = sessionHelper.userLogin(session);
        Klaim klaim = klaimRepository.findById(id).orElse(null);
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
        Klaim klaim = klaimRepository.findById(id).orElse(null);
        if (user.isAdmin() && klaim != null && Klaim.MENUNGGU.equals(klaim.getStatus())) {
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
            redirect.addFlashAttribute("pesan", "Klaim berhasil diverifikasi.");
        }
        return "redirect:/klaim";
    }

    @PostMapping("/klaim/verifikasi/{id}/tolak")
    public String tolak(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        User user = sessionHelper.userLogin(session);
        Klaim klaim = klaimRepository.findById(id).orElse(null);
        if (user.isAdmin() && klaim != null && Klaim.MENUNGGU.equals(klaim.getStatus())) {
            klaim.setStatus(Klaim.DITOLAK);
            klaimRepository.save(klaim);
            redirect.addFlashAttribute("pesan", "Klaim ditolak.");
        }
        return "redirect:/klaim";
    }
}
