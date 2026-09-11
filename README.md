# Attendance Tracker

<div align="center">

A modern Android attendance management app built with **Kotlin**, **Jetpack Compose**, and **Material 3**, designed to help users track their attendance seamlessly.

[![Android](https://img.shields.io/badge/Android-5.0+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack-Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-black?style=for-the-badge)](LICENSE)

</div>

---

## <img src="https://img.icons8.com/color/24/000000/tasklist.png" alt="features"/> Features

- <img src="https://img.icons8.com/color/20/000000/analytics.png" alt="track"/> **Track Attendance** - Record daily attendance with ease
- <img src="https://img.icons8.com/color/20/000000/combo-chart.png" alt="analytics"/> **Analytics** - View attendance statistics and trends
- <img src="https://img.icons8.com/color/20/000000/target.png" alt="goals"/> **Attendance Goals** - Set and monitor attendance targets
- <img src="https://img.icons8.com/color/20/000000/mobile-phone.png" alt="modern"/> **Modern UI** - Built with Jetpack Compose and Material 3
- <img src="https://img.icons8.com/color/20/000000/lightning-bolt.png" alt="fast"/> **Fast & Responsive** - Smooth performance on all devices
- <img src="https://img.icons8.com/color/20/000000/database.png" alt="storage"/> **Local Storage** - All data stored locally (privacy-first)

---

## <img src="https://img.icons8.com/color/24/000000/settings.png" alt="tech"/> Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Kotlin | 2.0 | Primary Language |
| Jetpack Compose | Latest | Modern UI Framework |
| Material 3 | Latest | Design System |
| Room Database | Latest | Local Data Persistence |
| ViewModel | Latest | UI State Management |
| Coroutines | Latest | Asynchronous Operations |

---

## <img src="https://img.icons8.com/color/24/000000/folder-invoices.png" alt="structure"/> Project Structure

```
app/src/main/java/com/attendance/
├── database/          # Room Database setup
├── models/            # Data models
├── repository/        # Data layer
├── ui/
│   ├── screens/       # Application screens
│   ├── components/    # Reusable UI components
│   └── theme/         # Material 3 theming
└── viewmodel/         # State management
```

---

## <img src="https://img.icons8.com/color/24/000000/rocket.png" alt="start"/> Getting Started

### Prerequisites

- Android Studio Latest
- Android SDK 33+
- Kotlin 2.0+

### Installation

```bash
# Clone the repository
git clone https://github.com/swastik-chavan/Attendance-Tracker.git

cd Attendance-Tracker

# Open in Android Studio and sync Gradle dependencies
```

### Run the App

1. Open Android Studio
2. Select an emulator or connect a physical device
3. Click **Run** (or press Shift + F10)

---

## <img src="https://img.icons8.com/color/24/000000/book.png" alt="usage"/> How to Use

1. **Add Records** - Create new attendance entries with date and status
2. **View Dashboard** - See attendance summary and statistics
3. **Track Progress** - Monitor your attendance percentage
4. **Manage Data** - Edit or delete attendance records

---

## <img src="https://img.icons8.com/color/24/000000/architecture.png" alt="architecture"/> Architecture

This project follows **MVVM (Model-View-ViewModel)** architecture with Repository pattern for clean code separation.

```
User Interface (Compose)
        ↓
    ViewModel
        ↓
   Repository
        ↓
   Room Database
```

---

## <img src="https://img.icons8.com/color/24/000000/forward.png" alt="future"/> Future Enhancements

- <img src="https://img.icons8.com/color/20/000000/lock.png" alt="cloud"/> Cloud sync support
- <img src="https://img.icons8.com/color/20/000000/download.png" alt="export"/> Export attendance reports
- <img src="https://img.icons8.com/color/20/000000/bell.png" alt="reminder"/> Attendance reminders
- <img src="https://img.icons8.com/color/20/000000/phone.png" alt="widget"/> Widget support
- <img src="https://img.icons8.com/color/20/000000/globe.png" alt="lang"/> Multi-language support

---

## <img src="https://img.icons8.com/color/24/000000/document.png" alt="license"/> License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## <img src="https://img.icons8.com/color/24/000000/user.png" alt="author"/> Author

**Swastik Chavan**

- GitHub: [@swastik-chavan](https://github.com/swastik-chavan)
- YouTube: [@tech.x.0](https://www.youtube.com/@tech.x.0)

---

## <img src="https://img.icons8.com/color/24/000000/handshake.png" alt="contribute"/> Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

### How to Contribute

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

<div align="center">

### <img src="https://img.icons8.com/color/24/000000/star.png" alt="star"/> If you found this helpful, please give it a star!

Built with <img src="https://img.icons8.com/color/20/000000/heart.png" alt="love"/> using Kotlin • Jetpack Compose • Material 3

</div>
