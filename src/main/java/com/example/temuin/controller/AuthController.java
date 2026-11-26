package com.example.temuin.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.temuin.entity.User;
import com.example.temuin.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        User user = userRepository.findByEmail(email.trim());
        if (user == null || !user.getPassword().equals(hash(password))) {
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
        if (userRepository.findByEmail(email.trim()) != null) {
            model.addAttribute("error", "Email sudah terdaftar.");
            return "auth/daftar";
        }

        User user = new User(nama.trim(), email.trim(), hash(password), User.ROLE_USER);
        user = userRepository.save(user);
        session.setAttribute("userId", user.getId());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    /** hash password dengan SHA-256 supaya tidak tersimpan polos di database */
    public static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hasil = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hasil.length; i++) {
                String hex = Integer.toHexString(0xff & hasil[i]);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
