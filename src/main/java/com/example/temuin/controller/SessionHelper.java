package com.example.temuin.controller;

import org.springframework.stereotype.Component;

import com.example.temuin.entity.User;
import com.example.temuin.service.UserService;

import jakarta.servlet.http.HttpSession;

/**
 * Mengambil user yang sedang login dari session.
 */
@Component
public class SessionHelper {

    private UserService userService;

    public SessionHelper(UserService userService) {
        this.userService = userService;
    }

    public User userLogin(HttpSession session) {
        Object id = session.getAttribute("userId");
        if (id == null) {
            return null;
        }
        return userService.cariById((Long) id);
    }
}
