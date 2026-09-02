<p align="center">
  <img src="playstore_icon_512.png" alt="CalcMate Icon" width="128" height="128" style="border-radius: 28px;" />
</p>

<h1 align="center">CalcMate — All-in-One Smart Calculator</h1>

<p align="center">
  <b>A modern, high-performance Android calculator suite engineered with Jetpack Compose & Material 3.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Kotlin-2.0+-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Design-Material%203-6750A4?logo=materialdesign&logoColor=white" alt="Material 3" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-informational" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-success" alt="Target SDK" />
</p>

---

<p align="center">
  <img src="playstore_feature_graphic_1024x500.png" alt="CalcMate Feature Graphic" width="100%" />
</p>

---

## ✨ Features

### 🧮 1. Smart & Scientific Calculator
* **Live Calculation Preview**: Evaluates expressions in real time as you type before hitting `=`.
* **Ergonomic Tactile Keypad**: Spring-animated touch micro-interactions and haptic feedback.
* **Collapsible Scientific Tray**: Trigonometry (`sin`, `cos`, `tan`, inverse), logarithms (`ln`, `log`), powers (`x²`, `xʸ`), roots (`√`), factorials (`!`), and constants (`π`, `e`).
* **Calculation History**: Instantly view, copy, or delete individual previous calculations.
* **Smart Input Safeguards**: Automatic operator replacement and duplicate decimal suppression.

### 💼 2. Finance & Business Suite
* **Loan & EMI Calculator**: Monthly installments, interest breakdown, and total repayment payoff.
* **Compound Interest**: Visualize compounding wealth and monthly contributions over time.
* **Savings Goal**: Determine exact months required to achieve savings milestones.
* **Discount & Sales Tax**: Real-time sales discount and tax calculations.
* **Profit & Margin**: Gross profit, profit margin %, and markup % analysis.
* **Tip & Bill Split**: Effortlessly split restaurant bills with custom tips and people steppers.

### 📐 3. Converters & Daily Tools
* **Universal Unit Converter**: Length, Mass, Temperature, Area, Volume, Speed, and Time with bi-directional swapping.
* **Digital Storage**: Convert Bytes, KB, MB, GB, TB, and PB in both Binary (1024) and Decimal (1000) base.
* **Age & Countdown**: Exact age in years, months, days, total days, and birthday countdown.
* **Date & Time Calculators**: Span between dates and precise interval duration.
* **Fuel & Electricity Usage**: Trip gas cost estimations and kilowatt-hour consumption costs.

### 🎨 4. Customization & Privacy
* **OLED Dark Mode & Crisp Light Mode**: Dynamic Material 3 palettes.
* **Configurable Decimal Precision**: Adjust precision from 0 to 12 decimal places.
* **Offline & Private**: All calculations and history stay 100% local on the device.

---

## 🛠️ Tech Stack & Architecture

* **Language**: [Kotlin](https://kotlinlang.org/)
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3
* **Architecture**: Clean Architecture / Modular UI separation
* **State & Persistence**: Kotlin Coroutines, StateFlow, Android Preferences DataStore
* **Native Splash**: Android 12+ SplashScreen API (`androidx.core.splashscreen`)
* **Monetization**: Google AdMob (Adaptive Banners, Native Advanced, Preloaded Interstitials)

---

## 🚀 Getting Started & Building

### Prerequisites
* JDK 17 or 21
* Android SDK (API 36)

### Clone & Build
```bash
git clone https://github.com/Midrey7/CalcMate.git
cd CalcMate

# Run unit tests
./gradlew test

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

Generated APKs will be located at:
* `app/build/outputs/apk/debug/app-debug.apk`
* `app/build/outputs/apk/release/app-release-unsigned.apk`

---

## 📄 Documentation & Links
* [Play Store Listing Guide](PLAYSTORE_LISTING.md)
* [Privacy Policy](PRIVACY_POLICY.md)
