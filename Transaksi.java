package service;

import java.util.ArrayList;

import model.ItemTransaksi;
import model.Mahasiswa;
import model.Produk;
import util.Tampilan;

/**
 * Class Transaksi
 * ---------------
 * Fungsi : mengelola satu proses pembelian di kantin:
 *          menampung item pesanan, menghitung total, memproses pembayaran
 *          (diskon, uang kurang, kembalian), mengurangi stok produk, dan mencetak struk.
 */
public class Transaksi {

    // ==================== ATRIBUT STATIC ====================

    // [STATIC] Penghitung transaksi yang pernah dibuat, sekaligus dipakai untuk membuat ID otomatis
    private static int jumlahTransaksi = 0;

    // [STATIC] Konstanta status transaksi (nilainya tetap, dipakai bersama)
    public static final String STATUS_PROSES = "DALAM PROSES";
    public static final String STATUS_LUNAS = "LUNAS";
    public static final String STATUS_BATAL = "DIBATALKAN";

    // Format kolom struk
    private static final String FORMAT_HEADER_ITEM = " %-28s %5s %16s %18s%n";
    private static final String FORMAT_BARIS_ITEM = " %-28s %5d %16s %18s%n";
    private static final String FORMAT_RINGKASAN = " %-51s %18s%n";

    // ==================== ATRIBUT OBJECT ====================

    // [PRIVATE] Semua atribut private
    private String idTransaksi;
    private Mahasiswa pembeli;
    private ArrayList<ItemTransaksi> daftarItem;
    private String status;
    private double diskonPersen; // persen diskon yang dipakai saat pembayaran
    private int diskon;          // potongan harga dalam rupiah
    private int totalAkhir;      // total yang dibayar setelah diskon
    private int uangBayar;
    private int kembalian;

    // ==================== CONSTRUCTOR ====================

    // [CONSTRUCTOR] Membuat transaksi baru untuk seorang mahasiswa. ID dibuat otomatis (TRX-001, TRX-002, ...)
    public Transaksi(Mahasiswa pembeli) {
        jumlahTransaksi++;
        this.idTransaksi = String.format("TRX-%03d", jumlahTransaksi);
        this.pembeli = pembeli;
        this.daftarItem = new ArrayList<>();
        this.status = STATUS_PROSES;
        this.diskonPersen = 0;
        this.diskon = 0;
        this.totalAkhir = 0;
        this.uangBayar = 0;
        this.kembalian = 0;
    }

    // ==================== GETTER ====================

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public Mahasiswa getPembeli() {
        return pembeli;
    }

    public String getStatus() {
        return status;
    }

    public int getTotalAkhir() {
        return totalAkhir;
    }

    public boolean isLunas() {
        return status.equals(STATUS_LUNAS);
    }

    // Jumlah seluruh barang (total qty) dalam transaksi ini
    public int getJumlahItem() {
        int total = 0;
        for (ItemTransaksi item : daftarItem) {
            total += item.getJumlah();
        }
        return total;
    }

    // [STATIC METHOD] Dipanggil lewat nama class: Transaksi.getJumlahTransaksi()
    public static int getJumlahTransaksi() {
        return jumlahTransaksi;
    }

    // ==================== MENAMBAH ITEM ====================

    // [OVERLOADING] tambahItem(produk, jumlah) dan tambahItem(produk): nama sama, parameter berbeda.

    // Menambah produk dengan jumlah tertentu. Mengembalikan true jika berhasil.
    public boolean tambahItem(Produk produk, int jumlah) {
        if (!status.equals(STATUS_PROSES)) {
            Tampilan.gagal("Transaksi " + idTransaksi + " sudah " + status + ", tidak bisa menambah item.");
            return false;
        }
        if (produk == null) {
            Tampilan.gagal("Produk tidak ditemukan.");
            return false;
        }
        if (jumlah <= 0) {
            Tampilan.gagal("Jumlah pembelian " + produk.getNamaProduk() + " harus lebih dari 0.");
            return false;
        }

        // Jika produk yang sama sudah ada di pesanan, jumlahnya digabung
        ItemTransaksi itemLama = cariItem(produk);
        int sudahDipesan = (itemLama == null) ? 0 : itemLama.getJumlah();
        int totalDiminta = sudahDipesan + jumlah;

        if (!produk.isTersedia(totalDiminta)) {
            Tampilan.gagal("Stok " + produk.getNamaProduk() + " tidak cukup (diminta " + totalDiminta
                    + ", tersedia " + produk.getStok() + ")");
            return false;
        }

        if (itemLama == null) {
            daftarItem.add(new ItemTransaksi(produk, jumlah));
        } else {
            itemLama.tambahJumlah(jumlah);
        }
        Tampilan.sukses(jumlah + " x " + produk.getNamaProduk() + " ditambahkan ke pesanan ("
                + Tampilan.rupiah(produk.getHarga() * jumlah) + ")");
        return true;
    }

    // Menambah satu produk saja (jumlah otomatis 1)
    public boolean tambahItem(Produk produk) {
        return tambahItem(produk, 1);
    }

    // ==================== MENGHITUNG TOTAL ====================

    // [OVERLOADING] hitungTotal() tanpa diskon, hitungTotal(diskonPersen) dengan diskon.

    public int hitungTotal() {
        int total = 0;
        for (ItemTransaksi item : daftarItem) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int hitungTotal(double diskonPersen) {
        return hitungTotal() - hitungDiskon(diskonPersen);
    }

    // ==================== PEMBAYARAN ====================

    // [OVERLOADING] bayar(uang) tanpa diskon, bayar(uang, diskonPersen) dengan diskon.

    public boolean bayar(int uang) {
        return bayar(uang, 0);
    }

    public boolean bayar(int uang, double diskonPersen) {
        if (!status.equals(STATUS_PROSES)) {
            Tampilan.gagal("Transaksi " + idTransaksi + " sudah " + status + ", tidak bisa dibayar lagi.");
            return false;
        }
        if (daftarItem.isEmpty()) {
            Tampilan.gagal("Pesanan masih kosong, tidak ada yang dibayar.");
            return false;
        }

        int potongan = hitungDiskon(diskonPersen);
        int tagihan = hitungTotal() - potongan;

        // Validasi 1: uang harus cukup
        if (uang < tagihan) {
            Tampilan.gagal("Uang kurang " + Tampilan.rupiah(tagihan - uang) + " (tagihan "
                    + Tampilan.rupiah(tagihan) + ", dibayar " + Tampilan.rupiah(uang) + ")");
            return false;
        }

        // Validasi 2: pastikan semua stok masih cukup SEBELUM ada stok yang dikurangi
        for (ItemTransaksi item : daftarItem) {
            if (!item.getProduk().isTersedia(item.getJumlah())) {
                Tampilan.gagal("Stok " + item.getProduk().getNamaProduk() + " sudah tidak cukup.");
                return false;
            }
        }

        // Semua valid -> kurangi stok, simpan hasil pembayaran, catat belanja mahasiswa
        for (ItemTransaksi item : daftarItem) {
            item.getProduk().kurangiStok(item.getJumlah());
        }
        this.diskonPersen = diskonPersen;
        this.diskon = potongan;
        this.totalAkhir = tagihan;
        this.uangBayar = uang;
        this.kembalian = uang - tagihan;
        this.status = STATUS_LUNAS;
        pembeli.catatPembelian(tagihan);

        Tampilan.sukses("Pembayaran berhasil. Total " + Tampilan.rupiah(tagihan)
                + ", kembalian " + Tampilan.rupiah(kembalian));
        return true;
    }

    // Membatalkan transaksi yang belum lunas (stok tidak berubah)
    public void batalkan() {
        if (status.equals(STATUS_PROSES)) {
            status = STATUS_BATAL;
            Tampilan.info("Transaksi " + idTransaksi + " dibatalkan. Stok produk tidak berubah.");
        } else {
            Tampilan.gagal("Transaksi " + idTransaksi + " sudah " + status + ", tidak bisa dibatalkan.");
        }
    }

    // ==================== STRUK ====================

    // Mencetak struk pembelian (hanya untuk transaksi yang sudah lunas)
    public void cetakStruk() {
        if (!isLunas()) {
            Tampilan.gagal("Struk hanya bisa dicetak setelah transaksi lunas.");
            return;
        }

        System.out.println();
        Tampilan.cetakJudul("STRUK PEMBELIAN KANTIN KAMPUS");
        System.out.println(" No. Transaksi : " + idTransaksi);
        System.out.println(" Pembeli       : " + pembeli.getNama() + " (" + pembeli.getNim() + ")");
        System.out.println(" Status        : " + status);
        Tampilan.cetakGaris();
        System.out.printf(FORMAT_HEADER_ITEM, "Item", "Qty", "Harga", "Subtotal");
        Tampilan.cetakGaris();
        for (ItemTransaksi item : daftarItem) {
            System.out.printf(FORMAT_BARIS_ITEM, Tampilan.potong(item.getProduk().getNamaProduk(), 28),
                    item.getJumlah(), Tampilan.rupiah(item.getHargaSatuan()), Tampilan.rupiah(item.getSubtotal()));
        }
        Tampilan.cetakGaris();
        // Rincian diskon hanya ditampilkan jika transaksi ini memakai diskon
        if (diskon > 0) {
            System.out.printf(FORMAT_RINGKASAN, "Total belanja", Tampilan.rupiah(hitungTotal()));
            System.out.printf(FORMAT_RINGKASAN, "Diskon " + formatPersen(diskonPersen) + "%", Tampilan.rupiah(-diskon));
        }
        System.out.printf(FORMAT_RINGKASAN, "TOTAL BAYAR", Tampilan.rupiah(totalAkhir));
        System.out.printf(FORMAT_RINGKASAN, "Tunai", Tampilan.rupiah(uangBayar));
        System.out.printf(FORMAT_RINGKASAN, "Kembalian", Tampilan.rupiah(kembalian));
        Tampilan.cetakGaris('=');
        System.out.println(Tampilan.tengah("Terima kasih, selamat menikmati!", Tampilan.LEBAR));
        Tampilan.cetakGaris('=');
    }

    // ==================== METHOD PRIVATE (BANTUAN INTERNAL) ====================

    // [PRIVATE] Mencari item pesanan berdasarkan kode produk. Hanya dipakai di dalam class ini.
    private ItemTransaksi cariItem(Produk produk) {
        for (ItemTransaksi item : daftarItem) {
            if (item.getProduk().getKode().equals(produk.getKode())) {
                return item;
            }
        }
        return null;
    }

    // [PRIVATE] Menghitung potongan harga (rupiah) dari persen diskon. Persen di luar 0-100 diabaikan.
    private int hitungDiskon(double persen) {
        if (persen <= 0 || persen > 100) {
            return 0;
        }
        return (int) Math.round(hitungTotal() * persen / 100.0);
    }

    // [PRIVATE] Menampilkan persen tanpa ".0" di belakang. Contoh: 10.0 -> "10", 12.5 -> "12.5"
    private String formatPersen(double persen) {
        if (persen == (int) persen) {
            return String.valueOf((int) persen);
        }
        return String.valueOf(persen);
    }
}
