package main;

import model.Mahasiswa;
import model.Produk;
import service.Kantin;
import service.Transaksi;
import util.Tampilan;

/**
 * Class Main
 * ----------
 * Fungsi : class utama untuk menjalankan aplikasi Sistem Pengelolaan Kantin Kampus.
 *          Main membuat object-object (Kantin, Produk, Mahasiswa, Transaksi),
 *          lalu menjalankan alur program secara berurutan dalam 5 tahap.
 */
public class Main {

    // CATATAN: isi dengan nama dan NIM kamu, lalu jalankan ulang sebelum mengambil screenshot
    private static final String NAMA_PEMBUAT = "[Nama Kamu]";
    private static final String NIM_PEMBUAT = "[NIM Kamu]";

    public static void main(String[] args) {

        // ============================================================
        // JUDUL APLIKASI
        // ============================================================
        Tampilan.cetakJudul("SISTEM PENGELOLAAN KANTIN KAMPUS", "Project Object Oriented Programming - Java");
        System.out.println(" Disusun oleh : " + NAMA_PEMBUAT + " (" + NIM_PEMBUAT + ")");

        // ============================================================
        // TAHAP 1 : membuat object Kantin, Produk, dan Mahasiswa
        // ============================================================
        Tampilan.cetakBagian("TAHAP 1 : PERSIAPAN DATA MENU DAN MAHASISWA");

        // Membuat object Kantin
        Kantin kantin = new Kantin("Kantin Kampus Sejahtera");

        // Membuat object Produk dengan constructor 5 parameter (lengkap dengan stok awal)
        kantin.tambahProduk(new Produk("P001", "Nasi Goreng", "Makanan", 15000, 10));
        kantin.tambahProduk(new Produk("P002", "Mie Ayam", "Makanan", 12000, 8));
        kantin.tambahProduk(new Produk("P003", "Ayam Geprek", "Makanan", 18000, 6));
        kantin.tambahProduk(new Produk("P004", "Soto Ayam", "Makanan", 14000, 7));
        kantin.tambahProduk(new Produk("P005", "Es Teh Manis", "Minuman", 4000, 20));
        kantin.tambahProduk(new Produk("P006", "Es Jeruk", "Minuman", 5000, 15));
        kantin.tambahProduk(new Produk("P007", "Kopi Hitam", "Minuman", 6000, 12));

        // Constructor 4 parameter: menu baru yang stok awalnya otomatis 0
        kantin.tambahProduk(new Produk("P008", "Roti Bakar", "Camilan", 10000));

        Tampilan.sukses(kantin.getJumlahMenu() + " produk berhasil didaftarkan ke menu " + kantin.getNamaKantin());
        kantin.tampilkanMenu();

        // Membuat object Mahasiswa: constructor 3 parameter, lalu constructor 2 parameter (tanpa jurusan)
        Mahasiswa rani = new Mahasiswa("2301001", "Rani Putri", "Informatika");
        Mahasiswa budi = new Mahasiswa("2301002", "Budi Santoso", "Sistem Informasi");
        Mahasiswa citra = new Mahasiswa("2301003", "Citra Lestari", "Teknik Elektro");
        Mahasiswa dimas = new Mahasiswa("2301004", "Dimas Pratama");

        Tampilan.cetakSubJudul("Data Mahasiswa Pelanggan");
        Mahasiswa.cetakHeaderTabel();
        rani.tampilkanInfo();
        budi.tampilkanInfo();
        citra.tampilkanInfo();
        dimas.tampilkanInfo();

        // ============================================================
        // TAHAP 2 : transaksi pembelian normal
        // ============================================================
        Tampilan.cetakBagian("TAHAP 2 : PROSES TRANSAKSI PEMBELIAN");

        // ----- Transaksi 1 : tanpa diskon -----
        Tampilan.cetakSubJudul("Transaksi 1 - Rani makan siang (tanpa diskon)");
        Transaksi trx1 = new Transaksi(rani);
        Tampilan.info("Transaksi " + trx1.getIdTransaksi() + " dibuat untuk " + rani.getNama());

        trx1.tambahItem(kantin.cariProduk("P001"), 2); // produk dicari berdasarkan KODE
        trx1.tambahItem(kantin.cariProduk(5), 2);      // produk dicari berdasarkan NOMOR MENU
        trx1.tambahItem(kantin.cariProduk("P007"));    // jumlah tidak ditulis -> otomatis 1
        Tampilan.info("Total belanja : " + Tampilan.rupiah(trx1.hitungTotal()));

        trx1.bayar(50000);                             // bayar tanpa diskon
        trx1.cetakStruk();
        kantin.catatTransaksi(trx1);

        // ----- Transaksi 2 : memakai diskon 10% -----
        Tampilan.cetakSubJudul("Transaksi 2 - Budi memakai promo diskon 10%");
        Transaksi trx2 = new Transaksi(budi);
        Tampilan.info("Transaksi " + trx2.getIdTransaksi() + " dibuat untuk " + budi.getNama());

        trx2.tambahItem(kantin.cariProduk("P003"), 2);
        trx2.tambahItem(kantin.cariProduk("P006"), 2);
        Tampilan.info("Total sebelum diskon : " + Tampilan.rupiah(trx2.hitungTotal()));
        Tampilan.info("Total setelah diskon : " + Tampilan.rupiah(trx2.hitungTotal(10)));

        trx2.bayar(50000, 10);                         // bayar dengan diskon 10%
        trx2.cetakStruk();
        kantin.catatTransaksi(trx2);

        // ============================================================
        // TAHAP 3 : transaksi yang bermasalah (stok, uang kurang, batal)
        // ============================================================
        Tampilan.cetakBagian("TAHAP 3 : TRANSAKSI DENGAN KENDALA (STOK / UANG / BATAL)");

        // ----- Transaksi 3 : stok habis, uang kurang, lalu berhasil -----
        Tampilan.cetakSubJudul("Transaksi 3 - Citra (stok habis dan uang kurang)");
        Transaksi trx3 = new Transaksi(citra);
        Tampilan.info("Transaksi " + trx3.getIdTransaksi() + " dibuat untuk " + citra.getNama());

        trx3.tambahItem(kantin.cariProduk("P008"), 1); // Roti Bakar stok 0 -> ditolak
        trx3.tambahItem(kantin.cariProduk("P002"), 3); // Mie Ayam diterima
        trx3.bayar(30000);                             // uang kurang -> pembayaran gagal
        trx3.bayar(40000);                             // dicoba lagi dengan uang cukup -> berhasil
        kantin.catatTransaksi(trx3);

        // ----- Transaksi 4 : dibatalkan -----
        Tampilan.cetakSubJudul("Transaksi 4 - Dimas membatalkan pesanan");
        Transaksi trx4 = new Transaksi(dimas);
        Tampilan.info("Transaksi " + trx4.getIdTransaksi() + " dibuat untuk " + dimas.getNama());

        trx4.tambahItem(kantin.cariProduk("P004"));    // Soto Ayam x1
        trx4.tambahItem(kantin.cariProduk("P005"));    // Es Teh Manis x1
        trx4.batalkan();                               // pesanan dibatalkan
        trx4.bayar(20000);                             // sudah dibatalkan -> tidak bisa dibayar
        kantin.catatTransaksi(trx4);

        // ============================================================
        // TAHAP 4 : pengelolaan menu (restok dan ubah harga)
        // ============================================================
        Tampilan.cetakBagian("TAHAP 4 : PENGELOLAAN MENU (RESTOK DAN UBAH HARGA)");

        Tampilan.cetakSubJudul("Restok Roti Bakar");
        kantin.restok("P008", 10);
        Tampilan.info("Detail produk setelah restok:");
        kantin.cariProduk("P008").tampilkanInfo();     // tampilkanInfo() tanpa parameter -> detail produk

        Tampilan.cetakSubJudul("Ubah harga Es Teh Manis");
        Produk esTeh = kantin.cariProduk("P005");
        Tampilan.info("Harga lama : " + Tampilan.rupiah(esTeh.getHarga()));
        esTeh.setHarga(5000);
        Tampilan.info("Harga baru : " + Tampilan.rupiah(esTeh.getHarga()));

        // ============================================================
        // TAHAP 5 : hasil akhir dan laporan
        // ============================================================
        Tampilan.cetakBagian("TAHAP 5 : HASIL AKHIR DAN LAPORAN KANTIN");

        kantin.tampilkanMenu();                        // stok dan harga setelah semua proses
        kantin.tampilkanRiwayat();

        Tampilan.cetakSubJudul("Rekap Belanja Mahasiswa");
        Mahasiswa.cetakHeaderTabel();
        rani.tampilkanInfo();
        budi.tampilkanInfo();
        citra.tampilkanInfo();
        dimas.tampilkanInfo();

        kantin.tampilkanLaporan();

        // Variabel static dibaca lewat nama class, tanpa perlu object
        Tampilan.cetakSubJudul("Statistik Object (variabel static)");
        System.out.printf(" %-25s: %d%n", "Object Produk dibuat", Produk.getJumlahProduk());
        System.out.printf(" %-25s: %d%n", "Object Mahasiswa dibuat", Mahasiswa.getJumlahMahasiswa());
        System.out.printf(" %-25s: %d%n", "Object Transaksi dibuat", Transaksi.getJumlahTransaksi());

        System.out.println();
        Tampilan.cetakJudul("PROGRAM SELESAI - TERIMA KASIH");
    }
}
