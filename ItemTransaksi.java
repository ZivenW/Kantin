package model;

/**
 * Class ItemTransaksi
 * -------------------
 * Fungsi : menyimpan SATU baris pembelian dalam transaksi, yaitu produk apa,
 *          berapa jumlahnya, dan berapa harga satuannya saat dibeli.
 *
 * Harga satuan disimpan di sini (bukan diambil ulang dari Produk) supaya struk lama
 * tidak berubah walaupun harga produk dinaikkan di kemudian hari.
 */
public class ItemTransaksi {

    // [PRIVATE] Atribut private
    private Produk produk;
    private int jumlah;
    private int hargaSatuan;

    // [CONSTRUCTOR] Harga satuan langsung "difoto" dari harga produk saat ini
    public ItemTransaksi(Produk produk, int jumlah) {
        this.produk = produk;
        this.jumlah = jumlah;
        this.hargaSatuan = produk.getHarga();
    }

    public Produk getProduk() {
        return produk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public int getHargaSatuan() {
        return hargaSatuan;
    }

    // Menambah jumlah jika produk yang sama dipesan lagi dalam satu transaksi
    public void tambahJumlah(int tambahan) {
        if (tambahan > 0) {
            this.jumlah += tambahan;
        }
    }

    // Subtotal = harga satuan x jumlah
    public int getSubtotal() {
        return hargaSatuan * jumlah;
    }
}
