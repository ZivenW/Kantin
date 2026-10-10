package model;

/**
 * Class AkunPenjual
 * -----------------
 * Fungsi : akun untuk penjual / pengelola kantin. Penjual boleh mengubah menu, stok,
 *          dan melihat laporan. Akun penjual dibuat oleh program (tidak bisa mendaftar sendiri).
 *
 * [INHERITANCE] "extends Akun"
 */
public class AkunPenjual extends Akun {

    private String namaPenjual;

    public AkunPenjual(String username, String password, String namaPenjual) {
        super(username, password);
        this.namaPenjual = namaPenjual;
    }

    @Override
    public String getPeran() {
        return PERAN_PENJUAL;
    }

    @Override
    public String getNamaTampil() {
        return namaPenjual;
    }
}
