package service;

import java.util.ArrayList;

import model.Akun;
import model.AkunPembeli;
import model.AkunPenjual;
import model.Mahasiswa;
import util.Tampilan;

/**
 * Class Autentikasi
 * -----------------
 * Fungsi : mengelola akun pengguna: mendaftarkan pembeli baru dan memeriksa login.
 *          Login selalu dicocokkan dengan PERAN yang dipilih (pembeli tidak bisa masuk
 *          lewat pintu penjual, begitu juga sebaliknya).
 */
public class Autentikasi {

    private ArrayList<Akun> daftarAkun = new ArrayList<>();

    // ==================== VALIDASI (mengembalikan pesan error, atau null jika valid) ====================

    public String pesanUsernameTidakValid(String username) {
        if (!username.matches("[A-Za-z0-9_]{4,15}")) {
            return "Username 4-15 karakter: huruf, angka, atau underscore (_).";
        }
        if (cariAkun(username) != null) {
            return "Username \"" + username + "\" sudah dipakai.";
        }
        return null;
    }

    public String pesanNimTidakValid(String nim) {
        if (!nim.matches("\\d{5,12}")) {
            return "NIM harus berupa angka 5-12 digit.";
        }
        if (cariPembeliByNim(nim) != null) {
            return "NIM " + nim + " sudah terdaftar.";
        }
        return null;
    }

    // ==================== PENDAFTARAN ====================

    // Mendaftarkan pembeli baru. Mengembalikan null jika ada data yang tidak valid.
    public AkunPembeli daftarPembeli(String username, String password, String nim, String nama, String jurusan) {
        String masalah = pesanUsernameTidakValid(username);
        if (masalah == null) {
            masalah = Akun.pesanPasswordTidakValid(password);
        }
        if (masalah == null) {
            masalah = pesanNimTidakValid(nim);
        }
        if (masalah == null && nama.trim().isEmpty()) {
            masalah = "Nama tidak boleh kosong.";
        }
        if (masalah != null) {
            Tampilan.gagal(masalah);
            return null;
        }

        String jurusanFinal = jurusan.trim().isEmpty() ? "Belum diisi" : jurusan.trim();
        AkunPembeli akun = new AkunPembeli(username, password, new Mahasiswa(nim, nama.trim(), jurusanFinal));
        daftarAkun.add(akun);
        return akun;
    }

    // Menambah akun penjual (dipanggil oleh program, bukan oleh pengguna)
    public AkunPenjual tambahPenjual(String username, String password, String nama) {
        if (cariAkun(username) != null) {
            return null;
        }
        AkunPenjual akun = new AkunPenjual(username, password, nama);
        daftarAkun.add(akun);
        return akun;
    }

    // ==================== LOGIN ====================

    // Login sesuai peran. Hasil null jika username / password / peran tidak cocok.
    // Pesan error sengaja dibuat umum agar orang lain tidak bisa menebak username yang ada.
    public Akun login(String username, String password, String peran) {
        Akun akun = cariAkun(username);
        if (akun == null || !akun.getPeran().equals(peran) || !akun.passwordCocok(password)) {
            Tampilan.gagal("Username atau password salah (atau akun bukan akun " + peran.toLowerCase() + ").");
            return null;
        }
        return akun;
    }

    // ==================== PENCARIAN & TAMPILAN ====================

    public Akun cariAkun(String username) {
        for (Akun akun : daftarAkun) {
            if (akun.getUsername().equalsIgnoreCase(username)) {
                return akun;
            }
        }
        return null;
    }

    public AkunPembeli cariPembeliByNim(String nim) {
        for (Akun akun : daftarAkun) {
            if (akun instanceof AkunPembeli) {
                AkunPembeli pembeli = (AkunPembeli) akun;
                if (pembeli.getProfil().getNim().equals(nim)) {
                    return pembeli;
                }
            }
        }
        return null;
    }

    // Menampilkan semua pembeli terdaftar (untuk penjual)
    public void tampilkanDaftarPembeli() {
        Tampilan.cetakSubJudul("Daftar Pembeli Terdaftar");
        int jumlah = 0;
        for (Akun akun : daftarAkun) {
            if (akun instanceof AkunPembeli) {
                if (jumlah == 0) {
                    Mahasiswa.cetakHeaderTabel();
                }
                ((AkunPembeli) akun).getProfil().tampilkanInfo();
                jumlah++;
            }
        }
        if (jumlah == 0) {
            System.out.println("  Belum ada pembeli terdaftar.");
        } else {
            Tampilan.cetakGaris();
            System.out.println(" Total " + jumlah + " pembeli");
        }
    }
}
