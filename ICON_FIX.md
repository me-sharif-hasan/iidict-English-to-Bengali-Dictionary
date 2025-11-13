# Icon Fix for EXE and DEB Releases

## Problem
The Windows EXE and Linux DEB installers were showing the default Java icon instead of the custom logo.

## Root Cause
- **Windows**: jpackage requires `.ico` format, but the workflow was pointing to `logo.png`
- **Maven Build**: The `.ico` files were not included in the Maven resources configuration

## Solution Applied

### 1. Workflow Updated (`.github/workflows/release.yml`)
Changed the Windows packaging icon parameter from:
```yaml
--icon "src\res\logo.png" `
```
to:
```yaml
--icon "src\res\logo.ico" `
```

### 2. Maven Resources Updated (`pom.xml`)
Added `.ico` files to the Maven resources section:
```xml
<include>**/*.ico</include>
```

This ensures that `logo.ico` is properly included in the packaged JAR.

## Requirements
- ✅ `logo.ico` file added to `src/res/` directory
- ✅ Workflow updated to use `.ico` for Windows
- ✅ Maven configured to include `.ico` files
- ✅ Linux already using `.png` format (correct)

## Next Steps
1. Commit these changes
2. Push to trigger the release workflow
3. The next Windows EXE build will display your custom logo
4. The Linux DEB build will continue to use the PNG logo

## Icon Format Requirements
- **Windows (.ico)**: Multi-resolution icon file with sizes: 16x16, 32x32, 48x48, 256x256
- **Linux (.png)**: PNG file, typically 128x128 or 256x256 pixels

## Testing
After the next release build:
- Windows: Check the EXE installer icon and installed application icon
- Linux: Check the DEB package icon and installed application icon in the application menu

