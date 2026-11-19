package com.example.temuin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.temuin.entity.Barang;

public interface BarangRepository extends JpaRepository<Barang, Long> {

    List<Barang> findAllByOrderByTanggalDescIdDesc();

    List<Barang> findByStatusOrderByTanggalDescIdDesc(String status);

    long countByStatus(String status);
}
