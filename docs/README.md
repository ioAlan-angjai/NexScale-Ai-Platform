# NexScale AI Marketplace — Backend Authentication Documentation

Dokumentasi ini menjadi sumber acuan untuk memahami, merencanakan, mengimplementasikan, dan menguji modul autentikasi backend NexScale AI Marketplace.

## Isi folder

| Dokumen | Kegunaan |
|---|---|
| `PRD.md` | Tujuan produk, kebutuhan pengguna, ruang lingkup, requirement fungsional dan nonfungsional |
| `architecture.md` | Struktur arsitektur, komponen, alur autentikasi, dan batas tanggung jawab |
| `api-spec.md` | Kontrak endpoint autentikasi dan format request/response |
| `data-model.md` | Model data pengguna, role, password, dan token |
| `implementation-plan.md` | Urutan pengerjaan bertahap dan kriteria selesai |
| `testing-strategy.md` | Strategi unit, integration, security, dan regression test |
| `AI_AGENT_GUIDE.md` | Instruksi untuk AI coding agent agar bekerja konsisten dan tidak mengarang struktur project |

## Konteks teknis yang diketahui

- Backend: Spring Boot 3.4.3
- Bahasa: Java
- Build: Maven
- Database: PostgreSQL (Supabase)
- Database migration: Flyway, migrasi yang telah dilaporkan sampai V6
- Package root: `com.nexscale.api`
- Security: Spring Security
- Password hashing: BCrypt
- Token: JWT menggunakan JJWT
- Test terakhir yang dijalankan pengguna: `mvn clean test` — 70 tests, 0 failures, 0 errors.

**Catatan penting:** hasil 70 test adalah kondisi pada saat pengujian terakhir, bukan jaminan bahwa semua perubahan berikutnya tetap lolos. Jalankan ulang test setelah setiap perubahan.

## Cara menggunakan dokumen

1. Letakkan folder ini di root repository atau salin isinya ke folder `docs/`.
2. Minta AI agent membaca `README.md`, `PRD.md`, `architecture.md`, dan `AI_AGENT_GUIDE.md` sebelum menyentuh kode.
3. Minta agent menginspeksi implementasi aktual. Dokumen ini adalah target desain, bukan bukti bahwa semua fitur sudah tersedia.
4. Implementasikan satu tahap kecil, pahami kode, lalu jalankan test sebelum melanjutkan.

## Batas akurasi

Dokumen dibuat dari konteks dan hasil audit yang tersedia. Nama class, field, konfigurasi, endpoint tambahan, dan relasi database wajib diverifikasi terhadap source code serta migration aktual sebelum implementasi. Jangan membuat migration atau mengubah kontrak API hanya berdasarkan asumsi.
