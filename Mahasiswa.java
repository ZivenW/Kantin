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

    // Format kolom tabel mahasiswa
    private static final String FORMAT_HEADER = " %-7s | %-14s | %-16s | %9s | %13s%n";
    private static final String FORMAT_BARIS = " %-7s | %-14s | %-16s | %9d | %13s%n";

    // ==================== ATRIBUT OBJECT ====================

    // [PRIVATE] Semua atribut private, diakses lewat getter
    private String nim;
    private String nama;
    private String jurusan;
    private int jumlahTransaksi;
    private int totalBelanja;

    // ==================== CONSTRUCTOR ====================

    // [CONSTRUCTOR] Mengisi identitas lengkap mahasiswa
    public Mahasiswa(String nim, String nama, String jurusan) {
        this.nim = nim;
        this.nama = nama;
        this.jurusan = jurusan;
        this.jumlahTransaksi = 0;
        this.totalBelanja = 0;
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

    // [STATIC METHOD] Dipanggil lewat nama class: Mahasiswa.getJumlahMahasiswa()
    public static int getJumlahMahasiswa() {
        return jumlahMahasiswa;
    }

    // ==================== LOGIKA ====================

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
        System.out.printf(FORMAT_HEADER, "NIM", "Nama", "Jurusan", "Transaksi", "Total Belanja");
        System.out.println("---------+----------------+------------------+-----------+--------------");
    }

    // Mencetak satu baris data mahasiswa
    public void tampilkanInfo() {
        System.out.printf(FORMAT_BARIS, nim, Tampilan.potong(nama, 14), Tampilan.potong(jurusan, 16),
                jumlahTransaksi, Tampilan.rupiah(totalBelanja));
    }
}
