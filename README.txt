==============================================================
 TripPlan - Perencana Perjalanan
 Proyek Tugas P10 - Pemrograman Mobile
==============================================================

IDENTITAS
  Nama          : Habibah
  NIM           : 2459201067
  Kelas         : 5A
  Program Studi : Sistem dan Teknologi Informasi
  Universitas   : Universitas Muhammadiyah Kotabumi
  Mata Kuliah   : Pemrograman Mobile
  Dosen         : Ryan Aji Wijaya, S.Kom., M.M.S.I.

STATUS PENTING
  - Folder ini berisi proyek Android Studio LENGKAP (source Java, layout XML,
    resource, konfigurasi Gradle).
  

CARA MEMBUKA DAN MENJALANKAN
  Kebutuhan: Android Studio (Hedgehog 2023.1.1 atau lebih baru), JDK 17
  (bawaan Android Studio), koneksi internet untuk unduh dependensi pertama kali.

  1. Ekstrak ZIP.
  2. Android Studio > File > Open > pilih folder "TripPlan_Habibah_P10".
  3. Tunggu "Gradle Sync" selesai (akan mengunduh Gradle 8.2 dan dependensi).
     Jika diminta, install Android SDK Platform 34.
  4. Jalankan di emulator/HP (Android 8.0 / API 26 ke atas): tombol Run.
  5. Membuat APK: Build > Build Bundle(s) / APK(s) > Build APK(s).
     Hasil: app/build/outputs/apk/debug/app-debug.apk

FITUR
  1.  Splash screen                      8.  Itinerary per hari dan jam
  2.  Dashboard (kartu ringkasan)        9.  Budget planner (anggaran/pengeluaran)
  3.  Daftar perjalanan                  10. Riwayat perjalanan
  4.  Tambah perjalanan                  11. Profil pengguna
  5.  Edit dan hapus perjalanan          12. Pengaturan aplikasi
  6.  Detail perjalanan (3 tab)          13. Tema terang dan gelap
  7.  Pengelolaan destinasi wisata       + Onboarding (nama dan mata uang)

SHAREDPREFERENCES  (PrefsManager.java)
  Fungsi simpan/ambil terpisah untuk: nama pengguna, email, tema gelap,
  mata uang, status onboarding, konfirmasi hapus, tampilan tips.
  Menyimpan dengan apply(); membaca dengan getSharedPreferences().
  Tema diterapkan di TripPlanApp.onCreate() dan MainActivity.onResume().
  Fragment Profil dan Pengaturan memuat ulang nilai di onResume().

SQLITE  (DatabaseHelper.java, SQLiteOpenHelper)
  Tabel: trips, destinations, itineraries, expenses (CRUD lengkap).
  Foreign key ON DELETE CASCADE: menghapus perjalanan ikut menghapus data
  turunannya.

CATATAN
  - Pilihan mata uang hanya mengubah simbol/format tampilan, tidak ada
    konversi kurs.
  - Perjalanan masuk Riwayat jika ditandai selesai atau tanggal selesainya
    sudah lewat.

DAFTAR UJI MANUAL (jalankan setelah build)
  [ ] Splash lalu onboarding muncul pertama kali; nama tampil di Beranda.
  [ ] Tambah perjalanan (validasi kosong dan tanggal terbalik berfungsi).
  [ ] Edit dan hapus perjalanan dari daftar dan dari halaman detail.
  [ ] Tambah/ubah/hapus destinasi dan itinerary (hari di luar rentang ditolak).
  [ ] Catat pengeluaran: ringkasan, progress, dan peringatan melebihi anggaran.
  [ ] Tandai selesai: perjalanan pindah ke Riwayat.
  [ ] Ubah tema gelap, mata uang, nama profil.
  [ ] Tutup paksa aplikasi lalu buka lagi: tema, nama, mata uang, dan seluruh
      data SQLite masih ada.

STRUKTUR
  app/src/main/java/com/habibah/tripplan/   source Java
  app/src/main/res/layout/                  layout XML
  app/src/main/res/                         drawable, values, menu, mipmap
  app/build.gradle, build.gradle, settings.gradle, gradle/wrapper/
