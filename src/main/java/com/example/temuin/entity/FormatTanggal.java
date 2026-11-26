package com.example.temuin.entity;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Helper untuk menampilkan tanggal dan waktu dengan format Indonesia,
 * contoh: "4 November 2026" dan "09.00".
 */
public class FormatTanggal {

    private static final String[] NAMA_BULAN = {
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

    public static String tanggal(LocalDate tgl) {
        if (tgl == null) {
            return "-";
        }
        String bulan = NAMA_BULAN[tgl.getMonthValue() - 1];
        return tgl.getDayOfMonth() + " " + bulan + " " + tgl.getYear();
    }

    public static String waktu(LocalTime jam) {
        if (jam == null) {
            return "-";
        }
        String h = String.valueOf(jam.getHour());
        String m = String.valueOf(jam.getMinute());
        if (h.length() == 1) {
            h = "0" + h;
        }
        if (m.length() == 1) {
            m = "0" + m;
        }
        return h + "." + m;
    }
}
