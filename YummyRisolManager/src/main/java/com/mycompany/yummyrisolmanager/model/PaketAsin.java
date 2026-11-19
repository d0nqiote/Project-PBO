package com.mycompany.yummyrisolmanager.model;

/** Paket campur ASIN (rasa "Campur"): harga paket sudah ditetapkan di database. */
public class PaketAsin extends RisolAsin {

    public PaketAsin(int idProduk, String nama, String rasa, String tipe,
            int isi, double harga, int stok, int stokMinimum) {
        super(idProduk, nama, rasa, tipe, isi, harga, stok, stokMinimum);
    }

    @Override
    public double hitungHarga(int jumlah) {
        return hargaDasar(jumlah);
    }
}