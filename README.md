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
(Add screenshots here if you have them to showcase backup, restore, search UI, etc.)

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
