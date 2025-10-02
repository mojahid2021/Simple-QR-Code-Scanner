# Advanced QR Code Scanner

A feature-rich Android QR Code and Barcode scanner application with advanced capabilities for scanning, generating, and managing QR codes.

## 📱 Features

### Scanning Capabilities
- **Multi-format Support**: Scan QR codes, barcodes, and various code formats
- **Smart Detection**: Automatically detects and handles different data types:
  - URLs (with auto-open option)
  - Phone numbers (direct dial)
  - Email addresses (compose email)
  - SMS messages
  - WiFi credentials (auto-connect)
  - Geographic locations (open in maps)
  - Contact information (vCard)
  - Calendar events
  - Driver licenses
  - ISBN codes
  - Product barcodes
  
### Camera Controls
- **Flashlight Toggle**: Turn on/off flashlight while scanning
- **Camera Switch**: Toggle between front and back cameras
- **Rescan**: Quick rescan without restarting the app
- **Auto-focus**: Intelligent focusing for better scan accuracy

### QR Code Generation
- Generate QR codes for multiple data types:
  - Plain Text
  - URLs
  - WiFi Credentials (SSID, password, encryption type)
  - Contact Information (vCard format)
  - Email addresses
  - Phone numbers
- **Save & Share**: Save generated QR codes to device gallery
- **High Quality**: Generated codes are saved in PNG format

### History Management
- **Complete History**: All scans are automatically saved
- **Search Functionality**: Quick search through scan history
- **Export Options**:
  - Export as CSV
  - Export as JSON
- **Delete**: Individual or bulk delete options
- **Clear All**: Remove all history with confirmation

### Settings & Customization
- **Dark Mode**: Toggle between light and dark themes
- **Scan Feedback**:
  - Vibration on successful scan (toggleable)
  - Sound notification (toggleable)
- **Behavior Settings**:
  - Auto-open URLs
  - Auto-copy to clipboard
- **Persistent Preferences**: Settings saved across app sessions

## 🚀 Getting Started

### Prerequisites
- Android device running Android 7.0 (API 24) or higher
- Camera permission required for scanning

### Installation
1. Clone this repository
```bash
git clone https://github.com/mojahid2021/Simple-QR-Code-Scanner.git
```

2. Open the project in Android Studio

3. Build and run the application

### Permissions
The app requires the following permissions:
- **Camera**: For scanning QR codes
- **Vibrate**: For haptic feedback on scan
- **Storage**: For saving generated QR codes and export functionality
- **WiFi State**: For WiFi auto-connect feature

## 📖 Usage Guide

### Scanning a QR Code
1. Launch the app
2. Point camera at QR code
3. App automatically scans and processes the code
4. Tap "Rescan" to scan another code

### Generating a QR Code
1. Tap "Generate QR Code" button
2. Select the type of QR code you want to create
3. Fill in the required information
4. Tap "Generate QR Code"
5. Tap "Save QR Code" to save to gallery

### Managing History
1. Tap "View History" button
2. Use search bar to find specific scans
3. Tap on any entry to copy and open
4. Use delete button to remove individual entries
5. Export history as CSV or JSON
6. Clear all history with "Clear All" button

### Adjusting Settings
1. Tap "Settings" button
2. Toggle preferences as needed:
   - Dark Mode
   - Vibration feedback
   - Sound feedback
   - Auto-open URLs
   - Auto-copy to clipboard

## 🛠️ Technical Details

### Technologies Used
- **Language**: Java
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 35
- **Libraries**:
  - CameraX for camera functionality
  - ML Kit for barcode scanning
  - Room Database for data persistence
  - ZXing for QR code generation
  - AndroidX components

### Architecture
- MVVM-like pattern with Activities
- Room Database for local storage
- Shared Preferences for settings
- Asynchronous operations with Threads and ExecutorService

## 📂 Project Structure
```
app/
├── src/main/
│   ├── java/com/mojahid/simple_qr_code_scanner/
│   │   ├── MainActivity.java           # Main scanner screen
│   │   ├── GenerateQRActivity.java     # QR code generation
│   │   ├── HistoryActivity.java        # Scan history management
│   │   ├── SettingsActivity.java       # App settings
│   │   └── history/
│   │       ├── ScanDatabase.java       # Room database
│   │       ├── ScanHistory.java        # Data model
│   │       ├── ScanHistoryDao.java     # Database operations
│   │       └── ScanHistoryAdapter.java # RecyclerView adapter
│   └── res/
│       ├── layout/                      # XML layouts
│       └── values/                      # Resources
```

## 🎨 UI/UX Features
- Clean, intuitive interface
- Material Design components
- Responsive layouts
- Dark mode support
- Clear visual feedback
- Confirmation dialogs for critical actions

## 🔒 Privacy & Security
- All data stored locally on device
- No internet connection required for core functionality
- No data collection or analytics
- User controls all data (export, delete, clear)

## 🤝 Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License
This project is open source and available under the MIT License.

## 👨‍💻 Developer
Developed by mojahid2021

## 🙏 Acknowledgments
- Google ML Kit for barcode scanning
- ZXing for QR code generation
- CameraX for modern camera implementation
- Android Jetpack components

## 📞 Support
For issues, questions, or suggestions, please open an issue on GitHub.

---
Made with ❤️ for the Android community
