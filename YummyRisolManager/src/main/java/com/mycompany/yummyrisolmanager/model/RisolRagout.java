package com.mycompany.yummyrisolmanager.model;

/** Risol ragout: diskon 10% untuk pembelian 20 pcs ke atas (ATURAN HARGA CONTOH - sesuaikan dengan hasil wawancara pemilik). */
public class RisolRagout extends RisolAsin {

    public RisolRagout(int idProduk, String nama, String rasa, String tipe,
            int isi, double harga, int stok, int stokMinimum) {
        super(idProduk, nama, rasa, tipe, isi, harga, stok, stokMinimum);
    }

    @Override
    public double hitungHarga(int jumlah) {
        double total = hargaDasar(jumlah);
        if (jumlah >= 20) {
            total -= total * 0.10;   // diskon 10% mulai 20 pcs
        }
        return Math.round(total);
    }
}