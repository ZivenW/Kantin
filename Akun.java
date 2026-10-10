package model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Class Akun (abstract)
 * ---------------------
 * Fungsi : induk dari semua jenis akun yang bisa login (AkunPembeli dan AkunPenjual).
 *          Menyimpan username dan password. Password TIDAK disimpan apa adanya,
 *          tetapi diubah menjadi hash SHA-256 bersama salt acak.
 *
 * [ABSTRACT CLASS] Tidak bisa dibuat object langsung; hanya dipakai lewat class turunannya.
 */
public abstract class Akun {

    // [STATIC] Nama peran, dipakai saat login dan saat menampilkan menu
    public static final String PERAN_PEMBELI = "Pembeli";
    public static final String PERAN_PENJUAL = "Penjual";

    public static final int PASSWORD_MIN = 6;

    private static final SecureRandom ACAK = new SecureRandom();

    private String username;
    private String salt;
    private String passwordHash;

    // [CONSTRUCTOR] Dipanggil oleh constructor class turunan
    protected Akun(String username, String password) {
        this.username = username;
        simpanPassword(password);
    }

    public String getUsername() {
        return username;
    }

    // [ABSTRACT METHOD] Wajib diisi oleh class turunan
    public abstract String getPeran();

    // Nama yang ditampilkan di sapaan, contoh: "Rani Putri"
    public abstract String getNamaTampil();

    // Memeriksa apakah password yang diketik sama dengan password akun
    public boolean passwordCocok(String password) {
        return passwordHash.equals(hash(salt, password));
    }

    // Mengganti password. Gagal jika password lama salah atau password baru tidak valid.
    public boolean gantiPassword(String passwordLama, String passwordBaru) {
        if (!passwordCocok(passwordLama) || pesanPasswordTidakValid(passwordBaru) != null) {
            return false;
        }
        simpanPassword(passwordBaru);
        return true;
    }

    // [STATIC METHOD] Mengembalikan alasan password tidak valid, atau null jika password boleh dipakai
    public static String pesanPasswordTidakValid(String password) {
        if (password == null || password.length() < PASSWORD_MIN) {
            return "Password minimal " + PASSWORD_MIN + " karakter.";
        }
        if (password.contains(" ")) {
            return "Password tidak boleh mengandung spasi.";
        }
        return null;
    }

    // ==================== PRIVATE ====================

    private void simpanPassword(String password) {
        byte[] bytes = new byte[8];
        ACAK.nextBytes(bytes);
        this.salt = toHex(bytes);
        this.passwordHash = hash(salt, password);
    }

    private static String hash(String salt, String password) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            return toHex(sha.digest((salt + password).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 tidak tersedia", e);
        }
    }

    private static String toHex(byte[] data) {
        StringBuilder hasil = new StringBuilder();
        for (byte b : data) {
            hasil.append(String.format("%02x", b));
        }
        return hasil.toString();
    }
}
