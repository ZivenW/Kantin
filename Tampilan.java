package util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Class Tampilan (class utilitas)
 * -------------------------------
 * Fungsi : merapikan tampilan output di console, misalnya format rupiah,
 *          garis pemisah, judul, dan pesan status (INFO / OK / GAGAL).
 *
 * Semua anggota class ini bersifat static, jadi dipanggil langsung lewat
 * nama class tanpa membuat object. Contoh: Tampilan.rupiah(15000)
 */
public class Tampilan {

    // [STATIC] Lebar garis dan judul di console (dipakai bersama oleh seluruh program)
    public static final int LEBAR = 72;

    // [PRIVATE] Constructor private: class utilitas ini tidak perlu dibuat object-nya
    private Tampilan() {
    }

    // ==================== FORMAT TEKS ====================

    // [STATIC METHOD] Mengubah angka menjadi format rupiah. Contoh: 15000 -> "Rp 15.000"
    public static String rupiah(int nominal) {
        DecimalFormatSymbols simbol = new DecimalFormatSymbols(Locale.US);
        simbol.setGroupingSeparator('.');
        DecimalFormat format = new DecimalFormat("#,##0", simbol);

        if (nominal < 0) {
            return "-Rp " + format.format(-(long) nominal);
        }
        return "Rp " + format.format(nominal);
    }

    // Mengulang satu karakter sebanyak 'jumlah' kali. Contoh: ulang('-', 3) -> "---"
    public static String ulang(char karakter, int jumlah) {
        StringBuilder hasil = new StringBuilder();
        for (int i = 0; i < jumlah; i++) {
            hasil.append(karakter);
        }
        return hasil.toString();
    }

    // Menggeser teks supaya tampil di tengah baris selebar 'lebar' karakter
    public static String tengah(String teks, int lebar) {
        if (teks.length() >= lebar) {
            return teks;
        }
        int spasiKiri = (lebar - teks.length()) / 2;
        return ulang(' ', spasiKiri) + teks;
    }

    // Memotong teks yang terlalu panjang agar kolom tabel tidak bergeser
    // Contoh: potong("Nasi Goreng Spesial", 10) -> "Nasi Gor.."
    public static String potong(String teks, int panjangMaks) {
        if (teks.length() <= panjangMaks) {
            return teks;
        }
        if (panjangMaks < 3) {
            return teks.substring(0, Math.max(panjangMaks, 0));
        }
        return teks.substring(0, panjangMaks - 2) + "..";
    }

    // ==================== GARIS DAN JUDUL ====================

    // [OVERLOADING] Ada tiga method bernama cetakGaris() dengan parameter berbeda.

    // Garis standar: karakter '-' sepanjang LEBAR
    public static void cetakGaris() {
        cetakGaris('-', LEBAR);
    }

    // Garis dengan karakter pilihan, panjangnya LEBAR
    public static void cetakGaris(char simbol) {
        cetakGaris(simbol, LEBAR);
    }

    // Garis dengan karakter dan panjang pilihan
    public static void cetakGaris(char simbol, int panjang) {
        System.out.println(ulang(simbol, panjang));
    }

    // [OVERLOADING] cetakJudul(judul) dan cetakJudul(judul, subJudul)
    public static void cetakJudul(String judul) {
        cetakGaris('=');
        System.out.println(tengah(judul, LEBAR));
        cetakGaris('=');
    }

    public static void cetakJudul(String judul, String subJudul) {
        cetakGaris('=');
        System.out.println(tengah(judul, LEBAR));
        System.out.println(tengah(subJudul, LEBAR));
        cetakGaris('=');
    }

    // Judul untuk setiap tahap program (dibingkai tanda + dan -)
    public static void cetakBagian(String judul) {
        String garis = "+" + ulang('-', LEBAR - 2) + "+";
        System.out.println();
        System.out.println(garis);
        System.out.println("| " + judul + ulang(' ', LEBAR - 4 - judul.length()) + " |");
        System.out.println(garis);
    }

    // Sub judul untuk satu langkah / satu transaksi
    public static void cetakSubJudul(String judul) {
        System.out.println();
        System.out.println(">> " + judul);
        cetakGaris();
    }

    // ==================== PESAN STATUS ====================

    public static void info(String pesan) {
        cetakPesan("[INFO]", pesan);
    }

    public static void sukses(String pesan) {
        cetakPesan("[OK]", pesan);
    }

    public static void gagal(String pesan) {
        cetakPesan("[GAGAL]", pesan);
    }

    // [PRIVATE] Method bantu, hanya dipakai di dalam class ini
    private static void cetakPesan(String label, String pesan) {
        System.out.printf("  %-8s%s%n", label, pesan);
    }
}
