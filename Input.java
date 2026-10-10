package util;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Class Input (class utilitas)
 * ----------------------------
 * Fungsi : membaca ketikan pengguna di console dengan aman. Semua method di sini mengulang
 *          pertanyaan sampai jawabannya valid, sehingga program tidak crash karena salah ketik.
 *
 * Seperti Tampilan, semua anggotanya static: Input.angka("Jumlah", 1, 10)
 */
public class Input {

    // [STATIC] Satu Scanner dipakai bersama supaya input tidak terbaca ganda
    private static final Scanner SCANNER = new Scanner(System.in);

    private Input() {
    }

    // Membaca satu baris teks (sudah di-trim). Bisa menghasilkan teks kosong.
    public static String teks(String pertanyaan) {
        System.out.print("  " + pertanyaan + " : ");
        try {
            return SCANNER.nextLine().trim();
        } catch (NoSuchElementException e) {
            // Input habis (misalnya Ctrl+D): tutup program dengan rapi
            System.out.println();
            Tampilan.info("Input berakhir, program ditutup.");
            System.exit(0);
            return "";
        }
    }

    // Membaca teks yang tidak boleh kosong
    public static String teksWajib(String pertanyaan) {
        while (true) {
            String jawaban = teks(pertanyaan);
            if (!jawaban.isEmpty()) {
                return jawaban;
            }
            Tampilan.gagal("Tidak boleh kosong.");
        }
    }

    // Membaca angka bulat antara min dan max (inklusif). Titik pemisah ribuan diabaikan: "15.000" -> 15000
    public static int angka(String pertanyaan, int min, int max) {
        while (true) {
            String jawaban = teks(pertanyaan).replace(".", "").replace(" ", "");
            try {
                long nilai = Long.parseLong(jawaban);
                if (nilai >= min && nilai <= max) {
                    return (int) nilai;
                }
                Tampilan.gagal("Masukkan angka " + min + " sampai " + max + ".");
            } catch (NumberFormatException e) {
                Tampilan.gagal("Masukkan angka saja (" + min + " - " + max + ").");
            }
        }
    }

    // Membaca pilihan menu: angka 0 sampai maks
    public static int pilihan(int maks) {
        System.out.println();
        return angka("Pilih menu", 0, maks);
    }

    // Pertanyaan ya/tidak. Menerima y/ya dan n/t/tidak.
    public static boolean ya(String pertanyaan) {
        while (true) {
            String jawaban = teks(pertanyaan + " (y/n)").toLowerCase();
            if (jawaban.equals("y") || jawaban.equals("ya")) {
                return true;
            }
            if (jawaban.equals("n") || jawaban.equals("t") || jawaban.equals("tidak")) {
                return false;
            }
            Tampilan.gagal("Jawab dengan y atau n.");
        }
    }

    // Berhenti sebentar supaya hasil di layar sempat dibaca
    public static void lanjut() {
        teks("Tekan Enter untuk lanjut");
    }
}
