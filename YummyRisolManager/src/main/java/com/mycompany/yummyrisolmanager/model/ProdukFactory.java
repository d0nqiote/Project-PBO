package com.mycompany.yummyrisolmanager.model;

/** Mengubah satu baris tabel produk menjadi objek kelas yang tepat. */
public final class ProdukFactory {

    private ProdukFactory() {
    }

    public static Produk buat(int id, String nama, String jenis, String rasa, String tipe,
            int isi, double harga, int stok, int stokMinimum) {
        switch (rasa.trim().toUpperCase()) {
            case "COKLAT":
                return new RisolCoklat(id, nama, rasa, tipe, isi, harga, stok, stokMinimum);
            case "MATCHA":
                return new RisolMatcha(id, nama, rasa, tipe, isi, harga, stok, stokMinimum);
            case "MAYO":
                return new RisolMayo(id, nama, rasa, tipe, isi, harga, stok, stokMinimum);
            case "RAGOUT":
                return new RisolRagout(id, nama, rasa, tipe, isi, harga, stok, stokMinimum);
            default:   // contoh: rasa "Campur" pada paket
                if ("MANIS".equalsIgnoreCase(jenis)) {
                    return new PaketManis(id, nama, rasa, tipe, isi, harga, stok, stokMinimum);
                }
                return new PaketAsin(id, nama, rasa, tipe, isi, harga, stok, stokMinimum);
        }
    }
}