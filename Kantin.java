package service;

import java.util.ArrayList;

import model.Produk;
import util.Tampilan;

/**
 * Class Kantin
 * ------------
 * Fungsi : pusat pengelolaan kantin. Menyimpan daftar menu (Produk), mencari produk,
 *          melakukan restok, mencatat riwayat Transaksi, dan membuat laporan kantin.
 */
public class Kantin {

    // Format kolom tabel riwayat transaksi
    private static final String FORMAT_RIWAYAT_HEADER = " %-7s | %-15s | %4s | %11s | %s%n";
    private static final String FORMAT_RIWAYAT_BARIS = " %-7s | %-15s | %4d | %11s | %s%n";

    // [PRIVATE] Atribut private
    private String namaKantin;
    private ArrayList<Produk> daftarProduk;
    private ArrayList<Transaksi> riwayatTransaksi;
    private int totalPendapatan;

    // [CONSTRUCTOR] Membuat kantin baru dengan menu dan riwayat yang masih kosong
    public Kantin(String namaKantin) {
        this.namaKantin = namaKantin;
        this.daftarProduk = new ArrayList<>();
        this.riwayatTransaksi = new ArrayList<>();
        this.totalPendapatan = 0;
    }

    // ==================== GETTER ====================

    public String getNamaKantin() {
        return namaKantin;
    }

    public int getJumlahMenu() {
        return daftarProduk.size();
    }

    public int getTotalPendapatan() {
        return totalPendapatan;
    }

    // ==================== PENGELOLAAN MENU ====================

    // Menambah produk ke menu. Kode produk tidak boleh sama dengan yang sudah ada.
    public boolean tambahProduk(Produk produk) {
        if (produk == null) {
            return false;
        }
        if (cariProduk(produk.getKode()) != null) {
            Tampilan.gagal("Kode produk " + produk.getKode() + " sudah terdaftar.");
            return false;
        }
        daftarProduk.add(produk);
        return true;
    }

    // [OVERLOADING] cariProduk(String kode) dan cariProduk(int nomorMenu): nama sama, tipe parameter berbeda.

    // Mencari produk berdasarkan kode, contoh: cariProduk("P001"). Hasil null jika tidak ada.
    public Produk cariProduk(String kode) {
        for (Produk produk : daftarProduk) {
            if (produk.getKode().equalsIgnoreCase(kode)) {
                return produk;
            }
        }
        return null;
    }

    // Mencari produk berdasarkan nomor urut di tabel menu (mulai dari 1), contoh: cariProduk(5)
    public Produk cariProduk(int nomorMenu) {
        if (nomorMenu >= 1 && nomorMenu <= daftarProduk.size()) {
            return daftarProduk.get(nomorMenu - 1);
        }
        return null;
    }

    // Menambah stok produk yang sudah terdaftar
    public boolean restok(String kode, int jumlah) {
        Produk produk = cariProduk(kode);
        if (produk == null) {
            Tampilan.gagal("Produk dengan kode " + kode + " tidak ditemukan.");
            return false;
        }
        if (!produk.tambahStok(jumlah)) {
            Tampilan.gagal("Jumlah restok " + produk.getNamaProduk() + " harus lebih dari 0.");
            return false;
        }
        Tampilan.sukses("Restok " + produk.getNamaProduk() + " +" + jumlah + " -> stok sekarang "
                + produk.getStok());
        return true;
    }

    // ==================== PENCATATAN TRANSAKSI ====================

    // Menyimpan transaksi yang sudah selesai (lunas / dibatalkan) ke riwayat kantin.
    // Pendapatan hanya bertambah untuk transaksi yang lunas.
    public boolean catatTransaksi(Transaksi transaksi) {
        if (transaksi == null) {
            return false;
        }
        if (transaksi.getStatus().equals(Transaksi.STATUS_PROSES)) {
            Tampilan.gagal("Transaksi " + transaksi.getIdTransaksi() + " belum selesai, belum bisa dicatat.");
            return false;
        }
        if (riwayatTransaksi.contains(transaksi)) {
            Tampilan.gagal("Transaksi " + transaksi.getIdTransaksi() + " sudah pernah dicatat.");
            return false;
        }

        riwayatTransaksi.add(transaksi);
        if (transaksi.isLunas()) {
            totalPendapatan += transaksi.getTotalAkhir();
        }
        Tampilan.info("Transaksi " + transaksi.getIdTransaksi() + " dicatat ke riwayat kantin ("
                + transaksi.getStatus() + ")");
        return true;
    }

    // ==================== TAMPILAN ====================

    // Menampilkan seluruh menu dalam bentuk tabel
    public void tampilkanMenu() {
        Tampilan.cetakSubJudul("Daftar Menu - " + namaKantin);
        if (daftarProduk.isEmpty()) {
            System.out.println("  Menu masih kosong.");
            return;
        }

        Produk.cetakHeaderTabel();
        int nomor = 1;
        for (Produk produk : daftarProduk) {
            produk.tampilkanInfo(nomor);
            nomor++;
        }
        Tampilan.cetakGaris();
        System.out.println(" Total " + daftarProduk.size() + " menu");
    }

    // Menampilkan riwayat transaksi dalam bentuk tabel
    public void tampilkanRiwayat() {
        Tampilan.cetakSubJudul("Riwayat Transaksi");
        if (riwayatTransaksi.isEmpty()) {
            System.out.println("  Belum ada transaksi.");
            return;
        }

        System.out.printf(FORMAT_RIWAYAT_HEADER, "ID", "Pembeli", "Item", "Total Bayar", "Status");
        System.out.println("---------+-----------------+------+-------------+-----------");
        for (Transaksi transaksi : riwayatTransaksi) {
            // Transaksi yang dibatalkan tidak ada uang yang dibayar, jadi totalnya ditulis "-"
            String totalBayar = transaksi.isLunas() ? Tampilan.rupiah(transaksi.getTotalAkhir()) : "-";
            System.out.printf(FORMAT_RIWAYAT_BARIS, transaksi.getIdTransaksi(),
                    Tampilan.potong(transaksi.getPembeli().getNama(), 15), transaksi.getJumlahItem(),
                    totalBayar, transaksi.getStatus());
        }
    }

    // Menampilkan laporan ringkas: jumlah transaksi, pendapatan, produk terlaris, dan stok menipis
    public void tampilkanLaporan() {
        int jumlahLunas = 0;
        int jumlahBatal = 0;
        for (Transaksi transaksi : riwayatTransaksi) {
            if (transaksi.isLunas()) {
                jumlahLunas++;
            } else if (transaksi.getStatus().equals(Transaksi.STATUS_BATAL)) {
                jumlahBatal++;
            }
        }

        Tampilan.cetakSubJudul("Laporan Kantin");
        cetakBaris("Nama kantin", namaKantin);
        cetakBaris("Jumlah menu", daftarProduk.size() + " produk");
        cetakBaris("Transaksi tercatat", String.valueOf(riwayatTransaksi.size()));
        cetakBaris("  - Lunas", String.valueOf(jumlahLunas));
        cetakBaris("  - Dibatalkan", String.valueOf(jumlahBatal));
        cetakBaris("Total pendapatan", Tampilan.rupiah(totalPendapatan));

        Produk terlaris = cariProdukTerlaris();
        if (terlaris == null) {
            cetakBaris("Produk terlaris", "-");
        } else {
            cetakBaris("Produk terlaris", terlaris.getNamaProduk() + " (" + terlaris.getTerjual() + " terjual)");
        }
        cetakBaris("Perlu restok (stok <= " + Produk.BATAS_STOK_MENIPIS + ")", daftarStokMenipis());
    }

    // ==================== METHOD PRIVATE (BANTUAN INTERNAL) ====================

    // [PRIVATE] Mencetak satu baris laporan dengan format "label : nilai"
    private void cetakBaris(String label, String nilai) {
        System.out.printf(" %-25s: %s%n", label, nilai);
    }

    // [PRIVATE] Mencari produk dengan jumlah terjual terbanyak. Hasil null jika belum ada yang terjual.
    private Produk cariProdukTerlaris() {
        Produk terlaris = null;
        for (Produk produk : daftarProduk) {
            if (produk.getTerjual() > 0 && (terlaris == null || produk.getTerjual() > terlaris.getTerjual())) {
                terlaris = produk;
            }
        }
        return terlaris;
    }

    // [PRIVATE] Menyusun teks daftar produk yang stoknya menipis, contoh: "Mie Ayam (5), Soto Ayam (3)"
    private String daftarStokMenipis() {
        String hasil = "";
        for (Produk produk : daftarProduk) {
            if (produk.isStokMenipis()) {
                if (!hasil.isEmpty()) {
                    hasil += ", ";
                }
                hasil += produk.getNamaProduk() + " (" + produk.getStok() + ")";
            }
        }
        return hasil.isEmpty() ? "tidak ada" : hasil;
    }
}
