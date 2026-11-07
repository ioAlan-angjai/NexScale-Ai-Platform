# API Specification — Authentication

**Base path:** `/api`  
**Status:** Draft berdasarkan endpoint yang telah dilaporkan. Verifikasi mapping dan DTO aktual sebelum menganggap dokumen ini sebagai kontrak final.

## 1. Endpoint yang diketahui

| Method | Path | Akses | Tujuan |
|---|---|---|---|
| POST | `/api/auth/login` | Public | Login |
| POST | `/api/users` | Public | Registrasi |
| GET | `/api/users/me` | Authenticated | Mendapatkan identitas pengguna aktif |
| GET | `/api/health` | Public | Health check |

Endpoint dan format payload di bawah adalah kontrak konseptual. Pertahankan format aktual apabila sudah digunakan frontend atau client lain.

## 2. Register

`POST /api/users`

Contoh request konseptual:
```json
{
  "name": "Example User",
  "email": "user@example.com",
  "password": "StrongPassword123",
  "role": "BUYER"
}
```

Contoh response konseptual:
```json
{
  "id": 123,
  "name": "Example User",
  "email": "user@example.com",
  "role": "BUYER"
}
```

Ketentuan:
- Jangan pernah mengembalikan `password` atau `passwordHash`.
- Email harus unik.
- Role publik harus divalidasi allowlist.
- `ADMIN` tidak boleh dibuat melalui endpoint registrasi publik.
- Field sebenarnya harus mengikuti DTO aktual.

## 3. Login

`POST /api/auth/login`

Contoh request konseptual:
```json
{
  "email": "user@example.com",
  "password": "StrongPassword123"
}
```

Contoh response konseptual:
```json
{
  "accessToken": "<jwt>",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

Nama field response dan satuan `expiresIn` harus disesuaikan dengan implementasi aktual.

## 4. Get current user

`GET /api/users/me`

Header:
```http
Authorization: Bearer <jwt>
```

Contoh response konseptual:
```json
{
  "id": 123,
  "name": "Example User",
  "email": "user@example.com",
  "role": "BUYER"
}
```

ID pengguna harus berasal dari principal yang tervalidasi, bukan query parameter atau body yang dikirim client.

## 5. Error semantics

| Status | Makna | Contoh |
|---|---|---|
| 400 | Request tidak valid | Field wajib kosong atau format email invalid |
| 401 | Belum terautentikasi | Token tidak ada, invalid, atau expired |
| 403 | Tidak memiliki izin | Role tidak memenuhi aturan endpoint |
| 404 | Resource tidak ditemukan | User/resource tidak ditemukan sesuai kebijakan |
| 409 | Konflik data | Email sudah digunakan, jika pola API memilih status ini |
| 500 | Kesalahan internal | Error tak terduga; jangan bocorkan detail internal |

Gunakan format error global yang sudah ada. Jangan memperkenalkan format baru tanpa memeriksa konsistensi seluruh API.

## 6. Example curl

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"StrongPassword123"}'
```

```bash
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <jwt>"
```

## 7. Contract verification checklist

- [ ] Path dan HTTP method cocok dengan Controller aktual.
- [ ] Request DTO dan nama field cocok.
- [ ] Response DTO cocok dan tidak membocorkan data sensitif.
- [ ] Status code sesuai implementasi dan test.
- [ ] Endpoint public/private cocok dengan SecurityConfig.
- [ ] Contoh token tidak menggunakan secret atau token sungguhan.
