# Graion 📐

A fast, modern, and private offline engineering calculator and wire drawing schedule toolbox for Android, built 100% with Jetpack Compose, Kotlin Coroutines, Room, and Material 3.

---

## ✨ Features

- **Scientific Calculator:**
  - High-precision arithmetic backed by Java `BigDecimal` (`MathContext.DECIMAL128`).
  - Trigonometric functions (`sin`, `cos`, `tan`) with clean degree conversions and undefined angle handling.
  - Natural logarithm (`ln`), base-10 logarithm (`log`), square root (`√`), power (`^`), and constants ($\pi$, $e$).
  - Percentage arithmetic adhering to calculator conventions (e.g. `200 + 10% = 220`).
  - Memory registers ($M+$, $M-$, $MR$, $MC$) and repeated equals operations.
  - Live preview of expression evaluations as you type.

- **Wire Drawing Tool:**
  - Area reduction, elongation, and reduction ratio calculations across die passes.
  - Automatic intermediate die sequence generation for target elongations.
  - Quality and consistency rating based on pass-to-pass deviation.
  - CSV export and persistent saved schedules.

- **Unit Converter:**
  - Comprehensive conversions across 6 categories: Length, Mass, Area, Volume, Temperature, and Speed.

- **Privacy First & 100% Offline:**
  - Zero internet permission (`android.permission.INTERNET` is not requested).
  - All history and preferences stored locally via Room and Jetpack DataStore.

---

## 🛠 Tech Stack & Architecture

- **Language:** Kotlin 2.3
- **UI Framework:** Jetpack Compose (Material 3)
- **Navigation:** AndroidX Navigation 3 (`NavDisplay`, type-safe `NavKey`)
- **Persistence:** AndroidX Room 2.8 (`CalculationDao`, `WireDrawScheduleDao`) & Jetpack DataStore Preferences
- **Architecture:** Clean Architecture with pure domain engines (`CalculatorEngine`, `ExpressionParser`, `ExpressionTokenizer`, `WireDrawingCalculatorEngine`, `UnitConverterEngine`) decoupled from Android framework APIs.

---

## 🚀 Building & Running

### Prerequisites
- Android Studio Ladybug / Meerkat or newer
- JDK 17+
- Android SDK (API 36 compileSdk, minSdk 26)

### Command Line
```bash
# Run unit tests
./gradlew test

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

---

## 📄 License & Privacy
See [PRIVACY_POLICY.md](PRIVACY_POLICY.md) for data privacy practices.
