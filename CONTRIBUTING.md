# Contributing to QR Scanner Pro

First off, thank you for considering contributing to QR Scanner Pro! It's people like you that make this app better for everyone.

## Code of Conduct

This project and everyone participating in it is governed by our commitment to providing a welcoming and inspiring community for all.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

* **Use a clear and descriptive title**
* **Describe the exact steps which reproduce the problem**
* **Provide specific examples to demonstrate the steps**
* **Describe the behavior you observed after following the steps**
* **Explain which behavior you expected to see instead and why**
* **Include screenshots if possible**
* **Include your Android version and device model**

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* **Use a clear and descriptive title**
* **Provide a step-by-step description of the suggested enhancement**
* **Provide specific examples to demonstrate the steps**
* **Describe the current behavior and explain which behavior you expected to see instead**
* **Explain why this enhancement would be useful**

### Pull Requests

* Fill in the required template
* Follow the Java coding style used throughout the project
* Include comments in your code where necessary
* Update the README.md with details of changes if applicable
* Update the CHANGELOG.md with your changes
* The PR should work for Android API 24 and above

## Development Setup

1. **Prerequisites**
   - Android Studio (latest stable version)
   - JDK 11 or higher
   - Android SDK (API 24 to 35)

2. **Clone the repository**
   ```bash
   git clone https://github.com/mojahid2021/Simple-QR-Code-Scanner.git
   cd Simple-QR-Code-Scanner
   ```

3. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository
   - Wait for Gradle sync to complete

4. **Build and Run**
   - Connect an Android device or start an emulator
   - Click Run or press Shift+F10

## Coding Guidelines

### Java Style Guide

* Use 4 spaces for indentation
* Use camelCase for variable and method names
* Use PascalCase for class names
* Keep methods focused and concise
* Add JavaDoc comments for public methods
* Follow Android naming conventions

### Resource Naming

* Layout files: `activity_[name].xml`, `item_[name].xml`
* ID naming: `btnActionName`, `tvDescription`, `etInput`
* Drawable naming: `ic_[name]_[color]_[size]dp`
* String resources: Use descriptive keys

### Architecture

* Follow the existing activity-based architecture
* Use Room for database operations
* Perform database operations on background threads
* Use SharedPreferences for settings
* Handle permissions properly
* Manage lifecycle correctly

## Project Structure

```
app/src/main/
├── java/com/mojahid/simple_qr_code_scanner/
│   ├── MainActivity.java
│   ├── GenerateQRActivity.java
│   ├── HistoryActivity.java
│   ├── SettingsActivity.java
│   ├── AboutActivity.java
│   ├── ScanFromImageActivity.java
│   ├── BatchScanActivity.java
│   └── history/
│       ├── ScanDatabase.java
│       ├── ScanHistory.java
│       ├── ScanHistoryDao.java
│       └── ScanHistoryAdapter.java
└── res/
    ├── layout/
    ├── values/
    └── drawable/
```

## Testing

* Test on multiple Android versions (API 24+)
* Test on different screen sizes
* Verify camera functionality
* Check database operations
* Validate all user inputs
* Test permission flows

## Feature Implementation Checklist

When implementing a new feature:

- [ ] Create the necessary Java classes
- [ ] Design and implement XML layouts
- [ ] Add strings to strings.xml
- [ ] Update AndroidManifest.xml if needed
- [ ] Add proper error handling
- [ ] Test on multiple devices
- [ ] Update documentation
- [ ] Add to CHANGELOG.md
- [ ] Create screenshots if UI changes

## Documentation

* Update README.md for significant changes
* Add inline comments for complex logic
* Update CHANGELOG.md with all changes
* Keep JavaDoc comments up to date

## Commit Messages

* Use the present tense ("Add feature" not "Added feature")
* Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
* Limit the first line to 72 characters or less
* Reference issues and pull requests after the first line

Examples:
```
Add batch scanning mode

- Implement continuous QR code scanning
- Add duplicate detection
- Create batch scan adapter
- Update UI for batch mode

Fixes #123
```

## Review Process

1. All submissions require review
2. Changes should be focused and well-tested
3. Follow the coding guidelines
4. Include tests if applicable
5. Update documentation

## Community

* Be respectful and constructive
* Help others when possible
* Share knowledge and experiences
* Follow the code of conduct

## Questions?

Feel free to open an issue with your question or contact the maintainers.

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to QR Scanner Pro! 🎉
