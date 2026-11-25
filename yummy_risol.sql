-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.4.3 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for yummy_risol
CREATE DATABASE IF NOT EXISTS `yummy_risol` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `yummy_risol`;

-- Dumping structure for table yummy_risol.detail_pesanan
CREATE TABLE IF NOT EXISTS `detail_pesanan` (
  `id_detail` int NOT NULL AUTO_INCREMENT,
  `id_pesanan` int NOT NULL,
  `id_produk` int NOT NULL,
  `jumlah` int NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id_detail`),
  KEY `id_pesanan` (`id_pesanan`),
  KEY `id_produk` (`id_produk`),
  CONSTRAINT `detail_pesanan_ibfk_1` FOREIGN KEY (`id_pesanan`) REFERENCES `pesanan` (`id_pesanan`) ON DELETE CASCADE,
  CONSTRAINT `detail_pesanan_ibfk_2` FOREIGN KEY (`id_produk`) REFERENCES `produk` (`id_produk`),
  CONSTRAINT `detail_pesanan_chk_1` CHECK ((`jumlah` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table yummy_risol.detail_pesanan: ~3 rows (approximately)
INSERT INTO `detail_pesanan` (`id_detail`, `id_pesanan`, `id_produk`, `jumlah`, `subtotal`) VALUES
	(1, 1, 1, 2, 6000.00),
	(2, 1, 3, 2, 7000.00),
	(3, 2, 6, 1, 18000.00);

-- Dumping structure for table yummy_risol.pelanggan
CREATE TABLE IF NOT EXISTS `pelanggan` (
  `id_pelanggan` int NOT NULL AUTO_INCREMENT,
  `nama` varchar(100) NOT NULL,
  `no_hp` varchar(20) DEFAULT NULL,
  `alamat` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_pelanggan`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table yummy_risol.pelanggan: ~2 rows (approximately)
INSERT INTO `pelanggan` (`id_pelanggan`, `nama`, `no_hp`, `alamat`) VALUES
	(1, 'Rina', '081234567890', 'Kost Jatinangor, Sayang'),
	(2, 'Dimas', '081298765432', 'Cikeruh, Jatinangor');

-- Dumping structure for table yummy_risol.pengguna
CREATE TABLE IF NOT EXISTS `pengguna` (
  `id_pengguna` int NOT NULL AUTO_INCREMENT,
  `nama` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `jabatan` enum('ADMIN','KASIR') NOT NULL DEFAULT 'KASIR',
  PRIMARY KEY (`id_pengguna`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table yummy_risol.pengguna: ~2 rows (approximately)
INSERT INTO `pengguna` (`id_pengguna`, `nama`, `username`, `password`, `jabatan`) VALUES
	(1, 'Pemilik Yummy Risol', 'admin', 'admin123', 'ADMIN'),
	(2, 'Kasir 1', 'kasir', 'kasir123', 'KASIR');

-- Dumping structure for table yummy_risol.pesanan
CREATE TABLE IF NOT EXISTS `pesanan` (
  `id_pesanan` int NOT NULL AUTO_INCREMENT,
  `id_pelanggan` int NOT NULL,
  `id_pengguna` int NOT NULL,
  `tanggal` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `metode_order` enum('LANGSUNG','WHATSAPP','TITIP') NOT NULL DEFAULT 'LANGSUNG',
  `status` enum('BARU','DIPROSES','SELESAI') NOT NULL DEFAULT 'BARU',
  `total_bayar` decimal(12,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id_pesanan`),
  KEY `id_pelanggan` (`id_pelanggan`),
  KEY `id_pengguna` (`id_pengguna`),
  CONSTRAINT `pesanan_ibfk_1` FOREIGN KEY (`id_pelanggan`) REFERENCES `pelanggan` (`id_pelanggan`),
  CONSTRAINT `pesanan_ibfk_2` FOREIGN KEY (`id_pengguna`) REFERENCES `pengguna` (`id_pengguna`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table yummy_risol.pesanan: ~8 rows (approximately)
INSERT INTO `pesanan` (`id_pesanan`, `id_pelanggan`, `id_pengguna`, `tanggal`, `metode_order`, `status`, `total_bayar`) VALUES
	(1, 1, 2, '2026-11-10 14:03:53', 'WHATSAPP', 'SELESAI', 13000.00),
	(2, 2, 2, '2026-11-10 14:03:53', 'LANGSUNG', 'BARU', 18000.00),
	(3, 1, 2, '2026-11-12 21:04:46', 'LANGSUNG', 'SELESAI', 45000.00),
	(4, 2, 2, '2026-11-13 21:04:46', 'WHATSAPP', 'SELESAI', 70000.00),
	(5, 1, 2, '2026-11-14 21:04:46', 'LANGSUNG', 'SELESAI', 55000.00),
	(6, 2, 2, '2026-11-15 21:04:46', 'TITIP', 'SELESAI', 85000.00),
	(7, 1, 2, '2026-11-16 21:04:46', 'WHATSAPP', 'SELESAI', 100000.00),
	(8, 2, 2, '2026-11-17 21:04:46', 'LANGSUNG', 'SELESAI', 65000.00);

-- Dumping structure for table yummy_risol.produk
CREATE TABLE IF NOT EXISTS `produk` (
  `id_produk` int NOT NULL AUTO_INCREMENT,
  `nama_produk` varchar(100) NOT NULL,
  `jenis` enum('MANIS','ASIN') NOT NULL,
  `rasa` varchar(50) NOT NULL,
  `tipe` enum('SATUAN','PAKET') NOT NULL DEFAULT 'SATUAN',
  `isi` int NOT NULL DEFAULT '1',
  `harga` decimal(10,2) NOT NULL,
  `stok` int NOT NULL DEFAULT '0',
  `stok_minimum` int NOT NULL DEFAULT '5',
  PRIMARY KEY (`id_produk`),
  CONSTRAINT `produk_chk_1` CHECK ((`harga` >= 0)),
  CONSTRAINT `produk_chk_2` CHECK ((`stok` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table yummy_risol.produk: ~6 rows (approximately)
INSERT INTO `produk` (`id_produk`, `nama_produk`, `jenis`, `rasa`, `tipe`, `isi`, `harga`, `stok`, `stok_minimum`) VALUES
	(1, 'Risol Coklat', 'MANIS', 'Coklat', 'SATUAN', 1, 3000.00, 40, 10),
	(2, 'Risol Matcha', 'MANIS', 'Matcha', 'SATUAN', 1, 3500.00, 8, 10),
	(3, 'Risol Mayo', 'ASIN', 'Mayo', 'SATUAN', 1, 3500.00, 50, 10),
	(4, 'Risol Ragout', 'ASIN', 'Ragout', 'SATUAN', 1, 4000.00, 5, 10),
	(5, 'Paket Manis (5 pcs)', 'MANIS', 'Campur', 'PAKET', 5, 15000.00, 10, 3),
	(6, 'Paket Asin (5 pcs)', 'ASIN', 'Campur', 'PAKET', 5, 18000.00, 10, 3);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
