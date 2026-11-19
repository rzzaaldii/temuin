package com.example.temuin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.temuin.entity.User;
import com.example.temuin.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String halamanLogin(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String prosesLogin(@RequestParam String email, @RequestParam String password,
            HttpSession session, Model model) {
        User user = userService.login(email.trim(), password);
        if (user == null) {
            model.addAttribute("error", "Email atau kata sandi salah.");
            model.addAttribute("email", email);
            return "auth/login";
        }
        session.setAttribute("userId", user.getId());
        return "redirect:/";
    }

    @GetMapping("/daftar")
    public String halamanDaftar() {
        return "auth/daftar";
    }

    @PostMapping("/daftar")
    public String prosesDaftar(@RequestParam String nama, @RequestParam String email,
            @RequestParam String password, @RequestParam String konfirmasi,
            HttpSession session, Model model) {
        model.addAttribute("nama", nama);
        model.addAttribute("email", email);

        if (nama.trim().isEmpty() || email.trim().isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "Semua kolom wajib diisi.");
            return "auth/daftar";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "Kata sandi minimal 6 karakter.");
            return "auth/daftar";
        }
        if (!password.equals(konfirmasi)) {
            model.addAttribute("error", "Konfirmasi kata sandi tidak sama.");
            return "auth/daftar";
        }
        if (userService.emailSudahAda(email.trim())) {
            model.addAttribute("error", "Email sudah terdaftar.");
            return "auth/daftar";
        }

        User user = userService.daftar(nama.trim(), email.trim(), password);
        session.setAttribute("userId", user.getId());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
