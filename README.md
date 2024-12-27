# Panduan Penggunaan Movie Tracker

## Deskripsi

Movie Tracker adalah aplikasi berbasis Java yang memungkinkan pengguna untuk melacak film favorit mereka. Aplikasi ini menggunakan API dari TMDb untuk mencari dan menambahkan informasi film ke daftar pengguna. Pengguna dapat melihat detail film, menambahkan film baru, menghapus film, serta mengubah status tontonan film.

## Fitur

- **Menambahkan Film**: Cari dan tambahkan film ke daftar menggunakan API TMDb.
- **Menghapus Film**: Hapus film yang dipilih dari daftar.
- **Melihat Detail Film**: Tampilkan detail lengkap seperti sinopsis, durasi, rating, status, dan poster.
- **Mengubah Status Tontonan**: Ubah status film antara "Sudah Ditonton" dan "Belum Ditonton".

## Prasyarat

1. **Java Development Kit (JDK)** versi 8 atau lebih baru.
2. **Koneksi Internet** untuk mengambil data film dari TMDb.

## Cara Menjalankan Program

1. Clone atau unduh repositori ini ke komputer Anda.
2. Pastikan Anda memiliki JDK yang terinstal.
3. Buka terminal atau IDE favorit Anda, lalu kompilasi dan jalankan file `MovieTracker.java`.
   ```bash
   javac MovieTracker.java
   java com.example.MovieTracker
   ```
4. Program akan membuka antarmuka grafis untuk mulai menggunakan aplikasi.

## Cara Menggunakan

1. **Menambahkan Film**:
   - Klik tombol "Add Movie".
   - Masukkan judul film yang ingin dicari.
   - Pilih film dari hasil pencarian.
   - Film akan ditambahkan ke daftar.

2. **Menghapus Film**:
   - Pilih film di tabel.
   - Klik tombol "Delete Movie".

3. **Melihat Detail Film**:
   - Pilih film di tabel.
   - Klik tombol "View Details".

4. **Mengubah Status Tontonan**:
   - Pilih film di tabel.
   - Klik tombol "Toggle Status".

## Struktur Program

- **MovieTracker**: Kelas utama yang mengatur GUI dan logika aplikasi.
- **Movie**: Kelas untuk merepresentasikan data film, termasuk judul, sinopsis, durasi, rating, status tontonan, dan poster.
- **ImageRenderer**: Kelas untuk merender poster film di tabel.

## API yang Digunakan

Aplikasi ini menggunakan API dari [TMDb](https://www.themoviedb.org/) untuk mengambil data film. Anda memerlukan API key untuk mengakses layanan ini.

### Mengganti API Key

Untuk mengganti API key:
1. Buka file `MovieTracker.java`.
2. Ganti nilai konstanta `API_KEY` dengan API key Anda:
   ```java
   private static final String API_KEY = "API_KEY_ANDA";
   ```

## Catatan Penting

- Pastikan Anda memiliki koneksi internet yang stabil saat menggunakan aplikasi ini.
- API key yang digunakan adalah contoh. Pastikan untuk menggantinya dengan API key Anda sendiri.

## Lisensi

Proyek ini dilisensikan di bawah [MIT License](LICENSE).

---

Jika Anda menemukan masalah atau memiliki saran, jangan ragu untuk mengajukannya di halaman issue repositori ini.

