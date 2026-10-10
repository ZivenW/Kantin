package main;

import java.util.ArrayList;

import model.AkunPenjual;
import model.Produk;
import service.Autentikasi;
import service.Kantin;
import util.Input;
import util.Tampilan;

/**
 * Class MenuPenjual
 * -----------------
 * Fungsi : seluruh tampilan dan alur menu untuk penjual yang sudah login:
 *          tambah / ubah / hapus menu, update stok, serta melihat riwayat, laporan, dan pembeli.
 */
public class MenuPenjual {

    private static final int HARGA_MAKS = 1000000;

    private final Kantin kantin;
    private final Autentikasi autentikasi;
    private final AkunPenjual akun;

    public MenuPenjual(Kantin kantin, Autentikasi autentikasi, AkunPenjual akun) {
        this.kantin = kantin;
        this.autentikasi = autentikasi;
        this.akun = akun;
    }

    // Loop menu utama penjual. Berakhir saat penjual memilih logout.
    public void jalankan() {
        while (true) {
            Tampilan.cetakBagian("MENU PENJUAL - " + Tampilan.potong(akun.getNamaTampil(), 30));
            int menipis = kantin.getStokMenipis().size();
            System.out.printf("  Menu: %d produk | Stok menipis/habis: %d produk | Pendapatan: %s%n",
                    kantin.getJumlahMenu(), menipis, Tampilan.rupiah(kantin.getTotalPendapatan()));
            Tampilan.cetakPilihan(1, "Lihat semua menu");
            Tampilan.cetakPilihan(2, "Tambah menu baru");
            Tampilan.cetakPilihan(3, "Ubah data menu (nama / kategori / harga)");
            Tampilan.cetakPilihan(4, "Update stok");
            Tampilan.cetakPilihan(5, "Hapus menu");
            Tampilan.cetakPilihan(6, "Cek stok menipis / habis");
            Tampilan.cetakPilihan(7, "Riwayat semua transaksi");
            Tampilan.cetakPilihan(8, "Laporan kantin");
            Tampilan.cetakPilihan(9, "Daftar pembeli");
            Tampilan.cetakPilihan(10, "Ganti password");
            Tampilan.cetakPilihan(0, "Logout");

            switch (Input.pilihan(10)) {
                case 1:
                    kantin.tampilkanMenu();
                    Input.lanjut();
                    break;
                case 2:
                    tambahMenu();
                    break;
                case 3:
                    ubahMenu();
                    break;
                case 4:
                    updateStok();
                    break;
                case 5:
                    hapusMenu();
                    break;
                case 6:
                    kantin.tampilkanMenu(kantin.getStokMenipis(),
                            "Stok Menipis / Habis (stok <= " + Produk.BATAS_STOK_MENIPIS + ")");
                    Input.lanjut();
                    break;
                case 7:
                    kantin.tampilkanRiwayat();
                    Input.lanjut();
                    break;
                case 8:
                    kantin.tampilkanLaporan();
                    Input.lanjut();
                    break;
                case 9:
                    autentikasi.tampilkanDaftarPembeli();
                    Input.lanjut();
                    break;
                case 10:
                    MenuPembeli.gantiPassword(akun);
                    break;
                default:
                    Tampilan.info("Logout berhasil.");
                    return;
            }
        }
    }

    // ==================== TAMBAH MENU ====================

    private void tambahMenu() {
        Tampilan.cetakSubJudul("Tambah Menu Baru");
        String kode = kantin.buatKodeBaru();
        Tampilan.info("Kode produk otomatis: " + kode);

        // Nama tidak boleh sama dengan menu yang sudah ada
        String nama;
        while (true) {
            nama = Input.teks("Nama makanan/minuman (kosong=batal)");
            if (nama.isEmpty()) {
                Tampilan.info("Penambahan menu dibatalkan.");
                return;
            }
            Produk sama = kantin.cariProdukNamaPersis(nama);
            if (sama == null) {
                break;
            }
            Tampilan.gagal("Menu \"" + sama.getNamaProduk() + "\" sudah ada (kode " + sama.getKode()
                    + "). Gunakan menu Update stok atau Ubah data.");
        }

        String kategori = pilihKategori();
        int harga = Input.angka("Harga (Rp)", 1, HARGA_MAKS);
        int stok = Input.angka("Stok awal (0 - " + Produk.STOK_MAKS + ")", 0, Produk.STOK_MAKS);

        Produk baru = new Produk(kode, nama, kategori, harga, stok);
        if (kantin.tambahProduk(baru)) {
            Tampilan.sukses("Menu baru ditambahkan:");
            baru.tampilkanInfo();
        }
        Input.lanjut();
    }

    // Memilih kategori yang sudah ada, atau mengetik kategori baru
    private String pilihKategori() {
        ArrayList<String> kategori = kantin.getDaftarKategori();
        System.out.println();
        System.out.println("  Kategori:");
        for (int i = 0; i < kategori.size(); i++) {
            Tampilan.cetakPilihan(i + 1, kategori.get(i));
        }
        Tampilan.cetakPilihan(kategori.size() + 1, "Kategori baru...");
        int nomor = Input.angka("Pilih kategori", 1, kategori.size() + 1);
        if (nomor <= kategori.size()) {
            return kategori.get(nomor - 1);
        }
        return Input.teksWajib("Nama kategori baru");
    }

    // ==================== UBAH DATA MENU ====================

    private void ubahMenu() {
        Tampilan.cetakSubJudul("Ubah Data Menu");
        Produk produk = pilihProduk("Kode produk yang diubah (kosong=batal, ?=lihat menu)");
        if (produk == null) {
            return;
        }

        while (true) {
            System.out.println();
            produk.tampilkanInfo();
            System.out.println();
            Tampilan.cetakPilihan(1, "Ubah nama");
            Tampilan.cetakPilihan(2, "Ubah kategori");
            Tampilan.cetakPilihan(3, "Ubah harga");
            Tampilan.cetakPilihan(0, "Selesai");

            switch (Input.pilihan(3)) {
                case 1:
                    String nama = Input.teks("Nama baru (kosong=batal)");
                    Produk sama = nama.isEmpty() ? null : kantin.cariProdukNamaPersis(nama);
                    if (nama.isEmpty()) {
                        break;
                    }
                    if (sama != null && sama != produk) {
                        Tampilan.gagal("Sudah ada menu bernama \"" + sama.getNamaProduk() + "\" (kode "
                                + sama.getKode() + ").");
                    } else if (produk.setNamaProduk(nama)) {
                        Tampilan.sukses("Nama diperbarui menjadi " + produk.getNamaProduk());
                    }
                    break;
                case 2:
                    if (produk.setKategori(pilihKategori())) {
                        Tampilan.sukses("Kategori diperbarui menjadi " + produk.getKategori());
                    }
                    break;
                case 3:
                    int lama = produk.getHarga();
                    produk.setHarga(Input.angka("Harga baru (Rp)", 1, HARGA_MAKS));
                    Tampilan.sukses("Harga " + produk.getNamaProduk() + ": " + Tampilan.rupiah(lama)
                            + " -> " + Tampilan.rupiah(produk.getHarga()));
                    Tampilan.info("Struk transaksi lama tidak berubah.");
                    break;
                default:
                    return;
            }
        }
    }

    // ==================== UPDATE STOK ====================

    private void updateStok() {
        Tampilan.cetakSubJudul("Update Stok");
        Produk produk = pilihProduk("Kode produk (kosong=batal, ?=lihat menu)");
        if (produk == null) {
            return;
        }

        while (true) {
            System.out.printf("%n  %s (%s) - stok sekarang: %d%n", produk.getNamaProduk(), produk.getKode(),
                    produk.getStok());
            Tampilan.cetakPilihan(1, "Tambah stok (restok)");
            Tampilan.cetakPilihan(2, "Kurangi stok (barang rusak / koreksi)");
            Tampilan.cetakPilihan(3, "Set stok ke angka tertentu");
            Tampilan.cetakPilihan(0, "Selesai");

            int sisaRuang = Produk.STOK_MAKS - produk.getStok();
            switch (Input.pilihan(3)) {
                case 1:
                    if (sisaRuang == 0) {
                        Tampilan.gagal("Stok sudah mencapai batas " + Produk.STOK_MAKS + ".");
                        break;
                    }
                    int tambah = Input.angka("Jumlah ditambah (0=batal, maks " + sisaRuang + ")", 0, sisaRuang);
                    if (tambah > 0) {
                        kantin.restok(produk.getKode(), tambah);
                    }
                    break;
                case 2:
                    if (produk.getStok() == 0) {
                        Tampilan.gagal("Stok sudah 0, tidak bisa dikurangi.");
                        break;
                    }
                    int kurang = Input.angka("Jumlah dikurangi (0=batal, maks " + produk.getStok() + ")", 0,
                            produk.getStok());
                    if (kurang > 0) {
                        produk.setStok(produk.getStok() - kurang);
                        Tampilan.sukses("Stok " + produk.getNamaProduk() + " -" + kurang + " -> stok sekarang "
                                + produk.getStok());
                    }
                    break;
                case 3:
                    int baru = Input.angka("Stok baru (0 - " + Produk.STOK_MAKS + ")", 0, Produk.STOK_MAKS);
                    produk.setStok(baru);
                    Tampilan.sukses("Stok " + produk.getNamaProduk() + " diatur menjadi " + produk.getStok());
                    break;
                default:
                    return;
            }
        }
    }

    // ==================== HAPUS MENU ====================

    private void hapusMenu() {
        Tampilan.cetakSubJudul("Hapus Menu");
        Produk produk = pilihProduk("Kode produk yang dihapus (kosong=batal, ?=lihat menu)");
        if (produk == null) {
            return;
        }
        System.out.println();
        produk.tampilkanInfo();
        System.out.println();
        if (Input.ya("Yakin hapus \"" + produk.getNamaProduk() + "\" dari menu")) {
            kantin.hapusProduk(produk.getKode());
            Tampilan.info("Riwayat transaksi lama tetap tersimpan. Keranjang pembeli akan menyesuaikan otomatis.");
        } else {
            Tampilan.info("Penghapusan dibatalkan.");
        }
        Input.lanjut();
    }

    // ==================== METHOD BANTUAN ====================

    // Memilih produk lewat kode. Hasil null jika dibatalkan.
    private Produk pilihProduk(String pertanyaan) {
        if (kantin.getJumlahMenu() == 0) {
            Tampilan.info("Menu masih kosong.");
            return null;
        }
        while (true) {
            String kode = Input.teks(pertanyaan);
            if (kode.isEmpty()) {
                return null;
            }
            if (kode.equals("?")) {
                kantin.tampilkanMenu();
                continue;
            }
            Produk produk = kantin.cariProduk(kode);
            if (produk != null) {
                return produk;
            }
            Tampilan.gagal("Kode \"" + kode + "\" tidak ditemukan. Ketik ? untuk melihat menu.");
        }
    }
}
