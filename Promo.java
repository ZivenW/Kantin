package model;

/**
 * Class Promo
 * -----------
 * Fungsi : menyimpan satu kode promo diskon (kode, deskripsi, persen diskon, minimal belanja).
 *          Pembeli memasukkan kode ini saat checkout untuk mendapat potongan harga.
 */
public class Promo {

    // [PRIVATE] Atribut private
    private String kode;
    private String deskripsi;
    private double persen;
    private int minBelanja;

    // [CONSTRUCTOR] Membuat promo baru
    public Promo(String kode, String deskripsi, double persen, int minBelanja) {
        this.kode = kode.toUpperCase();
        this.deskripsi = deskripsi;
        this.persen = persen;
        this.minBelanja = Math.max(minBelanja, 0);
    }

    public String getKode() {
        return kode;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public double getPersen() {
        return persen;
    }

    public int getMinBelanja() {
        return minBelanja;
    }

    // Promo hanya berlaku jika total belanja mencapai minimal belanja
    public boolean memenuhiSyarat(int totalBelanja) {
        return totalBelanja >= minBelanja;
    }

    // Menghitung potongan harga (rupiah). Cara pembulatannya sama dengan Transaksi.
    public int hitungPotongan(int totalBelanja) {
        if (persen <= 0 || persen > 100) {
            return 0;
        }
        return (int) Math.round(totalBelanja * persen / 100.0);
    }
}
