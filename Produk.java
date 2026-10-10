package model;

import util.Tampilan;

/**
 * Class Produk
 * ------------
 * Fungsi : menyimpan data makanan/minuman yang dijual di kantin
 *          (kode, nama, kategori, harga, stok) dan mengatur perubahan stoknya.
 */
public class Produk {

    // ==================== ATRIBUT STATIC ====================

    // [STATIC] Milik class (bukan milik satu object): menghitung berapa object Produk yang pernah dibuat
    private static int jumlahProduk = 0;

    // [STATIC] Konstanta: stok dianggap "menipis" jika jumlahnya <= nilai ini
    public static final int BATAS_STOK_MENIPIS = 5;

    // Format kolom tabel menu (dipakai bersama oleh header dan baris tabel)
    private static final String FORMAT_HEADER = " %2s | %-4s | %-24s | %-9s | %10s | %4s%n";
    private static final String FORMAT_BARIS = " %2d | %-4s | %-24s | %-9s | %10s | %4d%n";

    // ==================== ATRIBUT OBJECT ====================

    // [PRIVATE] Atribut dibuat private supaya tidak bisa diubah sembarangan dari class lain.
    // Akses dilakukan lewat method public (getter / setter) -> konsep enkapsulasi.
    private String kode;
    private String namaProduk;
    private String kategori;
    private int harga;
    private int stok;
    private int terjual;

    // ==================== CONSTRUCTOR ====================

    // [CONSTRUCTOR] Dipanggil saat object dibuat dengan "new Produk(...)" untuk mengisi nilai awal
    public Produk(String kode, String namaProduk, String kategori, int harga, int stok) {
        this.kode = kode;
        this.namaProduk = namaProduk;
        this.kategori = kategori;
        this.harga = Math.max(harga, 0); // harga tidak boleh negatif
        this.stok = Math.max(stok, 0);   // stok tidak boleh negatif
        this.terjual = 0;
        jumlahProduk++;                  // setiap object baru menambah penghitung static
    }

    // [OVERLOADING - CONSTRUCTOR] Constructor kedua tanpa parameter stok -> stok awal otomatis 0
    public Produk(String kode, String namaProduk, String kategori, int harga) {
        this(kode, namaProduk, kategori, harga, 0);
    }

    // ==================== GETTER & SETTER ====================

    public String getKode() {
        return kode;
    }

    public String getNamaProduk() {
        return namaProduk;
    }

    public String getKategori() {
        return kategori;
    }

    public int getHarga() {
        return harga;
    }

    public int getStok() {
        return stok;
    }

    public int getTerjual() {
        return terjual;
    }

    // Setter dengan validasi: harga baru harus lebih dari 0
    public void setHarga(int harga) {
        if (harga > 0) {
            this.harga = harga;
        }
    }

    // [STATIC METHOD] Dipanggil lewat nama class: Produk.getJumlahProduk()
    public static int getJumlahProduk() {
        return jumlahProduk;
    }

    // ==================== LOGIKA STOK ====================

    // Cek apakah stok cukup untuk jumlah yang diminta
    public boolean isTersedia(int jumlah) {
        return jumlah > 0 && stok >= jumlah;
    }

    // Mengurangi stok saat produk terjual. Mengembalikan false jika stok tidak cukup.
    public boolean kurangiStok(int jumlah) {
        if (!isTersedia(jumlah)) {
            return false;
        }
        stok -= jumlah;
        terjual += jumlah;
        return true;
    }

    // Menambah stok (restok). Mengembalikan false jika jumlah tidak valid.
    public boolean tambahStok(int jumlah) {
        if (jumlah <= 0) {
            return false;
        }
        stok += jumlah;
        return true;
    }

    // Stok dianggap menipis jika sisa <= BATAS_STOK_MENIPIS
    public boolean isStokMenipis() {
        return stok <= BATAS_STOK_MENIPIS;
    }

    // ==================== TAMPILAN ====================

    // [OVERLOADING] tampilkanInfo() menampilkan detail satu produk,
    //               tampilkanInfo(int nomor) menampilkan satu baris untuk tabel menu.

    public void tampilkanInfo() {
        System.out.println("  Kode      : " + kode);
        System.out.println("  Nama      : " + namaProduk);
        System.out.println("  Kategori  : " + kategori);
        System.out.println("  Harga     : " + Tampilan.rupiah(harga));
        System.out.println("  Stok      : " + stok);
        System.out.println("  Terjual   : " + terjual);
    }

    public void tampilkanInfo(int nomor) {
        System.out.printf(FORMAT_BARIS, nomor, kode, Tampilan.potong(namaProduk, 24),
                Tampilan.potong(kategori, 9), Tampilan.rupiah(harga), stok);
    }

    // [STATIC METHOD] Mencetak judul kolom tabel menu (tidak butuh object Produk tertentu)
    public static void cetakHeaderTabel() {
        System.out.printf(FORMAT_HEADER, "No", "Kode", "Nama Produk", "Kategori", "Harga", "Stok");
        System.out.println("----+------+--------------------------+-----------+------------+-----");
    }
}
