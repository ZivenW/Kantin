package model;

import util.Tampilan;

/**
 * Class Mahasiswa
 * ---------------
 * Fungsi : menyimpan identitas mahasiswa yang berbelanja di kantin (NIM, nama, jurusan)
 *          serta mencatat riwayat belanjanya (jumlah transaksi dan total belanja).
 */
public class Mahasiswa {

    // ==================== ATRIBUT STATIC ====================

    // [STATIC] Menghitung berapa object Mahasiswa yang pernah dibuat
    private static int jumlahMahasiswa = 0;

    // [STATIC] Konstanta: batas atas saldo kantin dan minimal sekali top up
    public static final int SALDO_MAKS = 5000000;
    public static final int TOPUP_MIN = 5000;

    // Format kolom tabel mahasiswa
    private static final String FORMAT_HEADER = " %-7s | %-14s | %-16s | %9s | %13s | %11s%n";
    private static final String FORMAT_BARIS = " %-7s | %-14s | %-16s | %9d | %13s | %11s%n";

    // ==================== ATRIBUT OBJECT ====================

    // [PRIVATE] Semua atribut private, diakses lewat getter
    private String nim;
    private String nama;
    private String jurusan;
    private int jumlahTransaksi;
    private int totalBelanja;
    private int saldo; // saldo kantin (e-wallet) yang bisa dipakai untuk membayar

    // ==================== CONSTRUCTOR ====================

    // [CONSTRUCTOR] Mengisi identitas lengkap mahasiswa
    public Mahasiswa(String nim, String nama, String jurusan) {
        this.nim = nim;
        this.nama = nama;
        this.jurusan = jurusan;
        this.jumlahTransaksi = 0;
        this.totalBelanja = 0;
        this.saldo = 0;
        jumlahMahasiswa++;
    }

    // [OVERLOADING - CONSTRUCTOR] Constructor tanpa jurusan -> jurusan diisi teks bawaan
    public Mahasiswa(String nim, String nama) {
        this(nim, nama, "Belum diisi");
    }

    // ==================== GETTER ====================

    public String getNim() {
        return nim;
    }

    public String getNama() {
        return nama;
    }

    public String getJurusan() {
        return jurusan;
    }

    public int getJumlahTransaksi() {
        return jumlahTransaksi;
    }

    public int getTotalBelanja() {
        return totalBelanja;
    }

    public int getSaldo() {
        return saldo;
    }

    // Mengubah nama (tidak boleh kosong). Mengembalikan false jika tidak valid.
    public boolean setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            return false;
        }
        this.nama = nama.trim();
        return true;
    }

    // Mengubah jurusan (tidak boleh kosong). Mengembalikan false jika tidak valid.
    public boolean setJurusan(String jurusan) {
        if (jurusan == null || jurusan.trim().isEmpty()) {
            return false;
        }
        this.jurusan = jurusan.trim();
        return true;
    }

    // [STATIC METHOD] Dipanggil lewat nama class: Mahasiswa.getJumlahMahasiswa()
    public static int getJumlahMahasiswa() {
        return jumlahMahasiswa;
    }

    // ==================== LOGIKA ====================

    // Menambah saldo (top up). Gagal jika nominal <= 0 atau saldo akan melebihi SALDO_MAKS.
    public boolean topUp(int nominal) {
        if (nominal <= 0 || (long) saldo + nominal > SALDO_MAKS) {
            return false;
        }
        saldo += nominal;
        return true;
    }

    // Mengurangi saldo saat membayar. Gagal jika nominal <= 0 atau saldo tidak cukup.
    public boolean kurangiSaldo(int nominal) {
        if (nominal <= 0 || nominal > saldo) {
            return false;
        }
        saldo -= nominal;
        return true;
    }

    // Dipanggil oleh Transaksi setelah pembayaran berhasil untuk mencatat belanja mahasiswa
    public void catatPembelian(int nominal) {
        if (nominal > 0) {
            jumlahTransaksi++;
            totalBelanja += nominal;
        }
    }

    // ==================== TAMPILAN ====================

    // [STATIC METHOD] Mencetak judul kolom tabel mahasiswa
    public static void cetakHeaderTabel() {
        System.out.printf(FORMAT_HEADER, "NIM", "Nama", "Jurusan", "Transaksi", "Total Belanja", "Saldo");
        System.out.println("---------+----------------+------------------+-----------+---------------+------------");
    }

    // Mencetak satu baris data mahasiswa
    public void tampilkanInfo() {
        System.out.printf(FORMAT_BARIS, nim, Tampilan.potong(nama, 14), Tampilan.potong(jurusan, 16),
                jumlahTransaksi, Tampilan.rupiah(totalBelanja), Tampilan.rupiah(saldo));
    }
}
