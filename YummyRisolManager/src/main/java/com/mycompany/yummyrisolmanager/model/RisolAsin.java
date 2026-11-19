package com.mycompany.yummyrisolmanager.model;

/** Perantara: semua risol ASIN. Masih abstrak karena hitungHarga() belum diisi. */
public abstract class RisolAsin extends Produk {

    protected RisolAsin(int idProduk, String nama, String rasa, String tipe,
            int isi, double harga, int stok, int stokMinimum) {
        super(idProduk, nama, rasa, tipe, isi, harga, stok, stokMinimum);
    }

    @Override
    public String getJenis() {
        return "ASIN";
    }
}