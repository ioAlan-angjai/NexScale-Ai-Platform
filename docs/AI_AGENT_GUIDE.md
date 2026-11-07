# AI Coding Agent Guide — NexScale Authentication

## Mission

Bantu mengembangkan backend authentication NexScale AI Marketplace secara bertahap, akurat, aman, dan mudah dipelajari oleh developer.

## Mandatory first step: inspect, do not assume

Sebelum memberi solusi:
1. Baca `README.md`, `PRD.md`, `architecture.md`, `api-spec.md`, dan `implementation-plan.md`.
2. Inspeksi repository dan source code aktual.
3. Identifikasi file yang benar-benar ada.
4. Baca class terkait beserta test dan migration.
5. Bedakan fakta source code, temuan audit, dan usulan desain.

Jangan mengarang nama class, field, endpoint, package, atau migration.

## Default collaboration mode: manual implementation

Developer ingin memahami dan mengetik kode sendiri.

Karena itu, secara default:
- Jangan mengedit, membuat, menghapus, memindahkan, atau me-rename file.
- Jangan menjalankan formatter yang mengubah file.
- Jangan menjalankan migration yang mengubah database.
- Jangan melakukan commit atau push.
- Berikan instruksi dan kode lengkap untuk satu file pada satu waktu.
- Jelaskan lokasi file, tanggung jawab class, alur method, annotation penting, dan alasan desain.
- Tunggu developer mengetik dan menguji sebelum pindah ke file berikutnya.

Perubahan file hanya boleh dilakukan jika developer secara eksplisit mengubah instruksi dan meminta agent mengedit.

## Required response format

Untuk setiap task, jawab dengan urutan:

1. **Kondisi aktual** — file dan implementasi yang ditemukan.
2. **Gap** — perbedaan kondisi aktual dengan requirement.
3. **Rencana kecil** — file yang perlu disentuh dan urutannya.
4. **Kode** — satu file per langkah, lengkap, sesuai package dan import aktual.
5. **Penjelasan** — jelaskan class, method, annotation, alur data, dan keputusan.
6. **Test** — perintah yang dijalankan dan hasil yang diharapkan.
7. **Next step** — lanjut hanya setelah developer mengonfirmasi hasil.

## Coding constraints

- Pertahankan Spring Boot 3.4.3 dan struktur project yang ada kecuali ada alasan teknis kuat.
- Jangan upgrade dependency atau Java version tanpa persetujuan.
- `pom.xml` menyatakan target Java tertentu; runtime lokal yang pernah dilaporkan adalah JDK 24. Periksa konfigurasi aktual sebelum memberi instruksi versi.
- Gunakan DTO untuk request/response; jangan expose JPA Entity secara langsung.
- Gunakan BCrypt melalui PasswordEncoder yang dikonfigurasi Spring.
- Jangan menyimpan password plaintext.
- Jangan menaruh JWT secret di source code.
- Jangan menerima role ADMIN dari registrasi publik.
- Ambil identitas user dari principal/SecurityContext untuk endpoint `/me` dan ownership.
- Gunakan `@Transactional` pada batas transaksi service bila satu use case menyentuh beberapa perubahan data.
- Perubahan schema harus menggunakan Flyway migration baru. Jangan mengedit migration yang sudah diterapkan.
- Jangan menambah dependency jika fitur dapat diselesaikan dengan dependency yang tersedia.
- Jangan melakukan refactor tidak terkait.

## Test-first / test-alongside

Sebelum implementasi, periksa test yang sudah ada. Tambahkan atau usulkan test untuk:
- happy path;
- invalid input;
- duplicate data;
- unauthorized;
- forbidden;
- invalid/expired JWT;
- data ownership;
- transaction rollback jika relevan.

Setelah satu perubahan selesai, minta developer menjalankan test yang sesuai. Setelah rangkaian perubahan selesai, jalankan `mvn clean test` dari folder `backend`.

## Security review checklist

- [ ] Tidak ada plaintext password.
- [ ] Tidak ada password hash di response.
- [ ] Tidak ada secret di git.
- [ ] JWT signature dan expiry divalidasi.
- [ ] Public routes eksplisit.
- [ ] Private routes terlindungi.
- [ ] Role diperiksa di server.
- [ ] Ownership diperiksa di server.
- [ ] Error response tidak membocorkan stack trace.
- [ ] Test mencakup skenario negatif.

## Do not

- Jangan menyimpulkan fitur sudah selesai hanya karena file bernama mirip ditemukan.
- Jangan menganggap audit lama lebih benar daripada source code saat ini.
- Jangan menghapus test untuk membuat build hijau.
- Jangan mengubah kontrak API diam-diam.
- Jangan menulis ulang seluruh security configuration tanpa memahami filter chain.
- Jangan mengirim banyak file sekaligus jika developer sedang belajar satu per satu.
- Jangan menyatakan test lulus jika belum benar-benar dijalankan.

## First task prompt template

> Baca semua dokumentasi di folder ini. Inspeksi backend NexScale secara read-only untuk modul authentication. Jangan mengubah file apa pun. Periksa entity, repository, service, controller, DTO, security config, JWT filter/service, exception handler, Flyway migration, dan test. Buat laporan kondisi aktual vs PRD, tunjukkan path dan potongan kode yang relevan, tandai informasi yang belum dapat dipastikan, lalu rekomendasikan satu task terkecil berikutnya. Jangan mengarang file atau field.
