package main;

import java.util.ArrayList;

import model.Akun;
import model.AkunPembeli;
import model.ItemTransaksi;
import model.Keranjang;
import model.Mahasiswa;
import model.Produk;
import model.Promo;
import service.Kantin;
import service.Transaksi;
import util.Input;
import util.Tampilan;

/**
 * Class MenuPembeli
 * -----------------
 * Fungsi : seluruh tampilan dan alur menu untuk pembeli yang sudah login:
 *          lihat & cari menu, keranjang, checkout (tunai / saldo, kode promo),
 *          top up saldo, riwayat & struk, pesan lagi, dan pengaturan profil.
 */
public class MenuPembeli {

    private final Kantin kantin;
    private final AkunPembeli akun;
    private final Mahasiswa mahasiswa;
    private final Keranjang keranjang;

    public MenuPembeli(Kantin kantin, AkunPembeli akun) {
        this.kantin = kantin;
        this.akun = akun;
        this.mahasiswa = akun.getProfil();
        this.keranjang = akun.getKeranjang();
    }

    // Loop menu utama pembeli. Berakhir saat pembeli memilih logout.
    public void jalankan() {
        while (true) {
            sesuaikanKeranjang();
            Tampilan.cetakBagian("MENU PEMBELI - " + Tampilan.potong(mahasiswa.getNama(), 30));
            System.out.printf("  Saldo: %s | Keranjang: %d barang%n", Tampilan.rupiah(mahasiswa.getSaldo()),
                    keranjang.getJumlahItem());
            Tampilan.cetakPilihan(1, "Lihat & cari menu");
            Tampilan.cetakPilihan(2, "Tambah ke keranjang");
            Tampilan.cetakPilihan(3, "Keranjang saya");
            Tampilan.cetakPilihan(4, "Checkout / bayar");
            Tampilan.cetakPilihan(5, "Lihat promo");
            Tampilan.cetakPilihan(6, "Saldo & top up");
            Tampilan.cetakPilihan(7, "Riwayat transaksi");
            Tampilan.cetakPilihan(8, "Profil & akun saya");
            Tampilan.cetakPilihan(0, "Logout");

            switch (Input.pilihan(8)) {
                case 1:
                    menuLihatMenu();
                    break;
                case 2:
                    tambahKeKeranjang();
                    break;
                case 3:
                    menuKeranjang();
                    break;
                case 4:
                    checkout();
                    break;
                case 5:
                    kantin.tampilkanPromo();
                    Input.lanjut();
                    break;
                case 6:
                    menuSaldo();
                    break;
                case 7:
                    menuRiwayat();
                    break;
                case 8:
                    menuProfil();
                    break;
                default:
                    if (keranjang.isKosong() || Input.ya("Keranjang masih berisi, akan tersimpan sampai login lagi. Logout")) {
                        Tampilan.info("Sampai jumpa, " + mahasiswa.getNama() + "!");
                        return;
                    }
            }
        }
    }

    // ==================== LIHAT & CARI MENU ====================

    private void menuLihatMenu() {
        while (true) {
            Tampilan.cetakSubJudul("Lihat & Cari Menu");
            Tampilan.cetakPilihan(1, "Semua menu");
            Tampilan.cetakPilihan(2, "Berdasarkan kategori");
            Tampilan.cetakPilihan(3, "Cari berdasarkan nama");
            Tampilan.cetakPilihan(4, "Urut harga termurah");
            Tampilan.cetakPilihan(5, "Urut harga termahal");
            Tampilan.cetakPilihan(6, "Menu terlaris");
            Tampilan.cetakPilihan(7, "Menu sesuai budget saya");
            Tampilan.cetakPilihan(0, "Kembali");

            int pilihan = Input.pilihan(7);
            switch (pilihan) {
                case 1:
                    kantin.tampilkanMenu();
                    break;
                case 2:
                    tampilkanPerKategori();
                    break;
                case 3:
                    String kata = Input.teksWajib("Kata kunci nama menu");
                    kantin.tampilkanMenu(kantin.cariBerdasarkanNama(kata), "Hasil pencarian \"" + kata + "\"");
                    break;
                case 4:
                    kantin.tampilkanMenu(kantin.urutkanHarga(true), "Menu - Harga Termurah");
                    break;
                case 5:
                    kantin.tampilkanMenu(kantin.urutkanHarga(false), "Menu - Harga Termahal");
                    break;
                case 6:
                    kantin.tampilkanMenu(kantin.getTerlaris(3), "3 Menu Terlaris");
                    break;
                case 7:
                    int budget = Input.angka("Budget kamu (Rp)", 1, 10000000);
                    kantin.tampilkanMenu(kantin.cariSesuaiBudget(budget),
                            "Menu dengan harga <= " + Tampilan.rupiah(budget) + " (stok tersedia)");
                    break;
                default:
                    return;
            }
            if (Input.ya("Mau tambahkan menu dari daftar ini ke keranjang")) {
                tambahKeKeranjang();
            }
        }
    }

    private void tampilkanPerKategori() {
        ArrayList<String> kategori = kantin.getDaftarKategori();
        if (kategori.isEmpty()) {
            Tampilan.info("Menu masih kosong.");
            return;
        }
        System.out.println();
        for (int i = 0; i < kategori.size(); i++) {
            Tampilan.cetakPilihan(i + 1, kategori.get(i));
        }
        int nomor = Input.angka("Pilih kategori (0=batal)", 0, kategori.size());
        if (nomor == 0) {
            return;
        }
        String dipilih = kategori.get(nomor - 1);
        kantin.tampilkanMenu(kantin.filterKategori(dipilih), "Kategori " + dipilih);
    }

    // ==================== KERANJANG ====================

    private void tambahKeKeranjang() {
        Tampilan.cetakSubJudul("Tambah ke Keranjang");
        Produk produk = pilihProduk("Kode produk (kosong=batal, ?=lihat menu)", true);
        if (produk == null) {
            return;
        }
        System.out.printf("  %s - %s (stok %d, di keranjang %d)%n", produk.getNamaProduk(),
                Tampilan.rupiah(produk.getHarga()), produk.getStok(), keranjang.getJumlah(produk));
        if (produk.getStok() == 0) {
            Tampilan.gagal(produk.getNamaProduk() + " sedang habis.");
            return;
        }
        int jumlah = Input.angka("Jumlah (0=batal)", 0, produk.getStok());
        if (jumlah > 0) {
            keranjang.tambah(produk, jumlah);
        }
    }

    private void menuKeranjang() {
        while (true) {
            sesuaikanKeranjang();
            keranjang.tampilkan();
            if (keranjang.isKosong()) {
                Input.lanjut();
                return;
            }
            Tampilan.cetakPilihan(1, "Ubah jumlah barang");
            Tampilan.cetakPilihan(2, "Hapus barang dari keranjang");
            Tampilan.cetakPilihan(3, "Kosongkan keranjang");
            Tampilan.cetakPilihan(4, "Lanjut ke checkout");
            Tampilan.cetakPilihan(0, "Kembali");

            switch (Input.pilihan(4)) {
                case 1:
                    Produk ubah = pilihDariKeranjang("Nomor barang yang diubah (0=batal)");
                    if (ubah != null) {
                        int jumlah = Input.angka("Jumlah baru (0=hapus barang)", 0, Math.max(ubah.getStok(), 0));
                        keranjang.ubahJumlah(ubah, jumlah);
                    }
                    break;
                case 2:
                    Produk hapus = pilihDariKeranjang("Nomor barang yang dihapus (0=batal)");
                    if (hapus != null) {
                        keranjang.hapus(hapus);
                    }
                    break;
                case 3:
                    if (Input.ya("Yakin kosongkan seluruh keranjang")) {
                        keranjang.kosongkan();
                        Tampilan.sukses("Keranjang dikosongkan");
                    }
                    break;
                case 4:
                    checkout();
                    return;
                default:
                    return;
            }
        }
    }

    // ==================== CHECKOUT ====================

    private void checkout() {
        sesuaikanKeranjang();
        keranjang.tampilkan();
        if (keranjang.isKosong()) {
            Tampilan.info("Tambahkan menu ke keranjang dulu sebelum checkout.");
            Input.lanjut();
            return;
        }

        int total = keranjang.hitungTotal();

        // 1. Kode promo (opsional)
        Promo promo = null;
        String kode = Input.teks("Kode promo (kosongkan jika tidak ada)");
        while (!kode.isEmpty() && promo == null) {
            Promo kandidat = kantin.cariPromo(kode);
            if (kandidat == null) {
                Tampilan.gagal("Kode promo \"" + kode + "\" tidak ditemukan.");
            } else if (!kandidat.memenuhiSyarat(total)) {
                Tampilan.gagal("Promo " + kandidat.getKode() + " butuh minimal belanja "
                        + Tampilan.rupiah(kandidat.getMinBelanja()) + ".");
            } else {
                promo = kandidat;
                Tampilan.sukses("Promo " + promo.getKode() + " dipakai (diskon "
                        + Tampilan.persen(promo.getPersen()) + "%)");
                break;
            }
            kode = Input.teks("Kode promo lain (kosongkan untuk lanjut tanpa promo)");
        }
        double persen = (promo == null) ? 0 : promo.getPersen();
        int potongan = (promo == null) ? 0 : promo.hitungPotongan(total);
        int tagihan = total - potongan;

        System.out.println();
        System.out.printf("  Total belanja : %s%n", Tampilan.rupiah(total));
        if (potongan > 0) {
            System.out.printf("  Diskon        : -%s%n", Tampilan.rupiah(potongan));
        }
        System.out.printf("  TOTAL BAYAR   : %s%n", Tampilan.rupiah(tagihan));

        // 2. Metode pembayaran
        System.out.println();
        Tampilan.cetakPilihan(1, "Bayar tunai");
        Tampilan.cetakPilihan(2, "Bayar dengan saldo (saldo kamu " + Tampilan.rupiah(mahasiswa.getSaldo()) + ")");
        Tampilan.cetakPilihan(0, "Batal");
        int metode = Input.pilihan(2);
        if (metode == 0) {
            Tampilan.info("Checkout dibatalkan, keranjang tetap tersimpan.");
            return;
        }

        // 3. Cek kecukupan uang SEBELUM transaksi dibuat, supaya nomor transaksi tidak terbuang
        int uang = 0;
        if (metode == 1) {
            uang = Input.angka("Uang tunai yang dibayarkan (0=batal)", 0, 100000000);
            if (uang == 0) {
                Tampilan.info("Checkout dibatalkan, keranjang tetap tersimpan.");
                return;
            }
            while (uang < tagihan) {
                Tampilan.gagal("Uang kurang " + Tampilan.rupiah(tagihan - uang) + ". Masukkan jumlah yang cukup.");
                uang = Input.angka("Uang tunai yang dibayarkan (0=batal)", 0, 100000000);
                if (uang == 0) {
                    Tampilan.info("Checkout dibatalkan, keranjang tetap tersimpan.");
                    return;
                }
            }
        } else if (mahasiswa.getSaldo() < tagihan) {
            Tampilan.gagal("Saldo kurang " + Tampilan.rupiah(tagihan - mahasiswa.getSaldo())
                    + ". Top up lewat menu \"Saldo & top up\", atau bayar tunai.");
            return;
        }

        if (!Input.ya("Proses pesanan sekarang")) {
            Tampilan.info("Checkout dibatalkan, keranjang tetap tersimpan.");
            return;
        }

        // 4. Buat transaksi dari isi keranjang lalu bayar
        Transaksi transaksi = new Transaksi(mahasiswa);
        for (Produk produk : keranjang.getDaftarProduk()) {
            transaksi.tambahItem(produk, keranjang.getJumlah(produk));
        }
        boolean berhasil = (metode == 1) ? transaksi.bayar(uang, persen) : transaksi.bayarDenganSaldo(persen);
        if (!berhasil) {
            transaksi.batalkan();
            kantin.catatTransaksi(transaksi);
            Input.lanjut();
            return;
        }

        keranjang.kosongkan();
        transaksi.cetakStruk();
        kantin.catatTransaksi(transaksi);
        Input.lanjut();
    }

    // ==================== SALDO ====================

    private void menuSaldo() {
        Tampilan.cetakSubJudul("Saldo & Top Up");
        System.out.println("  Saldo kamu sekarang : " + Tampilan.rupiah(mahasiswa.getSaldo()));
        System.out.println("  Batas saldo         : " + Tampilan.rupiah(Mahasiswa.SALDO_MAKS));
        System.out.println();
        Tampilan.cetakPilihan(1, "Top up Rp 10.000");
        Tampilan.cetakPilihan(2, "Top up Rp 20.000");
        Tampilan.cetakPilihan(3, "Top up Rp 50.000");
        Tampilan.cetakPilihan(4, "Top up Rp 100.000");
        Tampilan.cetakPilihan(5, "Nominal lain");
        Tampilan.cetakPilihan(0, "Kembali");

        int nominal;
        switch (Input.pilihan(5)) {
            case 1:
                nominal = 10000;
                break;
            case 2:
                nominal = 20000;
                break;
            case 3:
                nominal = 50000;
                break;
            case 4:
                nominal = 100000;
                break;
            case 5:
                nominal = Input.angka("Nominal top up (min " + Tampilan.rupiah(Mahasiswa.TOPUP_MIN) + ", 0=batal)",
                        0, Mahasiswa.SALDO_MAKS);
                if (nominal == 0) {
                    return;
                }
                if (nominal < Mahasiswa.TOPUP_MIN) {
                    Tampilan.gagal("Top up minimal " + Tampilan.rupiah(Mahasiswa.TOPUP_MIN) + ".");
                    return;
                }
                break;
            default:
                return;
        }

        if (!Input.ya("Top up " + Tampilan.rupiah(nominal) + " sekarang")) {
            return;
        }
        if (mahasiswa.topUp(nominal)) {
            Tampilan.sukses("Top up berhasil. Saldo sekarang " + Tampilan.rupiah(mahasiswa.getSaldo()));
        } else {
            Tampilan.gagal("Top up gagal: saldo tidak boleh melebihi " + Tampilan.rupiah(Mahasiswa.SALDO_MAKS) + ".");
        }
        Input.lanjut();
    }

    // ==================== RIWAYAT ====================

    private void menuRiwayat() {
        while (true) {
            kantin.tampilkanRiwayat(mahasiswa);
            if (kantin.getRiwayatPembeli(mahasiswa).isEmpty()) {
                Input.lanjut();
                return;
            }
            System.out.println();
            Tampilan.cetakPilihan(1, "Lihat struk transaksi");
            Tampilan.cetakPilihan(2, "Pesan lagi (masukkan isi transaksi lama ke keranjang)");
            Tampilan.cetakPilihan(0, "Kembali");

            int pilihan = Input.pilihan(2);
            if (pilihan == 0) {
                return;
            }
            Transaksi transaksi = pilihTransaksi();
            if (transaksi == null) {
                continue;
            }
            if (pilihan == 1) {
                transaksi.cetakStruk();
                Input.lanjut();
            } else {
                pesanLagi(transaksi);
            }
        }
    }

    private Transaksi pilihTransaksi() {
        String id = Input.teks("ID transaksi, contoh TRX-001 atau cukup 1 (kosong=batal)");
        if (id.isEmpty()) {
            return null;
        }
        if (id.matches("\\d{1,6}")) {
            id = String.format("TRX-%03d", Integer.parseInt(id));
        }
        Transaksi transaksi = kantin.cariTransaksi(id, mahasiswa);
        if (transaksi == null) {
            Tampilan.gagal("Transaksi " + id + " tidak ditemukan di riwayat kamu.");
        }
        return transaksi;
    }

    private void pesanLagi(Transaksi transaksi) {
        int masuk = 0;
        for (ItemTransaksi item : transaksi.getDaftarItem()) {
            Produk produk = item.getProduk();
            if (!kantin.adaProduk(produk)) {
                Tampilan.gagal(produk.getNamaProduk() + " sudah tidak ada di menu.");
            } else if (keranjang.tambah(produk, item.getJumlah())) {
                masuk++;
            }
        }
        Tampilan.info(masuk + " jenis barang dimasukkan ke keranjang. Harga memakai harga menu terbaru.");
        Input.lanjut();
    }

    // ==================== PROFIL & AKUN ====================

    private void menuProfil() {
        while (true) {
            Tampilan.cetakSubJudul("Profil & Akun Saya");
            System.out.println("  Username  : " + akun.getUsername());
            System.out.println("  Nama      : " + mahasiswa.getNama());
            System.out.println("  NIM       : " + mahasiswa.getNim());
            System.out.println("  Jurusan   : " + mahasiswa.getJurusan());
            System.out.println("  Saldo     : " + Tampilan.rupiah(mahasiswa.getSaldo()));
            System.out.println("  Transaksi : " + mahasiswa.getJumlahTransaksi() + "x belanja, total "
                    + Tampilan.rupiah(mahasiswa.getTotalBelanja()));
            System.out.println();
            Tampilan.cetakPilihan(1, "Ubah nama");
            Tampilan.cetakPilihan(2, "Ubah jurusan");
            Tampilan.cetakPilihan(3, "Ganti password");
            Tampilan.cetakPilihan(0, "Kembali");

            switch (Input.pilihan(3)) {
                case 1:
                    if (mahasiswa.setNama(Input.teksWajib("Nama baru"))) {
                        Tampilan.sukses("Nama diperbarui");
                    }
                    break;
                case 2:
                    if (mahasiswa.setJurusan(Input.teksWajib("Jurusan baru"))) {
                        Tampilan.sukses("Jurusan diperbarui");
                    }
                    break;
                case 3:
                    gantiPassword(akun);
                    break;
                default:
                    return;
            }
        }
    }

    // Dipakai bersama oleh menu pembeli dan penjual
    static void gantiPassword(Akun akun) {
        String lama = Input.teks("Password lama");
        if (!akun.passwordCocok(lama)) {
            Tampilan.gagal("Password lama salah.");
            return;
        }
        String baru = Input.teks("Password baru (min " + Akun.PASSWORD_MIN + " karakter)");
        String masalah = Akun.pesanPasswordTidakValid(baru);
        if (masalah != null) {
            Tampilan.gagal(masalah);
            return;
        }
        if (!Input.teks("Ulangi password baru").equals(baru)) {
            Tampilan.gagal("Pengulangan password tidak sama, password tidak diubah.");
            return;
        }
        if (akun.gantiPassword(lama, baru)) {
            Tampilan.sukses("Password berhasil diganti");
        }
    }

    // ==================== METHOD BANTUAN ====================

    // Memilih produk lewat kode. Hasil null jika dibatalkan / tidak ditemukan.
    private Produk pilihProduk(String pertanyaan, boolean bolehLihatMenu) {
        while (true) {
            String kode = Input.teks(pertanyaan);
            if (kode.isEmpty()) {
                return null;
            }
            if (bolehLihatMenu && kode.equals("?")) {
                kantin.tampilkanMenu();
                continue;
            }
            Produk produk = kantin.cariProduk(kode);
            if (produk == null) {
                Tampilan.gagal("Kode \"" + kode + "\" tidak ditemukan. Ketik ? untuk melihat menu.");
                continue;
            }
            return produk;
        }
    }

    // Memilih salah satu barang di keranjang lewat nomor urut tabel keranjang
    private Produk pilihDariKeranjang(String pertanyaan) {
        ArrayList<Produk> isi = keranjang.getDaftarProduk();
        int nomor = Input.angka(pertanyaan, 0, isi.size());
        return (nomor == 0) ? null : isi.get(nomor - 1);
    }

    // Menyesuaikan keranjang dengan kondisi menu terbaru (penjual bisa saja menghapus menu atau
    // mengurangi stok): produk yang dihapus dikeluarkan, jumlah yang melebihi stok diturunkan.
    private void sesuaikanKeranjang() {
        for (Produk produk : keranjang.getDaftarProduk()) {
            if (!kantin.adaProduk(produk)) {
                Tampilan.info(produk.getNamaProduk() + " sudah dihapus dari menu, dikeluarkan dari keranjang.");
                keranjang.hapus(produk);
            } else if (keranjang.getJumlah(produk) > produk.getStok()) {
                Tampilan.info("Stok " + produk.getNamaProduk() + " berubah menjadi " + produk.getStok()
                        + ", jumlah di keranjang disesuaikan.");
                keranjang.ubahJumlah(produk, produk.getStok());
            }
        }
    }
}
