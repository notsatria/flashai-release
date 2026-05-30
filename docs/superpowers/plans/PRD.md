# PRD — AI Flashcard App
**Workshop: Build & Launch Android App with Kotlin + AI**
**Version:** 1.0 | **Status:** Workshop Prebuilt Scope

---

## 1. Overview

Aplikasi flashcard berbasis Android yang memungkinkan pengguna membuat dan belajar dari kartu soal secara manual maupun otomatis menggunakan AI (Gemini). Dibangun dengan Kotlin + Jetpack Compose + Firebase Firestore.

**Tujuan workshop:** Mendemonstrasikan workflow mobile developer modern — dari setup project, arsitektur clean, integrasi cloud database, hingga integrasi AI API — dalam format yang bisa diselesaikan dalam 2 hari.

---

## 2. Target Pengguna

Mahasiswa dan pelajar yang ingin belajar materi lebih efisien menggunakan metode flashcard.

---

## 3. Scope

### ✅ In Scope (Prebuilt + Live Coding Workshop)
- Membuat dan mengelola Deck (kumpulan flashcard)
- Membuat flashcard secara manual (pertanyaan + jawaban)
- Generate flashcard otomatis dari topik menggunakan Gemini AI
- Study Mode: flip card satu per satu
- Simpan data ke Firebase Firestore

### ❌ Out of Scope (Tidak dikerjakan di workshop)
- Login / autentikasi pengguna (gunakan anonymous user)
- Statistik belajar (correct/incorrect tracking)
- Spaced repetition algorithm
- Notifikasi / reminder
- Export atau share flashcard
- Offline mode / caching

---

## 4. User Stories

### Deck Management
- Sebagai pengguna, saya ingin membuat deck baru dengan nama dan deskripsi singkat
- Sebagai pengguna, saya ingin melihat daftar semua deck yang sudah saya buat
- Sebagai pengguna, saya ingin menghapus deck yang tidak diperlukan

### Card Management
- Sebagai pengguna, saya ingin menambah flashcard secara manual ke dalam sebuah deck
- Sebagai pengguna, saya ingin melihat daftar semua kartu dalam sebuah deck
- Sebagai pengguna, saya ingin menghapus kartu dari sebuah deck

### AI Generate
- Sebagai pengguna, saya ingin mengetik topik tertentu dan mendapatkan flashcard yang di-generate otomatis oleh AI
- Sebagai pengguna, saya ingin memilih berapa kartu yang ingin di-generate (5 / 10)
- Sebagai pengguna, saya ingin menyimpan hasil generate AI ke dalam deck yang dipilih

### Study Mode
- Sebagai pengguna, saya ingin membuka deck dan belajar kartu satu per satu
- Sebagai pengguna, saya ingin men-tap kartu untuk membalik dan melihat jawaban
- Sebagai pengguna, saya ingin navigasi ke kartu berikutnya atau sebelumnya

---

## 5. Functional Requirements

| ID | Fitur | Prioritas |
|----|-------|-----------|
| F01 | Buat deck baru (nama + deskripsi) | Wajib |
| F02 | Lihat daftar deck | Wajib |
| F03 | Hapus deck | Wajib |
| F04 | Tambah kartu manual (Q + A) ke deck | Wajib |
| F05 | Lihat daftar kartu dalam deck | Wajib |
| F06 | Hapus kartu | Wajib |
| F07 | Input topik → Generate kartu via Gemini API | Wajib |
| F08 | Pilih jumlah kartu yang di-generate (5/10) | Opsional |
| F09 | Simpan hasil generate ke deck | Wajib |
| F10 | Study Mode: flip card, navigasi prev/next | Wajib |

---

## 6. Non-Functional Requirements

- **Performa:** Respon UI < 300ms untuk interaksi lokal; Gemini API response ditampilkan dengan loading state
- **Offline:** Aplikasi tidak wajib offline-first; koneksi internet dibutuhkan untuk AI generate dan Firestore sync
- **Kompatibilitas:** Minimum Android API 26 (Android 8.0)
- **UI:** Menggunakan Material3 default — tidak ada custom design system
- **Arsitektur:** MVVM dengan Repository pattern

---

## 7. Tech Stack

| Layer | Teknologi |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM + Repository |
| Database | Firebase Firestore |
| Auth | Firebase Anonymous Auth |
| AI | Gemini API (gemini-1.5-flash) |
| DI | Manual DI (tanpa Hilt, untuk simplisitas workshop) |
| Build | Gradle (Kotlin DSL) |

---

## 8. Data Model

### Deck
```
id: String (Firestore document ID)
name: String
description: String
cardCount: Int
createdAt: Timestamp
userId: String (anonymous UID)
```

### Card
```
id: String (Firestore document ID)
deckId: String
question: String
answer: String
createdAt: Timestamp
```

---

## 9. Struktur Screen

```
HomeScreen (Deck List)
  └── DeckDetailScreen (Card List)
        ├── AddCardScreen (Manual input)
        ├── GenerateAIScreen (AI generate)
        └── StudyModeScreen (Flip card)
```

---

## 10. Pembagian Prebuilt vs Live Coding

### Prebuilt (sudah jadi sebelum workshop)
- Setup project, Gradle dependencies, package structure
- Firebase setup (google-services.json, Firestore rules)
- Repository layer (DeckRepository, CardRepository)
- ViewModel boilerplate
- Navigation graph
- Komponen UI reusable (CardItem, DeckItem, LoadingIndicator)
- Gemini API service class

### Live Coding (dikerjakan bersama peserta)
- HomeScreen UI + ViewModel logic
- DeckDetailScreen UI
- AddCardScreen dengan form input
- GenerateAIScreen: trigger API call + tampilkan hasil
- StudyModeScreen dengan animasi flip card

---

## 11. Risiko & Mitigasi

| Risiko | Kemungkinan | Mitigasi |
|--------|-------------|----------|
| Gemini API gagal saat demo | Sedang | Siapkan mock response sebagai fallback |
| Koneksi internet buruk | Sedang | Gunakan data mobile sebagai hotspot cadangan |
| Peserta tertinggal di live coding | Tinggi | Sediakan checkpoint branch di GitHub per segmen |
| Waktu habis sebelum Study Mode | Sedang | Study Mode dikerjakan terakhir, bisa di-skip jika waktu kurang |