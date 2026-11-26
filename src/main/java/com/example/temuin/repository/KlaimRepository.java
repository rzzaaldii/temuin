package com.example.temuin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.temuin.entity.Barang;
import com.example.temuin.entity.Klaim;

public interface KlaimRepository extends JpaRepository<Klaim, Long> {

    List<Klaim> findByStatusOrderByIdDesc(String status);

    List<Klaim> findByBarangAndStatus(Barang barang, String status);

    List<Klaim> findByBarangAndStatusOrderByIdDesc(Barang barang, String status);
}
