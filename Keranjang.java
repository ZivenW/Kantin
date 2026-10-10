package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import util.Tampilan;

/**
 * Class Keranjang
 * ---------------
 * Fungsi : menampung produk yang dipilih pembeli SEBELUM checkout.
 *          Berbeda dengan Transaksi, keranjang boleh diubah-ubah (tambah, ubah jumlah, hapus)
 *          dan harganya selalu mengikuti harga produk terbaru.
 */
public class Keranjang {

    private static final String FORMAT_HEADER = " %2s | %-4s | %-24s | %4s | %10s | %11s%n";
    private static final String FORMAT_BARIS = " %2d | %-4s | %-24s | %4d | %10s | %11s%n";

    // LinkedHashMap menjaga urutan produk sesuai waktu dimasukkan
    private LinkedHashMap<Produk, Integer> isi = new LinkedHashMap<>();

    // Menambah produk ke keranjang. Jumlah digabung jika produk sudah ada. Stok dicek.
    public boolean tambah(Produk produk, int jumlah) {
        if (produk == null) {
            Tampilan.gagal("Produk tidak ditemukan.");
            return false;
        }
        if (jumlah <= 0) {
            Tampilan.gagal("Jumlah harus lebih dari 0.");
            return false;
        }
        if (produk.getStok() == 0) {
            Tampilan.gagal(produk.getNamaProduk() + " sedang habis.");
            return false;
        }
        int total = getJumlah(produk) + jumlah;
        if (!produk.isTersedia(total)) {
            Tampilan.gagal("Stok " + produk.getNamaProduk() + " tidak cukup (di keranjang "
                    + getJumlah(produk) + ", tersedia " + produk.getStok() + ")");
            return false;
        }
        isi.put(produk, total);
        Tampilan.sukses(jumlah + " x " + produk.getNamaProduk() + " masuk keranjang");
        return true;
    }

    // Mengganti jumlah produk di keranjang. Jumlah 0 berarti menghapus produk dari keranjang.
    public boolean ubahJumlah(Produk produk, int jumlah) {
        if (!isi.containsKey(produk)) {
            Tampilan.gagal("Produk tidak ada di keranjang.");
            return false;
        }
        if (jumlah == 0) {
            return hapus(produk);
        }
        if (!produk.isTersedia(jumlah)) {
            Tampilan.gagal("Stok " + produk.getNamaProduk() + " tidak cukup (tersedia "
                    + produk.getStok() + ")");
            return false;
        }
        isi.put(produk, jumlah);
        Tampilan.sukses("Jumlah " + produk.getNamaProduk() + " diubah menjadi " + jumlah);
        return true;
    }

    // Mengeluarkan satu produk dari keranjang
    public boolean hapus(Produk produk) {
        if (isi.remove(produk) == null) {
            Tampilan.gagal("Produk tidak ada di keranjang.");
            return false;
        }
        Tampilan.sukses(produk.getNamaProduk() + " dikeluarkan dari keranjang");
        return true;
    }

    // Mengosongkan seluruh keranjang (tanpa pesan, dipakai juga setelah checkout)
    public void kosongkan() {
        isi.clear();
    }

    public boolean isKosong() {
        return isi.isEmpty();
    }

    // Jumlah produk tertentu di keranjang (0 jika belum ada)
    public int getJumlah(Produk produk) {
        Integer jumlah = isi.get(produk);
        return (jumlah == null) ? 0 : jumlah;
    }

    // Total seluruh barang (qty) di keranjang
    public int getJumlahItem() {
        int total = 0;
        for (int jumlah : isi.values()) {
            total += jumlah;
        }
        return total;
    }

    // Total harga sebelum diskon, memakai harga produk saat ini
    public int hitungTotal() {
        int total = 0;
        for (Map.Entry<Produk, Integer> entri : isi.entrySet()) {
            total += entri.getKey().getHarga() * entri.getValue();
        }
        return total;
    }

    // Salinan daftar produk (aman diubah-ubah sambil mengubah keranjang)
    public ArrayList<Produk> getDaftarProduk() {
        return new ArrayList<>(isi.keySet());
    }

    // Menampilkan isi keranjang dalam bentuk tabel
    public void tampilkan() {
        Tampilan.cetakSubJudul("Keranjang Belanja");
        if (isi.isEmpty()) {
            System.out.println("  Keranjang masih kosong.");
            return;
        }
        System.out.printf(FORMAT_HEADER, "No", "Kode", "Nama Produk", "Qty", "Harga", "Subtotal");
        System.out.println("----+------+--------------------------+------+------------+------------");
        int nomor = 1;
        for (Map.Entry<Produk, Integer> entri : isi.entrySet()) {
            Produk produk = entri.getKey();
            int jumlah = entri.getValue();
            System.out.printf(FORMAT_BARIS, nomor, produk.getKode(), Tampilan.potong(produk.getNamaProduk(), 24),
                    jumlah, Tampilan.rupiah(produk.getHarga()), Tampilan.rupiah(produk.getHarga() * jumlah));
            nomor++;
        }
        Tampilan.cetakGaris();
        System.out.printf(" %d barang | Total sebelum diskon: %s%n", getJumlahItem(), Tampilan.rupiah(hitungTotal()));
    }
}
