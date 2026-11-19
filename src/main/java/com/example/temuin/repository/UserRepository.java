package com.example.temuin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.temuin.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
