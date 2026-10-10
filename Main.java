package main;

import model.Akun;
import model.AkunPembeli;
import model.AkunPenjual;
import model.Produk;
import model.Promo;
import service.Autentikasi;
import service.Kantin;
import util.Input;
import util.Tampilan;

/**
 * Class Main
 * ----------
 * Fungsi : class utama Sistem Pengelolaan Kantin Kampus (versi interaktif).
 *          Menyiapkan data awal (menu, promo, akun), lalu menampilkan menu awal:
 *          login sebagai Pembeli, login sebagai Penjual, atau daftar akun pembeli baru.
 *          Setelah login, alur dilanjutkan oleh MenuPembeli atau MenuPenjual.
 *
 * Catatan: semua data disimpan di memori, jadi kembali ke kondisi awal setiap program dijalankan ulang.
 */
public class Main {

    // CATATAN: isi dengan nama dan NIM kamu
    private static final String NAMA_PEMBUAT = "[Nama Kamu]";
    private static final String NIM_PEMBUAT = "[NIM Kamu]";

    private static final int BATAS_PERCOBAAN_LOGIN = 3;

    public static void main(String[] args) {
        Kantin kantin = siapkanKantin();
        Autentikasi autentikasi = siapkanAkun();

        Tampilan.cetakJudul("SISTEM PENGELOLAAN KANTIN KAMPUS", "Project Object Oriented Programming - Java");
        System.out.println(" Disusun oleh : " + NAMA_PEMBUAT + " (" + NIM_PEMBUAT + ")");

        boolean berjalan = true;
        while (berjalan) {
            Tampilan.cetakBagian("SELAMAT DATANG DI " + kantin.getNamaKantin().toUpperCase());
            Tampilan.cetakPilihan(1, "Login sebagai Pembeli");
            Tampilan.cetakPilihan(2, "Login sebagai Penjual");
            Tampilan.cetakPilihan(3, "Daftar akun pembeli baru");
            Tampilan.cetakPilihan(4, "Lihat akun demo");
            Tampilan.cetakPilihan(0, "Keluar");

            switch (Input.pilihan(4)) {
                case 1:
                    Akun pembeli = login(autentikasi, Akun.PERAN_PEMBELI);
                    if (pembeli != null) {
                        Tampilan.sukses("Login berhasil. Selamat datang, " + pembeli.getNamaTampil() + "!");
                        new MenuPembeli(kantin, (AkunPembeli) pembeli).jalankan();
                    }
                    break;
                case 2:
                    Akun penjual = login(autentikasi, Akun.PERAN_PENJUAL);
                    if (penjual != null) {
                        Tampilan.sukses("Login berhasil. Selamat bekerja, " + penjual.getNamaTampil() + "!");
                        new MenuPenjual(kantin, autentikasi, (AkunPenjual) penjual).jalankan();
                    }
                    break;
                case 3:
                    daftar(autentikasi);
                    break;
                case 4:
                    tampilkanAkunDemo();
                    break;
                default:
                    berjalan = false;
            }
        }

        System.out.println();
        Tampilan.cetakJudul("PROGRAM SELESAI - TERIMA KASIH");
    }

    // ==================== LOGIN & DAFTAR ====================

    // Meminta username dan password (maksimal 3 kali coba). Hasil null jika gagal / dibatalkan.
    private static Akun login(Autentikasi autentikasi, String peran) {
        Tampilan.cetakSubJudul("Login " + peran);
        for (int percobaan = 1; percobaan <= BATAS_PERCOBAAN_LOGIN; percobaan++) {
            String username = Input.teks("Username (kosong=batal)");
            if (username.isEmpty()) {
                return null;
            }
            String password = Input.teks("Password");
            Akun akun = autentikasi.login(username, password, peran);
            if (akun != null) {
                return akun;
            }
            if (percobaan < BATAS_PERCOBAAN_LOGIN) {
                Tampilan.info("Sisa percobaan: " + (BATAS_PERCOBAAN_LOGIN - percobaan));
            }
        }
        Tampilan.gagal("Terlalu banyak percobaan. Kembali ke menu awal.");
        return null;
    }

    // Pendaftaran pembeli baru: setiap isian diperiksa satu per satu
    private static void daftar(Autentikasi autentikasi) {
        Tampilan.cetakSubJudul("Daftar Akun Pembeli Baru (kosongkan username untuk batal)");

        String username;
        while (true) {
            username = Input.teks("Username");
            if (username.isEmpty()) {
                Tampilan.info("Pendaftaran dibatalkan.");
                return;
            }
            String masalah = autentikasi.pesanUsernameTidakValid(username);
            if (masalah == null) {
                break;
            }
            Tampilan.gagal(masalah);
        }

        String password;
        while (true) {
            password = Input.teks("Password (min " + Akun.PASSWORD_MIN + " karakter)");
            String masalah = Akun.pesanPasswordTidakValid(password);
            if (masalah != null) {
                Tampilan.gagal(masalah);
            } else if (!Input.teks("Ulangi password").equals(password)) {
                Tampilan.gagal("Pengulangan password tidak sama.");
            } else {
                break;
            }
        }

        String nama = Input.teksWajib("Nama lengkap");

        String nim;
        while (true) {
            nim = Input.teks("NIM");
            String masalah = autentikasi.pesanNimTidakValid(nim);
            if (masalah == null) {
                break;
            }
            Tampilan.gagal(masalah);
        }

        String jurusan = Input.teks("Jurusan (boleh dikosongkan)");

        AkunPembeli akun = autentikasi.daftarPembeli(username, password, nim, nama, jurusan);
        if (akun != null) {
            Tampilan.sukses("Akun \"" + akun.getUsername() + "\" berhasil dibuat. Silakan login.");
        }
    }

    private static void tampilkanAkunDemo() {
        Tampilan.cetakSubJudul("Akun Demo");
        System.out.println("  Penjual : username penjual | password kantin123");
        System.out.println("  Pembeli : username rani    | password rani123   (saldo Rp 50.000)");
        System.out.println("  Pembeli : username budi    | password budi123   (saldo Rp 30.000)");
        System.out.println("  Kamu juga bisa membuat akun pembeli sendiri lewat menu Daftar.");
    }

    // ==================== DATA AWAL ====================

    private static Kantin siapkanKantin() {
        Kantin kantin = new Kantin("Kantin Kampus Sejahtera");

        kantin.tambahProduk(new Produk("P001", "Nasi Goreng", "Makanan", 15000, 10));
        kantin.tambahProduk(new Produk("P002", "Mie Ayam", "Makanan", 12000, 8));
        kantin.tambahProduk(new Produk("P003", "Ayam Geprek", "Makanan", 18000, 6));
        kantin.tambahProduk(new Produk("P004", "Soto Ayam", "Makanan", 14000, 7));
        kantin.tambahProduk(new Produk("P005", "Es Teh Manis", "Minuman", 4000, 20));
        kantin.tambahProduk(new Produk("P006", "Es Jeruk", "Minuman", 5000, 15));
        kantin.tambahProduk(new Produk("P007", "Kopi Hitam", "Minuman", 6000, 12));
        kantin.tambahProduk(new Produk("P008", "Roti Bakar", "Camilan", 10000)); // constructor 4 parameter: stok 0

        kantin.tambahPromo(new Promo("MAHASISWA5", "Diskon khusus mahasiswa", 5, 0));
        kantin.tambahPromo(new Promo("HEMAT10", "Diskon belanja hemat", 10, 30000));
        kantin.tambahPromo(new Promo("SERBA15", "Diskon belanja besar", 15, 60000));
        return kantin;
    }

    private static Autentikasi siapkanAkun() {
        Autentikasi autentikasi = new Autentikasi();
        autentikasi.tambahPenjual("penjual", "kantin123", "Pengelola Kantin");

        AkunPembeli rani = autentikasi.daftarPembeli("rani", "rani123", "2301001", "Rani Putri", "Informatika");
        rani.getProfil().topUp(50000);
        AkunPembeli budi = autentikasi.daftarPembeli("budi", "budi123", "2301002", "Budi Santoso",
                "Sistem Informasi");
        budi.getProfil().topUp(30000);
        return autentikasi;
    }
}
