package com.mycompany.yummyrisolmanager.model;

/** Risol matcha: promo beli 5 gratis 1 (ATURAN HARGA CONTOH - sesuaikan dengan hasil wawancara pemilik). */
public class RisolMatcha extends RisolManis {

    public RisolMatcha(int idProduk, String nama, String rasa, String tipe,
            int isi, double harga, int stok, int stokMinimum) {
        super(idProduk, nama, rasa, tipe, isi, harga, stok, stokMinimum);
    }

    @Override
    public double hitungHarga(int jumlah) {
        int dibayar = jumlah - (jumlah / 5);   // beli 5 gratis 1
        return hargaDasar(dibayar);
    }
}