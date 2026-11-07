# Implementation Plan — Authentication

## Prinsip pengerjaan

- Kerjakan satu perubahan kecil pada satu waktu.
- Inspeksi source code sebelum mengusulkan perubahan.
- Pertahankan arsitektur dan konvensi project.
- Pahami setiap class, annotation, method, dan alur data sebelum mengetik kode.
- Jalankan test setelah satu bagian selesai.
- Jangan menganggap audit lama selalu sesuai dengan source code terbaru.

## Phase 0 — Baseline dan inspeksi

**Tujuan:** mengetahui kondisi autentikasi saat ini tanpa mengubah file.

Periksa:
- `pom.xml`
- User Entity, Repository, Service, Controller
- DTO registrasi dan login
- Auth Service dan Controller
- JWT service dan filter
- Security configuration
- PasswordEncoder configuration
- Global exception handler
- Flyway migration terkait user/profile
- Test autentikasi dan user

Output:
- Peta file aktual.
- Diagram alur aktual.
- Daftar fitur yang sudah ada.
- Gap antara implementasi dan PRD.
- Daftar risiko dan urutan perbaikan.

**Gate:** jangan coding sebelum laporan inspeksi disetujui.

## Phase 1 — Registration hardening

Target:
- Validasi field registrasi.
- Normalisasi email dengan aturan konsisten.
- Penanganan email duplikat.
- BCrypt hashing.
- Role allowlist; registrasi publik tidak dapat membuat ADMIN.
- Response aman.
- Transaction boundary yang benar.

Test minimum:
- Registrasi valid.
- Email duplikat.
- Email invalid.
- Password tidak memenuhi aturan.
- Role tidak diizinkan.
- Password hash tersimpan dan bukan plaintext.
- Response tidak membocorkan hash.

## Phase 2 — Login and JWT verification

Target:
- Verifikasi kredensial.
- JWT generation.
- Validasi signature dan expiry.
- Subject/claim konsisten.
- Respons error aman.

Test minimum:
- Login sukses.
- Password salah.
- User tidak ditemukan.
- Token valid.
- Token expired.
- Token signature invalid.
- Claim tidak valid.

## Phase 3 — Spring Security and current user

Target:
- Public route allowlist.
- Endpoint privat membutuhkan token.
- Principal masuk ke SecurityContext.
- `/api/users/me` menggunakan principal.
- Mapping role/authority konsisten.

Test minimum:
- Request publik tanpa token.
- Request privat tanpa token.
- Request dengan token valid.
- Token invalid.
- Role benar.
- Role salah.
- `/me` mengembalikan pengguna yang sesuai token.

## Phase 4 — Profile lifecycle (keputusan bisnis diperlukan)

Target:
- Tentukan apakah BUYER harus memiliki ClientProfile dan SELLER harus memiliki ProviderProfile.
- Verifikasi entity, repository, migration, foreign key.
- Buat profil bersama User dalam satu transaksi jika memang diwajibkan.

Test minimum:
- Buyer registration membuat profil yang benar.
- Seller registration membuat profil yang benar.
- Kegagalan pembuatan profil me-rollback user.
- Tidak ada profil ganda.
- Role tidak dapat dipasangkan dengan tipe profil yang salah.

## Phase 5 — Security regression

Target:
- Periksa semua endpoint domain yang sudah ada.
- Audit ownership untuk Portfolio, Experience, Proposal, dan domain lain.
- Pastikan user tidak dapat mengakses data user lain dengan mengganti ID.
- Tambahkan test regresi.

## Phase 6 — Release readiness

Checklist:
- [ ] `mvn clean test` lulus.
- [ ] Migration test lulus.
- [ ] Tidak ada secret dalam git diff.
- [ ] Error response tidak membocorkan detail internal.
- [ ] API docs diperbarui.
- [ ] README/config example diperbarui.
- [ ] Manual test login, register, `/me` berhasil.
- [ ] Perubahan dan risiko dicatat.

## Definition of Done

Satu phase selesai jika:
1. Requirement phase terpenuhi.
2. Implementasi sesuai arsitektur.
3. Test relevan ditambahkan atau diperbarui.
4. Seluruh test suite lulus.
5. Tidak ada perubahan tak terkait.
6. Developer dapat menjelaskan alur dan alasan desainnya.
