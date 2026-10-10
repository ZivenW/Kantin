package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.Mahasiswa;
import model.Produk;
import model.Promo;
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

    // Format kolom tabel riwayat milik satu pembeli
    private static final String FORMAT_RIWAYAT_PEMBELI_HEADER = " %-7s | %4s | %11s | %-6s | %s%n";
    private static final String FORMAT_RIWAYAT_PEMBELI_BARIS = " %-7s | %4d | %11s | %-6s | %s%n";

    // [PRIVATE] Atribut private
    private String namaKantin;
    private ArrayList<Produk> daftarProduk;
    private ArrayList<Transaksi> riwayatTransaksi;
    private ArrayList<Promo> daftarPromo;
    private int totalPendapatan;
    private int urutanKodeTerakhir; // nomor terbesar yang pernah dipakai kode produk (P001 -> 1)

    // [CONSTRUCTOR] Membuat kantin baru dengan menu dan riwayat yang masih kosong
    public Kantin(String namaKantin) {
        this.namaKantin = namaKantin;
        this.daftarProduk = new ArrayList<>();
        this.riwayatTransaksi = new ArrayList<>();
        this.daftarPromo = new ArrayList<>();
        this.totalPendapatan = 0;
        this.urutanKodeTerakhir = 0;
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
        catatUrutanKode(produk.getKode());
        return true;
    }

    // Menghapus produk dari menu berdasarkan kode. Riwayat transaksi lama tidak terpengaruh.
    public boolean hapusProduk(String kode) {
        Produk produk = cariProduk(kode);
        if (produk == null) {
            Tampilan.gagal("Produk dengan kode " + kode + " tidak ditemukan.");
            return false;
        }
        daftarProduk.remove(produk);
        Tampilan.sukses(produk.getNamaProduk() + " (" + produk.getKode() + ") dihapus dari menu");
        return true;
    }

    // Membuat kode produk baru yang belum pernah dipakai, contoh: "P009".
    // Kode produk yang sudah dihapus tidak dipakai ulang.
    public String buatKodeBaru() {
        return String.format("P%03d", urutanKodeTerakhir + 1);
    }

    // Cek apakah object produk ini masih ada di menu (belum dihapus)
    public boolean adaProduk(Produk produk) {
        return daftarProduk.contains(produk);
    }

    // Salinan seluruh daftar produk (aman dipakai untuk filter / urut tanpa mengubah menu asli)
    public ArrayList<Produk> getDaftarProduk() {
        return new ArrayList<>(daftarProduk);
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

    // ==================== PENCARIAN, FILTER, DAN URUTAN ====================

    // Mencari produk yang namanya mengandung kata kunci (tidak peduli huruf besar/kecil)
    public ArrayList<Produk> cariBerdasarkanNama(String kataKunci) {
        ArrayList<Produk> hasil = new ArrayList<>();
        for (Produk produk : daftarProduk) {
            if (produk.getNamaProduk().toLowerCase().contains(kataKunci.toLowerCase())) {
                hasil.add(produk);
            }
        }
        return hasil;
    }

    // Mencari produk dengan nama persis sama (untuk mencegah menu ganda)
    public Produk cariProdukNamaPersis(String nama) {
        for (Produk produk : daftarProduk) {
            if (produk.getNamaProduk().equalsIgnoreCase(nama)) {
                return produk;
            }
        }
        return null;
    }

    // Daftar kategori yang ada di menu saat ini (tanpa duplikat, urut sesuai kemunculan)
    public ArrayList<String> getDaftarKategori() {
        ArrayList<String> kategori = new ArrayList<>();
        for (Produk produk : daftarProduk) {
            boolean sudahAda = false;
            for (String k : kategori) {
                if (k.equalsIgnoreCase(produk.getKategori())) {
                    sudahAda = true;
                }
            }
            if (!sudahAda) {
                kategori.add(produk.getKategori());
            }
        }
        return kategori;
    }

    public ArrayList<Produk> filterKategori(String kategori) {
        ArrayList<Produk> hasil = new ArrayList<>();
        for (Produk produk : daftarProduk) {
            if (produk.getKategori().equalsIgnoreCase(kategori)) {
                hasil.add(produk);
            }
        }
        return hasil;
    }

    // Menu diurutkan dari harga termurah (naik = true) atau termahal (naik = false)
    public ArrayList<Produk> urutkanHarga(boolean naik) {
        ArrayList<Produk> hasil = new ArrayList<>(daftarProduk);
        Comparator<Produk> pembanding = Comparator.comparingInt(Produk::getHarga);
        hasil.sort(naik ? pembanding : pembanding.reversed());
        return hasil;
    }

    // Produk yang sudah pernah terjual, diurutkan dari yang paling laris (maksimal 'batas' produk)
    public ArrayList<Produk> getTerlaris(int batas) {
        ArrayList<Produk> hasil = new ArrayList<>();
        for (Produk produk : daftarProduk) {
            if (produk.getTerjual() > 0) {
                hasil.add(produk);
            }
        }
        hasil.sort(Comparator.comparingInt(Produk::getTerjual).reversed());
        return new ArrayList<>(hasil.subList(0, Math.min(batas, hasil.size())));
    }

    // Produk yang harganya tidak melebihi budget dan stoknya masih ada, termurah dulu
    public ArrayList<Produk> cariSesuaiBudget(int budget) {
        ArrayList<Produk> hasil = new ArrayList<>();
        for (Produk produk : urutkanHarga(true)) {
            if (produk.getHarga() <= budget && produk.getStok() > 0) {
                hasil.add(produk);
            }
        }
        return hasil;
    }

    // Produk yang stoknya menipis atau habis (urut dari stok tersedikit)
    public ArrayList<Produk> getStokMenipis() {
        ArrayList<Produk> hasil = new ArrayList<>();
        for (Produk produk : daftarProduk) {
            if (produk.isStokMenipis()) {
                hasil.add(produk);
            }
        }
        hasil.sort(Comparator.comparingInt(Produk::getStok));
        return hasil;
    }

    // ==================== PROMO ====================

    public void tambahPromo(Promo promo) {
        daftarPromo.add(promo);
    }

    // Mencari promo berdasarkan kode (tidak peduli huruf besar/kecil). Hasil null jika tidak ada.
    public Promo cariPromo(String kode) {
        for (Promo promo : daftarPromo) {
            if (promo.getKode().equalsIgnoreCase(kode)) {
                return promo;
            }
        }
        return null;
    }

    public void tampilkanPromo() {
        Tampilan.cetakSubJudul("Promo yang Tersedia");
        if (daftarPromo.isEmpty()) {
            System.out.println("  Belum ada promo.");
            return;
        }
        for (Promo promo : daftarPromo) {
            String syarat = (promo.getMinBelanja() > 0)
                    ? "min. belanja " + Tampilan.rupiah(promo.getMinBelanja())
                    : "tanpa minimal belanja";
            System.out.printf("  %-11s Diskon %s%% - %s (%s)%n", promo.getKode(),
                    Tampilan.persen(promo.getPersen()), promo.getDeskripsi(), syarat);
        }
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

    // [OVERLOADING] tampilkanMenu() menampilkan seluruh menu,
    //               tampilkanMenu(daftar, judul) menampilkan daftar produk pilihan (hasil cari / filter).

    public void tampilkanMenu() {
        tampilkanMenu(daftarProduk, "Daftar Menu - " + namaKantin);
    }

    public void tampilkanMenu(List<Produk> daftar, String judul) {
        Tampilan.cetakSubJudul(judul);
        if (daftar.isEmpty()) {
            System.out.println("  Tidak ada menu yang cocok.");
            return;
        }

        Produk.cetakHeaderTabel();
        int nomor = 1;
        for (Produk produk : daftar) {
            produk.tampilkanInfo(nomor);
            nomor++;
        }
        Tampilan.cetakGaris();
        System.out.println(" Total " + daftar.size() + " menu");
    }

    // [OVERLOADING] tampilkanRiwayat() untuk semua transaksi (penjual),
    //               tampilkanRiwayat(pembeli) hanya transaksi milik satu pembeli.
    public void tampilkanRiwayat(Mahasiswa pembeli) {
        Tampilan.cetakSubJudul("Riwayat Transaksi " + pembeli.getNama());
        ArrayList<Transaksi> milikPembeli = getRiwayatPembeli(pembeli);
        if (milikPembeli.isEmpty()) {
            System.out.println("  Belum ada transaksi.");
            return;
        }

        System.out.printf(FORMAT_RIWAYAT_PEMBELI_HEADER, "ID", "Item", "Total Bayar", "Metode", "Status");
        System.out.println("---------+------+-------------+--------+-----------");
        for (Transaksi transaksi : milikPembeli) {
            String totalBayar = transaksi.isLunas() ? Tampilan.rupiah(transaksi.getTotalAkhir()) : "-";
            System.out.printf(FORMAT_RIWAYAT_PEMBELI_BARIS, transaksi.getIdTransaksi(),
                    transaksi.getJumlahItem(), totalBayar, transaksi.getMetodeBayar(), transaksi.getStatus());
        }
    }

    // Semua transaksi milik satu pembeli (dicocokkan lewat NIM)
    public ArrayList<Transaksi> getRiwayatPembeli(Mahasiswa pembeli) {
        ArrayList<Transaksi> hasil = new ArrayList<>();
        for (Transaksi transaksi : riwayatTransaksi) {
            if (transaksi.getPembeli().getNim().equals(pembeli.getNim())) {
                hasil.add(transaksi);
            }
        }
        return hasil;
    }

    // Mencari satu transaksi milik pembeli berdasarkan ID (contoh "TRX-003"). Null jika tidak ada.
    public Transaksi cariTransaksi(String id, Mahasiswa pembeli) {
        for (Transaksi transaksi : getRiwayatPembeli(pembeli)) {
            if (transaksi.getIdTransaksi().equalsIgnoreCase(id)) {
                return transaksi;
            }
        }
        return null;
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

    // [PRIVATE] Mengingat nomor terbesar yang pernah dipakai kode produk. "P007" -> 7
    private void catatUrutanKode(String kode) {
        if (kode.matches("[Pp]\\d+") && kode.length() <= 9) {
            urutanKodeTerakhir = Math.max(urutanKodeTerakhir, Integer.parseInt(kode.substring(1)));
        }
    }

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
