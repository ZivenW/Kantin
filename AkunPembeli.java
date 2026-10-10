package model;

/**
 * Class AkunPembeli
 * -----------------
 * Fungsi : akun untuk mahasiswa pembeli. Selain username dan password,
 *          akun ini membawa data Mahasiswa (profil, saldo) dan Keranjang belanjanya.
 *
 * [INHERITANCE] "extends Akun" -> mewarisi username, password, dan method login dari Akun.
 */
public class AkunPembeli extends Akun {

    private Mahasiswa profil;
    private Keranjang keranjang;

    public AkunPembeli(String username, String password, Mahasiswa profil) {
        super(username, password);
        this.profil = profil;
        this.keranjang = new Keranjang();
    }

    public Mahasiswa getProfil() {
        return profil;
    }

    public Keranjang getKeranjang() {
        return keranjang;
    }

    // [OVERRIDING] Mengisi method abstract dari Akun
    @Override
    public String getPeran() {
        return PERAN_PEMBELI;
    }

    @Override
    public String getNamaTampil() {
        return profil.getNama();
    }
}
