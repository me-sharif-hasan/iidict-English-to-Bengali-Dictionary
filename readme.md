# IIDICT - ENGLISH TO BANGLA DICTIONARY

[![Build and Release](https://github.com/YOUR_USERNAME/YOUR_REPO_NAME/actions/workflows/release.yml/badge.svg)](https://github.com/YOUR_USERNAME/YOUR_REPO_NAME/actions/workflows/release.yml)
[![Build Test](https://github.com/YOUR_USERNAME/YOUR_REPO_NAME/actions/workflows/build-test.yml/badge.svg)](https://github.com/YOUR_USERNAME/YOUR_REPO_NAME/actions/workflows/build-test.yml)

## 📥 Download

**No Java installation required!** Download the latest release for your platform:

- **Windows**: Download `IIDict-<version>-windows-x64.exe` from [Releases](https://github.com/YOUR_USERNAME/YOUR_REPO_NAME/releases/latest)
- **Linux (Ubuntu/Debian)**: Download `iidict-<version>-linux-x64.deb` from [Releases](https://github.com/YOUR_USERNAME/YOUR_REPO_NAME/releases/latest)

Both installers come with **bundled JRE 21**, so you don't need to install Java separately!

## 🚀 Installation

### Windows
1. Download the `.exe` installer
2. Double-click to run
3. Follow the installation wizard
4. Launch from Start Menu

### Linux (Ubuntu/Debian)
```bash
sudo dpkg -i iidict-<version>-linux-x64.deb
# If dependencies are missing:
sudo apt-get install -f
```

## 💻 Development Requirements

For developers who want to build from source:

- **Java JDK 21** or higher (matches bundled JRE version)
- **Maven 3.6+** for building

### Legacy Information
* Original configuration was: 
*openjdk 14.0.2 2020-07-14
OpenJDK Runtime Environment (build 14.0.2+12-Ubuntu-120.04)
OpenJDK 64-Bit Server VM (build 14.0.2+12-Ubuntu-120.04, mixed mode, sharing)*

**Note**: The project has been updated to use Java 21 for consistency with the bundled JRE.

