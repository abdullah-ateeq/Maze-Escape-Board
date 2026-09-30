# Maze Escape Board 🧩🏃‍♂️

**Maze Escape Board** is an immersive, feature-rich Android puzzle game built from the ground up using **Kotlin**, **Jetpack Compose**, and **Material 3**. Test your problem-solving skills as you navigate complex handcrafted mazes, optimize your moves, and beat your best times!

---

## 🌟 Features

- **🧩 Engaging Puzzle Mechanics**: Navigate through challenging handcrafted mazes with intuitive controls.
- **🎨 Modern Material 3 UI**: Clean, responsive, and adaptive user interface built entirely with Jetpack Compose.
- **🔊 Audio & Tactile Feedback**: Integrated sound effects and haptic feedback responses for an immersive gameplay experience.
- **⚙️ Persistent Preferences**: Settings management powered by **Jetpack DataStore** to customize sound, haptics, and gameplay options.
- **📊 Performance Tracking**: Detailed end-game statistics including completion time, move counts, and scoring summaries.
- **🛡️ Robust Testing**: Comprehensive unit testing suite covering maze rules, player movement logic, and game state transitions.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/) (100%)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & [Material 3](https://m3.material.io/)
- **Architecture**: MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF)
- **Navigation**: Jetpack Navigation Compose
- **Persistence**: Jetpack DataStore Preferences
- **Concurrency**: Kotlin Coroutines & Flow
- **Testing**: JUnit 4, AndroidX Test (JUnit & Espresso)

---

## 📂 Project Structure

```text
com.example.mazeescapeboard
├── data/                 # Data layer (SettingsRepository, Sound & Haptic managers)
├── model/                # Core domain models (GridPosition, Direction, MazeTile, HandcraftedMaze, MazeEscapeRules)
├── ui/
│   ├── components/       # Reusable UI components (MazeBoard, StatCard, TopHud)
│   ├── navigation/       # Navigation routes and graph definitions
│   ├── screens/          # App screens (Splash, Home, Game, Result, HowToPlay, Settings, About)
│   ├── theme/            # Material 3 Theme, Colors, and Typography
│   └── viewmodel/        # ViewModels managing UI state (GameViewModel, SettingsViewModel)
└── MainActivity.kt       # Single Activity entry point
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Koala or newer recommended)
- **Android SDK** (Compile SDK 37, Min SDK 24)
- JDK 11 or higher

### Building and Running
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/Maze-Escape-Board.git
   ```
2. Open the project folder in Android Studio.
3. Sync Gradle and run the app on an Android emulator or physical device.

---

## 🧪 Running Tests

Run unit tests via Gradle:
```bash
./gradlew test
```

---

## 👤 Author

**Abdullah Ateeq**
- GitHub: [@abdullah-ateeq](https://github.com/abdullah-ateeq)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
