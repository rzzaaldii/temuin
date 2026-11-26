package com.example.temuin.controller;

import org.springframework.stereotype.Component;

import com.example.temuin.entity.User;
import com.example.temuin.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

/**
 * Mengambil user yang sedang login dari session.
 */
@Component
public class SessionHelper {

    private UserRepository userRepository;

    public SessionHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User userLogin(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (id == null) {
            return null;
        }
        return userRepository.findById((Long) id).orElse(null);
    }
}
