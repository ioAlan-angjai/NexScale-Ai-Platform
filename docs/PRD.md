# Product Requirements Document (PRD)
## Modul Autentikasi & Akses Pengguna — NexScale AI Marketplace

**Status:** Draft untuk implementasi bertahap  
**Produk:** NexScale AI Marketplace  
**Modul:** Identity, Authentication, Authorization  
**Backend:** Spring Boot 3.4.3, Java, Maven, PostgreSQL, Spring Security, JWT, BCrypt

---

## 1. Ringkasan produk

Modul autentikasi bertanggung jawab memastikan bahwa pengguna NexScale AI Marketplace dapat membuat akun, masuk ke sistem, dan mengakses fitur sesuai identitas serta role yang dimilikinya.

Marketplace memiliki tiga role utama:
- `BUYER` — pengguna yang memublikasikan kebutuhan/proyek dan mencari penyedia layanan.
- `SELLER` — penyedia layanan yang membuat profil profesional, portofolio, dan mengirim proposal.
- `ADMIN` — pengelola platform dengan akses administratif.

Autentikasi menjawab **siapa pengguna tersebut**. Otorisasi menjawab **apa yang boleh dilakukan pengguna tersebut**.

## 2. Masalah yang ingin diselesaikan

Tanpa autentikasi dan otorisasi yang konsisten:
- identitas pengguna tidak dapat dipercaya;
- password berisiko tersimpan atau diproses secara tidak aman;
- endpoint privat dapat diakses tanpa token;
- pengguna dapat mencoba mengakses data milik pengguna lain;
- fitur berbasis role tidak dapat ditegakkan secara konsisten.

## 3. Tujuan dan indikator keberhasilan

### Tujuan
1. Pengguna dapat mendaftar dengan data yang valid.
2. Pengguna dapat login menggunakan kredensial yang benar.
3. Password tidak pernah disimpan sebagai plaintext.
4. Endpoint privat hanya dapat diakses dengan JWT yang valid.
5. Akses administratif dibatasi untuk role `ADMIN`.
6. Identitas pengguna pada endpoint `/me` berasal dari konteks autentikasi, bukan ID yang dikirim bebas oleh client.
7. Perilaku autentikasi dan otorisasi dapat diuji otomatis.

### Indikator keberhasilan
- Password di database berupa hash BCrypt.
- Login valid menghasilkan token yang dapat digunakan untuk endpoint privat.
- Login dengan password salah ditolak.
- Token hilang, malformed, expired, atau signature tidak valid ditolak pada endpoint privat.
- Endpoint publik tetap dapat diakses tanpa token.
- Role yang tidak berwenang menerima respons `403 Forbidden`.
- Tidak ada test regression setelah implementasi; seluruh test suite lulus.

## 4. Persona dan kebutuhan pengguna

### Buyer / Seller
- Sebagai pengguna, saya ingin mendaftar agar dapat menggunakan marketplace.
- Sebagai pengguna, saya ingin login agar dapat mengakses akun saya.
- Sebagai pengguna, saya ingin melihat identitas akun aktif melalui endpoint profil diri.
- Sebagai pengguna, saya tidak ingin password asli saya tersimpan di database.

### Admin
- Sebagai admin, saya ingin endpoint administratif hanya dapat diakses oleh akun dengan role `ADMIN`.

### Developer
- Sebagai developer, saya ingin alur autentikasi terpisah dari logika domain marketplace.
- Sebagai developer, saya ingin token dan aturan akses dapat diuji secara otomatis.

## 5. Ruang lingkup

### In scope
- Registrasi akun dengan validasi input.
- Hashing password menggunakan BCrypt.
- Login dan verifikasi kredensial.
- Pembuatan dan validasi JWT.
- Filter autentikasi JWT pada Spring Security.
- Otorisasi berbasis role.
- Endpoint identitas pengguna aktif (`/api/users/me`).
- Penanganan error autentikasi dan akses ditolak.
- Test untuk skenario sukses, gagal, dan batas keamanan.

### Out of scope untuk tahap ini
- OAuth Google atau social login.
- MFA/OTP.
- Forgot password dan reset password via email.
- Refresh-token rotation, kecuali fitur ini memang sudah ada dan diverifikasi di source code.
- Manajemen sesi lintas perangkat.
- Frontend login/register.
- Pembuatan lengkap ClientProfile dan ProviderProfile, kecuali dijadikan tahap terpisah setelah struktur aktual diverifikasi.

## 6. Functional requirements

| ID | Requirement | Prioritas |
|---|---|---|
| FR-01 | Sistem menerima registrasi dengan field wajib yang valid | Must |
| FR-02 | Sistem menolak email yang sudah digunakan | Must |
| FR-03 | Sistem menyimpan password dalam bentuk BCrypt hash | Must |
| FR-04 | Sistem tidak mengembalikan password/hash dalam response | Must |
| FR-05 | Sistem memverifikasi email dan password saat login | Must |
| FR-06 | Sistem menerbitkan JWT setelah login berhasil | Must |
| FR-07 | Sistem memvalidasi signature dan masa berlaku JWT | Must |
| FR-08 | Sistem mengisi SecurityContext dengan identitas dan authorities pengguna | Must |
| FR-09 | Endpoint privat menolak request tanpa token valid | Must |
| FR-10 | Endpoint berbasis role membatasi akses sesuai role | Must |
| FR-11 | Pengguna dapat mengambil data dirinya melalui `/api/users/me` | Must |
| FR-12 | Error autentikasi dan otorisasi memiliki format konsisten | Should |
| FR-13 | Data profil spesifik role dibuat sesuai aturan bisnis yang disepakati | Should, tahap lanjutan |

## 7. Non-functional requirements

### Security
- Password hanya disimpan menggunakan password encoder yang kuat (BCrypt).
- Secret JWT tidak boleh ditulis langsung di source code atau commit.
- Token tidak boleh dicatat ke log.
- Validasi JWT harus memeriksa signature dan expiry.
- Endpoint sensitif harus menggunakan prinsip least privilege.
- Identitas pemilik data harus berasal dari principal terautentikasi.
- Error login tidak perlu membocorkan apakah email terdaftar atau password yang salah.

### Maintainability
- Ikuti arsitektur dan konvensi package yang sudah ada.
- Pisahkan Controller, Service, Repository, Entity, DTO, dan konfigurasi Security.
- Hindari duplikasi aturan otorisasi.
- Perubahan database dilakukan melalui Flyway, bukan perubahan manual yang tidak terdokumentasi.

### Reliability
- Operasi registrasi yang menyentuh beberapa tabel harus transaksional.
- Constraint database tetap menjadi lapisan perlindungan terhadap duplikasi.
- Test harus dapat dijalankan melalui Maven.

## 8. Business rules

1. Email harus unik setelah normalisasi yang konsisten.
2. Password tidak boleh disimpan atau dikembalikan dalam bentuk plaintext.
3. Role yang diterima saat registrasi harus dibatasi pada role yang memang boleh dipilih publik. `ADMIN` tidak boleh dapat dibuat melalui registrasi publik.
4. Role pada token harus berasal dari data akun yang dipercaya di server.
5. Pengguna hanya boleh membaca atau mengubah data miliknya, kecuali admin memiliki izin eksplisit.
6. Jangan percaya `userId`, `role`, atau `ownerId` dari request body untuk menentukan hak akses.
7. Jika profil Buyer/Seller wajib dibuat saat registrasi, pembuatan user dan profil harus berhasil atau gagal sebagai satu transaksi.

## 9. Acceptance criteria

### Registrasi
- [ ] Request valid membuat satu akun.
- [ ] Password tersimpan sebagai BCrypt hash.
- [ ] Email duplikat ditolak dengan status yang sesuai.
- [ ] Role publik yang tidak diizinkan ditolak.
- [ ] Response tidak mengandung password maupun password hash.
- [ ] Jika pembuatan profil termasuk tahap implementasi, profil yang sesuai dibuat dalam transaksi yang sama.

### Login
- [ ] Kredensial valid menghasilkan JWT.
- [ ] Password salah ditolak.
- [ ] Akun tidak ditemukan ditolak dengan respons aman.
- [ ] Token memiliki subject/claim yang sesuai desain dan expiry.

### Authorization
- [ ] Endpoint privat tanpa token menghasilkan `401`.
- [ ] Token invalid/expired menghasilkan `401`.
- [ ] Role yang tidak sesuai menghasilkan `403`.
- [ ] `/api/users/me` mengembalikan akun yang berasal dari principal aktif.

### Quality
- [ ] Unit test dan integration test relevan lulus.
- [ ] `mvn clean test` lulus.
- [ ] Tidak ada secret di repository.
- [ ] Dokumentasi endpoint dan konfigurasi diperbarui.

## 10. Risiko dan pertanyaan yang harus diverifikasi

- Apakah login menggunakan email atau username?
- Apakah token saat ini memuat role sebagai claim atau role dibaca ulang dari database?
- Apakah refresh token sudah diimplementasikan?
- Apakah role Buyer/Seller wajib memiliki profil langsung saat registrasi?
- Apakah email dinormalisasi sebelum pengecekan keunikan?
- Apakah semua endpoint domain sudah memeriksa ownership?
- Apakah JWT secret dan expiry sudah dikonfigurasi melalui environment variable?

Jangan menjawab pertanyaan ini dengan asumsi. Periksa kode dan migration aktual.
