package com.mycompany.yummyrisolmanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** COMPOSITION: Pesanan memiliki daftar DetailPesanan. */
public class Pesanan {

    private final List<DetailPesanan> daftarItem = new ArrayList<>();
    private Pelanggan pelanggan;
    private String status = "BARU";
    private String metodeOrder = "LANGSUNG";

    public void tambahItem(Produk produk, int jumlah) {
        for (DetailPesanan d : daftarItem) {
            if (d.getProduk().getIdProduk() == produk.getIdProduk()) {
                d.tambahJumlah(jumlah);
                return;
            }
        }
        daftarItem.add(new DetailPesanan(produk, jumlah));
    }

    public void hapusItem(int indeks) {
        daftarItem.remove(indeks);
    }

    public int hitungJumlah(Produk produk) {
        int total = 0;
        for (DetailPesanan d : daftarItem) {
            if (d.getProduk().getIdProduk() == produk.getIdProduk()) {
                total += d.getJumlah();
            }
        }
        return total;
    }

    public double hitungTotal() {
        double total = 0;
        for (DetailPesanan d : daftarItem) {
            total += d.getSubtotal();
        }
        return total;
    }

    public List<DetailPesanan> getDaftarItem() { return Collections.unmodifiableList(daftarItem); }
    public Pelanggan getPelanggan() { return pelanggan; }
    public String getStatus() { return status; }
    public String getMetodeOrder() { return metodeOrder; }
    public void setPelanggan(Pelanggan pelanggan) { this.pelanggan = pelanggan; }
    public void setStatus(String status) { this.status = status; }
    public void setMetodeOrder(String metodeOrder) { this.metodeOrder = metodeOrder; }
}