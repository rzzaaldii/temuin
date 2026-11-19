package com.example.temuin.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import com.example.temuin.entity.User;
import com.example.temuin.repository.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** mengembalikan user jika email & password benar, null jika salah */
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        if (user.getPassword().equals(hash(password))) {
            return user;
        }
        return null;
    }

    public boolean emailSudahAda(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public User daftar(String nama, String email, String password) {
        User user = new User(nama, email, hash(password), User.ROLE_USER);
        return userRepository.save(user);
    }

    public User simpan(User user) {
        return userRepository.save(user);
    }

    public User cariById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public long jumlahUser() {
        return userRepository.count();
    }

    /** hash password dengan SHA-256 supaya tidak tersimpan polos di database */
    public String hash(String text) {
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
