package com.mycompany.yummyrisolmanager.model;

/**
 * Induk untuk semua orang di aplikasi (Pelanggan, Karyawan).
 * Atribut dibuat private dan diakses lewat getter/setter (encapsulation).
 */
public abstract class Person {

    private String nama;
    private String noHp;

    public Person(String nama, String noHp) {
        this.nama = nama;
        this.noHp = noHp;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }
}