package com.mycompany.yummyrisolmanager.model;

/** Risol mayo: harga normal tanpa promo (ATURAN HARGA CONTOH - sesuaikan dengan hasil wawancara pemilik). */
public class RisolMayo extends RisolAsin {

    public RisolMayo(int idProduk, String nama, String rasa, String tipe,
            int isi, double harga, int stok, int stokMinimum) {
        super(idProduk, nama, rasa, tipe, isi, harga, stok, stokMinimum);
    }

    @Override
    public double hitungHarga(int jumlah) {
        return hargaDasar(jumlah);   // harga normal
    }
}