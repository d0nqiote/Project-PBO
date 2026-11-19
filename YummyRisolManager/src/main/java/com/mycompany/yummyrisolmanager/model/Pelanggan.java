package com.mycompany.yummyrisolmanager.model;

/**
 * Pelanggan Yummy Risol. Turunan Person, ditambah id dan alamat.
 */
public class Pelanggan extends Person {

    private int idPelanggan;
    private String alamat;

    public Pelanggan(int idPelanggan, String nama, String noHp, String alamat) {
        super(nama, noHp);
        this.idPelanggan = idPelanggan;
        this.alamat = alamat;
    }

    /** Untuk pelanggan baru yang belum punya id (id dibuat database). */
    public Pelanggan(String nama, String noHp, String alamat) {
        this(0, nama, noHp, alamat);
    }

    public int getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(int idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    /** Kode tampilan di tabel, misalnya C001. */
    public String getKode() {
        return String.format("C%03d", idPelanggan);
    }
}