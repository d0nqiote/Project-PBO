package com.mycompany.yummyrisolmanager.model;

/** Perantara: semua risol MANIS. Masih abstrak karena hitungHarga() belum diisi. */
public abstract class RisolManis extends Produk {

    protected RisolManis(int idProduk, String nama, String rasa, String tipe,
            int isi, double harga, int stok, int stokMinimum) {
        super(idProduk, nama, rasa, tipe, isi, harga, stok, stokMinimum);
    }

    @Override
    public String getJenis() {
        return "MANIS";
    }
}