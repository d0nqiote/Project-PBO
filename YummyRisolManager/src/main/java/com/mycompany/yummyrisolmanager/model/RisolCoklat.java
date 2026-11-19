package com.mycompany.yummyrisolmanager.model;

/** Risol coklat: diskon 5% untuk pembelian 10 pcs ke atas (ATURAN HARGA CONTOH - sesuaikan dengan hasil wawancara pemilik). */
public class RisolCoklat extends RisolManis {

    public RisolCoklat(int idProduk, String nama, String rasa, String tipe,
            int isi, double harga, int stok, int stokMinimum) {
        super(idProduk, nama, rasa, tipe, isi, harga, stok, stokMinimum);
    }

    @Override
    public double hitungHarga(int jumlah) {
        double total = hargaDasar(jumlah);
        if (jumlah >= 10) {
            total -= total * 0.05;   // diskon 5% mulai 10 pcs
        }
        return Math.round(total);
    }
}