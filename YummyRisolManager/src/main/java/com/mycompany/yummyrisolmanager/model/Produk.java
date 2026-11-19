package com.mycompany.yummyrisolmanager.model;

/** Induk semua risol. Abstrak: tidak ada produk yang "cuma risol", harus punya jenis & rasa. */
public abstract class Produk {

    private int idProduk;
    private String nama;
    private String rasa;
    private String tipe;       // SATUAN atau PAKET
    private int isi;
    private double harga;
    private int stok;
    private int stokMinimum;

    protected Produk(int idProduk, String nama, String rasa, String tipe,
            int isi, double harga, int stok, int stokMinimum) {
        this.idProduk = idProduk;
        this.nama = nama;
        this.rasa = rasa;
        this.tipe = tipe;
        this.isi = isi;
        setHarga(harga);
        setStok(stok);
        this.stokMinimum = stokMinimum;
    }

    /** Diisi oleh RisolManis / RisolAsin. */
    public abstract String getJenis();

    /** POLYMORPHISM: tiap rasa boleh punya aturan harga sendiri. */
    public abstract double hitungHarga(int jumlah);

    protected double hargaDasar(int jumlah) {
        return harga * jumlah;
    }

    public boolean isStokMenipis() {
        return stok <= stokMinimum;
    }

    public boolean isPaket() {
        return "PAKET".equalsIgnoreCase(tipe);
    }

    public int getIdProduk() { return idProduk; }
    public String getNama() { return nama; }
    public String getRasa() { return rasa; }
    public String getTipe() { return tipe; }
    public int getIsi() { return isi; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }
    public int getStokMinimum() { return stokMinimum; }

    public void setNama(String nama) { this.nama = nama; }
    public void setRasa(String rasa) { this.rasa = rasa; }
    public void setTipe(String tipe) { this.tipe = tipe; }
    public void setIsi(int isi) { this.isi = isi; }
    public void setStokMinimum(int stokMinimum) { this.stokMinimum = stokMinimum; }

    public void setHarga(double harga) {
        if (harga < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif.");
        }
        this.harga = harga;
    }

    public void setStok(int stok) {
        if (stok < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif.");
        }
        this.stok = stok;
    }

    /** Dipakai JComboBox untuk menampilkan teks item. */
    @Override
    public String toString() {
        return nama;
    }
}