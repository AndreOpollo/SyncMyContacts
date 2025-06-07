# 📇 SyncMyContacts

SyncMyContacts is an Android app fully built with Kotlin and Jetpack Compose that allows users to effortlessly backup, restore, and export their contacts in XLS or VCF format. It features a single-screen UI with a built-in search bar for quickly filtering contacts.

## 🚀 Features
- 🔄 Local Backup & Restore of device contacts

- 📤 Export Contacts as:

   - 📄 .xls using Apache POI

   - 📇 .vcf (vCard) format

- 🔍 Search Functionality to quickly filter through contacts

- 🧑‍💻 Single-Screen UI using modern Material 3 design principles

- ✅ Full support for Scoped Storage on Android 10+

## 🛠 Tech Stack
| Layer       | Tools / Libraries                                                |
| ----------- | ---------------------------------------------------------------- |
| UI          | [Jetpack Compose](https://developer.android.com/jetpack/compose) |
| Theming     | [Material Theme ](https://m3.material.io/)                       |
| DI          | [Dagger Hilt](https://dagger.dev/hilt/)                          |
| Data Flow   | [Kotlin Flow](https://developer.android.com/kotlin/flow)         |
| XLS Support | [Apache POI](https://poi.apache.org/)                            |
| File Access | Scoped Storage & MediaStore APIs                                 |


## 📸 Screenshots


<img src="https://github.com/user-attachments/assets/9117166b-c9d8-41b1-a5c1-b4a9350936ed" width="300" alt="Screenshot 1A"/>
<img src="https://github.com/user-attachments/assets/a372b21e-38e6-427c-8bd0-2cf1f0b5e5d4" width="300" alt="Screenshot 1B"/>


<img src="https://github.com/user-attachments/assets/26387dc8-bcc6-46f4-9e1a-0edca0c07f1c" width="300" alt="Screenshot 2A"/>
<img src="https://github.com/user-attachments/assets/fe5029af-7a8c-4441-9bb6-0bd132a4b0d9" width="300" alt="Screenshot 2B"/>


<img src="https://github.com/user-attachments/assets/5f99d342-37af-49ca-b495-ce4002baaec4" width="300" alt="Screenshot 3A"/>
<img src="https://github.com/user-attachments/assets/bf9cdc42-863b-40c3-a7a0-58c7bfef3afa" width="300" alt="Screenshot 3B"/>


<img src="https://github.com/user-attachments/assets/f03e487f-81b4-4028-a441-0afe790e141a" width="300" alt="Screenshot 4A"/>
<img src="https://github.com/user-attachments/assets/c6c7d37f-ac44-48b0-a1e1-45cbdbcdbb0b" width="300" alt="Screenshot 4B"/>


<img src="https://github.com/user-attachments/assets/6f0e0157-c6df-4fb6-b695-b8d1444cd8d8" width="300" alt="Screenshot 5A"/>
<img src="https://github.com/user-attachments/assets/0dbf7eb9-a3cc-476a-8211-a688097f6bbb" width="300" alt="Screenshot 5B"/>


<img src="https://github.com/user-attachments/assets/6ecdf7b4-e2c7-44af-9873-cf6f3985e3ab" width="300" alt="Screenshot 6A"/>
<img src="https://github.com/user-attachments/assets/8aeee7df-ecad-420b-9cb4-2c3c6193e46e" width="300" alt="Screenshot 6B"/>



## 📦 How It Works
- Reads device contacts using ContactsContract.

- Backs up contacts locally as JSON for internal app use.

- Restores contacts via ContentProvider insertion.

- Exports contacts:

  - As .vcf using vCard formatting

  - As .xls using Apache POI's workbook and sheet APIs

All operations comply with scoped storage guidelines for Android 10+ and newer.

## 🔧 Setup & Installation
- Clone the repo

```bash
git clone https://github.com/yourusername/SyncMyContacts.git
```
- Open in Android Studio Hedgehog or later

- Run on an emulator or real device (API 29+ recommended)

- Grant contacts and storage permissions when prompted

## ✅ Permissions Required
- READ_CONTACTS

- WRITE_CONTACTS


- Uses MediaStore API and Storage Access Framework on Android 10+ for scoped storage compliance.
