// ===== Temuin - interaksi sederhana (dropdown, kalender, waktu, foto) =====

var NAMA_BULAN = ["Januari", "Februari", "Maret", "April", "Mei", "Juni",
    "Juli", "Agustus", "September", "Oktober", "November", "Desember"];
var NAMA_BULAN_EN = ["January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"];

// ---------- password ----------
function togglePassword(idInput) {
    var input = document.getElementById(idInput);
    if (input.type === "password") {
        input.type = "text";
    } else {
        input.type = "password";
    }
}

// ---------- dropdown ----------
function tutupSemuaPopup(kecuali) {
    var daftar = document.querySelectorAll(".dropdown.open, .calendar.open");
    for (var i = 0; i < daftar.length; i++) {
        if (daftar[i].id !== kecuali) {
            daftar[i].classList.remove("open");
        }
    }
}

function toggleDropdown(idPopup) {
    var popup = document.getElementById(idPopup);
    var sudahBuka = popup.classList.contains("open");
    tutupSemuaPopup(idPopup);
    if (sudahBuka) {
        popup.classList.remove("open");
    } else {
        popup.classList.add("open");
    }
}

function pilihOpsi(namaField, nilai) {
    document.getElementById(namaField + "-value").value = nilai;
    var label = document.getElementById(namaField + "-label");
    label.textContent = nilai;
    label.classList.add("filled");
    document.getElementById(namaField + "-popup").classList.remove("open");
}

function klikDiLuar(event) {
    var target = event.target;
    if (target.closest(".field") === null) {
        tutupSemuaPopup("");
    }
}

document.addEventListener("click", klikDiLuar);

// ---------- waktu ----------
function setWaktu(namaField) {
    var jam = parseInt(document.getElementById(namaField + "-jam").value, 10);
    var menit = parseInt(document.getElementById(namaField + "-menit").value, 10);
    if (isNaN(jam) || jam < 0 || jam > 23) {
        jam = 0;
    }
    if (isNaN(menit) || menit < 0 || menit > 59) {
        menit = 0;
    }
    var teks = duaDigit(jam) + ":" + duaDigit(menit);
    document.getElementById(namaField + "-value").value = teks;
    var label = document.getElementById(namaField + "-label");
    label.textContent = duaDigit(jam) + "." + duaDigit(menit);
    label.classList.add("filled");
    document.getElementById(namaField + "-popup").classList.remove("open");
}

function duaDigit(angka) {
    if (angka < 10) {
        return "0" + angka;
    }
    return "" + angka;
}

// ---------- kalender ----------
// menyimpan bulan yang sedang tampil untuk tiap kalender
var posisiKalender = {};

function bukaKalender(namaField) {
    var idPopup = namaField + "-popup";
    var popup = document.getElementById(idPopup);
    if (popup.classList.contains("open")) {
        popup.classList.remove("open");
        return;
    }
    tutupSemuaPopup(idPopup);

    var nilai = document.getElementById(namaField + "-value").value;
    var tgl = new Date();
    if (nilai !== "") {
        tgl = new Date(nilai + "T00:00:00");
    }
    posisiKalender[namaField] = { tahun: tgl.getFullYear(), bulan: tgl.getMonth() };
    gambarKalender(namaField);
    popup.classList.add("open");
}

function gantiBulan(namaField, arah) {
    var pos = posisiKalender[namaField];
    pos.bulan = pos.bulan + arah;
    if (pos.bulan < 0) {
        pos.bulan = 11;
        pos.tahun = pos.tahun - 1;
    }
    if (pos.bulan > 11) {
        pos.bulan = 0;
        pos.tahun = pos.tahun + 1;
    }
    gambarKalender(namaField);
}

function gambarKalender(namaField) {
    var pos = posisiKalender[namaField];
    var tahun = pos.tahun;
    var bulan = pos.bulan;
    var terpilih = document.getElementById(namaField + "-value").value;

    document.getElementById(namaField + "-judul").textContent = NAMA_BULAN_EN[bulan] + " " + tahun;

    var grid = document.getElementById(namaField + "-grid");
    var html = "";
    var hari = ["Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"];
    for (var h = 0; h < 7; h++) {
        var kelasHari = h >= 5 ? "wk" : "";
        html = html + "<span class='" + kelasHari + "'>" + hari[h] + "</span>";
    }

    // hari pertama (Senin = 0)
    var awal = new Date(tahun, bulan, 1).getDay();
    awal = (awal + 6) % 7;
    var mulai = new Date(tahun, bulan, 1 - awal);

    for (var i = 0; i < 42; i++) {
        var d = new Date(mulai.getFullYear(), mulai.getMonth(), mulai.getDate() + i);
        var kolom = i % 7;
        var kelas = "";
        if (d.getMonth() !== bulan) {
            kelas = kelas + " other";
        }
        if (kolom >= 5) {
            kelas = kelas + " wk";
        }
        var iso = d.getFullYear() + "-" + duaDigit(d.getMonth() + 1) + "-" + duaDigit(d.getDate());
        if (iso === terpilih) {
            kelas = kelas + " sel";
        }
        html = html + "<button type='button' class='" + kelas + "' onclick=\"pilihTanggal('" + namaField +
            "','" + iso + "')\">" + d.getDate() + "</button>";
    }
    grid.innerHTML = html;
}

function pilihTanggal(namaField, iso) {
    document.getElementById(namaField + "-value").value = iso;
    var bagian = iso.split("-");
    var teks = parseInt(bagian[2], 10) + " " + NAMA_BULAN[parseInt(bagian[1], 10) - 1] + " " + bagian[0];
    var label = document.getElementById(namaField + "-label");
    var awalan = label.getAttribute("data-prefix");
    if (awalan !== null) {
        teks = awalan + teks;
    }
    label.textContent = teks;
    label.classList.add("filled");
    document.getElementById(namaField + "-popup").classList.remove("open");
}

// ---------- foto (bottom sheet) ----------
var inputFotoAktif = null;

function bukaSheet(idInput) {
    inputFotoAktif = idInput;
    document.getElementById("sheet").classList.add("open");
}

function tutupSheet(event) {
    if (event.target.id === "sheet") {
        document.getElementById("sheet").classList.remove("open");
    }
}

function ambilGambar() {
    var input = document.getElementById(inputFotoAktif);
    input.setAttribute("capture", "environment");
    document.getElementById("sheet").classList.remove("open");
    input.click();
}

function pilihFile() {
    var input = document.getElementById(inputFotoAktif);
    input.removeAttribute("capture");
    document.getElementById("sheet").classList.remove("open");
    input.click();
}

function tampilkanPreview(input) {
    if (input.files.length === 0) {
        return;
    }
    var preview = document.getElementById(input.id + "-preview");
    var reader = new FileReader();
    reader.onload = selesaiBaca;
    reader.readAsDataURL(input.files[0]);

    function selesaiBaca(e) {
        preview.src = e.target.result;
        preview.style.display = "block";
        var icon = document.getElementById(input.id + "-icon");
        if (icon !== null) {
            icon.style.display = "none";
        }
    }
}

// drag & drop gambar ke kotak upload
function seretMasuk(event, kotak) {
    event.preventDefault();
    kotak.classList.add("dragover");
}

function seretKeluar(kotak) {
    kotak.classList.remove("dragover");
}

function lepasFile(event, kotak, idInput) {
    event.preventDefault();
    kotak.classList.remove("dragover");
    var input = document.getElementById(idInput);
    input.files = event.dataTransfer.files;
    tampilkanPreview(input);
}

// ---------- toast pesan ----------
function sembunyikanToast() {
    var toast = document.getElementById("toast");
    if (toast !== null) {
        toast.style.display = "none";
    }
}

setTimeout(sembunyikanToast, 3000);
