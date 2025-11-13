# CI/CD Pipeline Documentation

## Overview

This project uses GitHub Actions for automated building and releasing of the IIDict application. The pipeline automatically creates standalone installers for Windows and Linux with bundled JRE 21.0.9.

## Workflows

### 1. Build and Release (`release.yml`)

**Trigger**: Push or merge to `master` branch

**What it does**:
- Automatically calculates version number based on commit count
- Builds application for both Windows and Linux
- Downloads and bundles Oracle JRE 21.0.9 for both platforms
- Creates platform-specific installers:
  - Windows: `.exe` installer with Start Menu integration
  - Linux: `.deb` package for Debian/Ubuntu
- Creates a GitHub Release with auto-generated release notes
- Uploads installers as release assets

### 2. Build Test (`build-test.yml`)

**Trigger**: Pull requests and pushes to non-master branches

**What it does**:
- Tests builds on both Windows and Linux
- Verifies JAR creation
- Helps catch build issues before merging

## Version Numbering

The version number is automatically calculated using the format:
```
<base-version>.<build-number>
```

- **Base version**: Extracted from `pom.xml` (currently `1.0`)
- **Build number**: Total count of commits in the repository
- **Example**: `1.0.123` (version 1.0, build 123)

## Release Assets

Each release includes:

### Windows
- **File**: `IIDict-<version>-windows-x64.exe`
- **Size**: ~200-250 MB (includes JRE)
- **JRE**: Oracle JRE 21.0.9 for Windows x64
- **Features**:
  - GUI installer
  - Start Menu shortcuts
  - Desktop shortcut option
  - Uninstaller
  - Directory chooser

### Linux
- **File**: `iidict-<version>-linux-x64.deb`
- **Size**: ~200-250 MB (includes JRE)
- **JRE**: Oracle JRE 21.0.9 for Linux x64
- **Features**:
  - Debian package format
  - Application menu integration
  - System integration
  - Easy uninstall via package manager

## Installation Instructions

### Windows
1. Download `IIDict-<version>-windows-x64.exe` from the latest release
2. Double-click the installer
3. Follow the installation wizard
4. Launch from Start Menu or Desktop shortcut

### Linux (Ubuntu/Debian)
1. Download `iidict-<version>-linux-x64.deb` from the latest release
2. Install using:
   ```bash
   sudo dpkg -i iidict-<version>-linux-x64.deb
   ```
3. If there are missing dependencies:
   ```bash
   sudo apt-get install -f
   ```
4. Launch from application menu or run `iidict` from terminal

## Manual Trigger

You can manually trigger a release build:
1. Go to **Actions** tab in GitHub
2. Select **Build and Release** workflow
3. Click **Run workflow**
4. Select the `master` branch
5. Click **Run workflow**

## Requirements

### For Building
- Maven 3.6+
- JDK 21 (for compilation)
- Internet connection (to download JRE 21)

### For End Users
- **Windows**: Windows 10 or later (x64)
- **Linux**: Ubuntu 20.04+ or equivalent (x64)
- **No Java installation required** - JRE is bundled!

## Technical Details

### Build Process

1. **Prepare Stage**
   - Calculates version number
   - Outputs version for other jobs

2. **Build Windows**
   - Sets up JDK 21 for compilation
   - Updates version in `pom.xml`
   - Builds JAR with Maven
   - Downloads Oracle JRE 21 for Windows
   - Uses `jpackage` to create Windows installer with bundled JRE
   - Renames installer with version number

3. **Build Linux**
   - Sets up JDK 21 for compilation
   - Updates version in `pom.xml`
   - Builds JAR with Maven
   - Downloads Oracle JRE 21 for Linux
   - Uses `jpackage` to create Debian package with bundled JRE
   - Renames package with version number

4. **Create Release**
   - Downloads artifacts from both builds
   - Generates release notes with changelog
   - Creates GitHub Release
   - Uploads installers as release assets
   - Updates `latest` tag

### JRE Sources

- **Windows**: https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.zip
- **Linux**: https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz

These URLs always point to the latest JRE 21 build from Oracle.

## Troubleshooting

### Build Fails on Windows
- Check if WiX Toolset installation succeeded
- Verify icon file exists at `src/res/icon.png`

### Build Fails on Linux
- Check if `fakeroot` and `binutils` were installed
- Verify icon file exists at `src/res/icon.png`

### Release Not Created
- Check if `GITHUB_TOKEN` has proper permissions
- Ensure workflow has `contents: write` permission

### Version Not Updating
- Version is based on commit count
- Make sure there are actual commits being made

## Customization

### Changing Base Version
Edit `pom.xml` and change the version number:
```xml
<version>2.0</version>
```

Next release will be `2.0.X` where X is the commit count.

### Changing App Name
Edit the `--name` parameter in the `jpackage` commands in `release.yml`.

### Changing Icons
Replace `src/res/icon.png` with your desired icon (preferably 256x256 or larger).

### Adding More Platforms
Add additional jobs in `release.yml` for macOS or other Linux distributions.

## GitHub Actions Permissions

The workflow requires the following permissions:
- `contents: write` - To create releases and push tags
- `actions: read` - To download artifacts

These are automatically granted by the `GITHUB_TOKEN`.

## Monitoring

- View workflow runs in the **Actions** tab
- Each run shows detailed logs for debugging
- Artifacts are retained for 5 days
- Releases are permanent until manually deleted

## Support

For issues with the CI/CD pipeline, check:
1. GitHub Actions logs
2. Maven build output
3. jpackage error messages
4. Release creation status

---

**Last Updated**: 2025-11-13
# CI/CD Pipeline Documentation

## Overview

This project uses GitHub Actions for automated building and releasing of the IIDict application. The pipeline automatically creates standalone installers for Windows and Linux with bundled JRE 21.0.9.

## Workflows

### 1. Build and Release (`release.yml`)

**Trigger**: Push or merge to `master` branch

**What it does**:
- Automatically calculates version number based on commit count
- Builds application for both Windows and Linux
- Downloads and bundles Oracle JRE 21.0.9 for both platforms
- Creates platform-specific installers:
  - Windows: `.exe` installer with Start Menu integration
  - Linux: `.deb` package for Debian/Ubuntu
- Creates a GitHub Release with auto-generated release notes
- Uploads installers as release assets

### 2. Build Test (`build-test.yml`)

**Trigger**: Pull requests and pushes to non-master branches

**What it does**:
- Tests builds on both Windows and Linux
- Verifies JAR creation
- Helps catch build issues before merging

## Version Numbering

The version number is automatically calculated using the format:
```
<base-version>.<build-number>
```

- **Base version**: Extracted from `pom.xml` (currently `1.0`)
- **Build number**: Total count of commits in the repository
- **Example**: `1.0.123` (version 1.0, build 123)

## Release Assets

Each release includes:

### Windows
- **File**: `IIDict-<version>-windows-x64.exe`
- **Size**: ~200-250 MB (includes JRE)
- **JRE**: Oracle JRE 21.0.9 for Windows x64
- **Features**:
  - GUI installer
  - Start Menu shortcuts
  - Desktop shortcut option
  - Uninstaller
  - Directory chooser

### Linux
- **File**: `iidict-<version>-linux-x64.deb`
- **Size**: ~200-250 MB (includes JRE)
- **JRE**: Oracle JRE 21.0.9 for Linux x64
- **Features**:
  - Debian package format
  - Application menu integration
  - System integration
  - Easy uninstall via package manager

## Installation Instructions

### Windows
1. Download `IIDict-<version>-windows-x64.exe` from the latest release
2. Double-click the installer
3. Follow the installation wizard
4. Launch from Start Menu or Desktop shortcut

### Linux (Ubuntu/Debian)
1. Download `iidict-<version>-linux-x64.deb` from the latest release
2. Install using:
   ```bash
   sudo dpkg -i iidict-<version>-linux-x64.deb
   ```
3. If there are missing dependencies:
   ```bash
   sudo apt-get install -f
   ```
4. Launch from application menu or run `iidict` from terminal

## Manual Trigger

You can manually trigger a release build:
1. Go to **Actions** tab in GitHub
2. Select **Build and Release** workflow
3. Click **Run workflow**
4. Select the `master` branch
5. Click **Run workflow**

## Requirements

### For Building
- Maven 3.6+
- JDK 11 (for compilation)
- Internet connection (to download JRE 21)

### For End Users
- **Windows**: Windows 10 or later (x64)
- **Linux**: Ubuntu 20.04+ or equivalent (x64)
- **No Java installation required** - JRE is bundled!

## Technical Details

### Build Process

1. **Prepare Stage**
   - Calculates version number
   - Outputs version for other jobs

2. **Build Windows**
   - Sets up JDK 21 for compilation
   - Updates version in `pom.xml`
   - Builds JAR with Maven
   - Downloads Oracle JRE 21 for Windows
   - Uses `jpackage` to create Windows installer with bundled JRE
   - Renames installer with version number

3. **Build Linux**
   - Sets up JDK 21 for compilation
   - Updates version in `pom.xml`
   - Builds JAR with Maven
   - Downloads Oracle JRE 21 for Linux
   - Uses `jpackage` to create Debian package with bundled JRE
   - Renames package with version number

4. **Create Release**
   - Downloads artifacts from both builds
   - Generates release notes with changelog
   - Creates GitHub Release
   - Uploads installers as release assets
   - Updates `latest` tag

### JRE Sources

- **Windows**: https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.zip
- **Linux**: https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz

These URLs always point to the latest JRE 21 build from Oracle.

## Troubleshooting

### Build Fails on Windows
- Check if WiX Toolset installation succeeded
- Verify icon file exists at `src/res/icon.png`

### Build Fails on Linux
- Check if `fakeroot` and `binutils` were installed
- Verify icon file exists at `src/res/icon.png`

### Release Not Created
- Check if `GITHUB_TOKEN` has proper permissions
- Ensure workflow has `contents: write` permission

### Version Not Updating
- Version is based on commit count
- Make sure there are actual commits being made

## Customization

### Changing Base Version
Edit `pom.xml` and change the version number:
```xml
<version>2.0</version>
```

Next release will be `2.0.X` where X is the commit count.

### Changing App Name
Edit the `--name` parameter in the `jpackage` commands in `release.yml`.

### Changing Icons
Replace `src/res/icon.png` with your desired icon (preferably 256x256 or larger).

### Adding More Platforms
Add additional jobs in `release.yml` for macOS or other Linux distributions.

## GitHub Actions Permissions

The workflow requires the following permissions:
- `contents: write` - To create releases and push tags
- `actions: read` - To download artifacts

These are automatically granted by the `GITHUB_TOKEN`.

## Monitoring

- View workflow runs in the **Actions** tab
- Each run shows detailed logs for debugging
- Artifacts are retained for 5 days
- Releases are permanent until manually deleted

## Support

For issues with the CI/CD pipeline, check:
1. GitHub Actions logs
2. Maven build output
3. jpackage error messages
4. Release creation status

---

**Last Updated**: 2025-11-13

