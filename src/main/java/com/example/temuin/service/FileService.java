package com.example.temuin.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    /**
     * Simpan file upload ke folder uploads, lalu kembalikan URL-nya.
     * Jika file kosong, kembalikan null.
     */
    public String simpan(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String namaAsli = file.getOriginalFilename();
        String ekstensi = "";
        if (namaAsli != null && namaAsli.lastIndexOf('.') >= 0) {
            ekstensi = namaAsli.substring(namaAsli.lastIndexOf('.')).toLowerCase();
        }
        if (!ekstensi.equals(".jpg") && !ekstensi.equals(".jpeg") && !ekstensi.equals(".png")
                && !ekstensi.equals(".webp") && !ekstensi.equals(".gif")) {
            return null;
        }
        String namaBaru = UUID.randomUUID().toString() + ekstensi;

        File folder = new File(uploadDir).getAbsoluteFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File tujuan = new File(folder, namaBaru);
        try {
            file.transferTo(tujuan);
        } catch (IOException e) {
            return null;
        }
        return "/uploads/" + namaBaru;
    }
}
