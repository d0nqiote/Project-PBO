package com.mycompany.yummyrisolmanager.model;

public class DetailPesanan {

    private final Produk produk;
    private int jumlah;

    public DetailPesanan(Produk produk, int jumlah) {
        this.produk = produk;
        this.jumlah = jumlah;
    }

    public void tambahJumlah(int tambahan) {
        this.jumlah += tambahan;
    }

    /** Subtotal dihitung oleh objek produk-nya sendiri (polymorphism). */
    public double getSubtotal() {
        return produk.hitungHarga(jumlah);
    }

    public Produk getProduk() { return produk; }
    public int getJumlah() { return jumlah; }
}