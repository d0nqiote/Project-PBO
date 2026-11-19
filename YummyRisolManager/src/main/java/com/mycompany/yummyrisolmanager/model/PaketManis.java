package com.mycompany.yummyrisolmanager.model;

/** Paket campur MANIS (rasa "Campur"): harga paket sudah ditetapkan di database. */
public class PaketManis extends RisolManis {

    public PaketManis(int idProduk, String nama, String rasa, String tipe,
            int isi, double harga, int stok, int stokMinimum) {
        super(idProduk, nama, rasa, tipe, isi, harga, stok, stokMinimum);
    }

    @Override
    public double hitungHarga(int jumlah) {
        return hargaDasar(jumlah);
    }
}