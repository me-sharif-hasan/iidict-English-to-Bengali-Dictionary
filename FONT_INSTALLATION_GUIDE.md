# Font Installation Guide for Universal Translator

## Problem: Japanese Characters Showing as Rectangles

The rectangles you're seeing are "tofu" characters - placeholders that appear when the system doesn't have the appropriate fonts to render Japanese (and other international) characters.

## Solution: Install OS Native Fonts

JavaFX now uses your operating system's native font rendering engine, which requires the appropriate fonts to be installed on your system.

---

## Linux Font Installation

### Ubuntu/Debian

```bash
# Install comprehensive CJK (Chinese, Japanese, Korean) fonts
sudo apt-get update
sudo apt-get install -y fonts-noto-cjk fonts-noto-cjk-extra

# Install individual language fonts (recommended)
sudo apt-get install -y fonts-noto-core
sudo apt-get install -y fonts-noto-color-emoji
sudo apt-get install -y fonts-liberation
sudo apt-get install -y fonts-dejavu

# For better Japanese support
sudo apt-get install -y fonts-ipafont fonts-ipaexfont

# For Bengali support
sudo apt-get install -y fonts-bengali fonts-lohit-beng-bengali

# For Arabic support
sudo apt-get install -y fonts-noto-arabic

# Refresh font cache
fc-cache -fv
```

### Fedora/RHEL/CentOS

```bash
# Install Google Noto fonts
sudo dnf install -y google-noto-*

# Or individual packages
sudo dnf install -y google-noto-sans-cjk-jp-fonts
sudo dnf install -y google-noto-sans-cjk-kr-fonts
sudo dnf install -y google-noto-sans-cjk-sc-fonts
sudo dnf install -y google-noto-sans-arabic-fonts
sudo dnf install -y google-noto-sans-bengali-fonts

# Refresh font cache
fc-cache -fv
```

### Arch Linux

```bash
# Install Noto fonts
sudo pacman -S noto-fonts noto-fonts-cjk noto-fonts-emoji

# Refresh font cache
fc-cache -fv
```

---

## Windows Font Installation

Windows usually comes with good Unicode font support, but if Japanese characters aren't displaying:

### Option 1: Install Language Pack (Recommended)

1. **Settings** → **Time & Language** → **Language**
2. Click **Add a language**
3. Search for **Japanese (日本語)**
4. Select and click **Next**
5. Check **Install language pack**
6. Click **Install**

### Option 2: Download Noto Fonts

1. Visit: https://fonts.google.com/noto
2. Download **Noto Sans CJK JP** (for Japanese)
3. Extract and install the font files
4. Restart the application

---

## macOS Font Installation

macOS has excellent built-in font support, but you can install additional fonts:

```bash
# Using Homebrew
brew tap homebrew/cask-fonts
brew install --cask font-noto-sans-cjk
brew install --cask font-noto-sans-bengali
brew install --cask font-noto-sans-arabic
```

Or manually:
1. Visit: https://fonts.google.com/noto
2. Download desired fonts
3. Open Font Book (Applications → Font Book)
4. Drag font files into Font Book
5. Restart the application

---

## Recommended Font Packages by Language

| Language | Linux Package | Font Family Name |
|----------|--------------|------------------|
| Japanese | fonts-noto-cjk, fonts-ipafont | Noto Sans CJK JP, IPAGothic |
| Chinese (Simplified) | fonts-noto-cjk | Noto Sans CJK SC |
| Chinese (Traditional) | fonts-noto-cjk | Noto Sans CJK TC |
| Korean | fonts-noto-cjk | Noto Sans CJK KR |
| Bengali | fonts-bengali, fonts-lohit-beng-bengali | Noto Sans Bengali, Kalpurush |
| Arabic | fonts-noto-arabic | Noto Sans Arabic |
| Thai | fonts-noto-thai | Noto Sans Thai |
| Hebrew | fonts-noto-hebrew | Noto Sans Hebrew |
| Hindi/Devanagari | fonts-noto-devanagari | Noto Sans Devanagari |

---

## Quick Install Script for Linux

Save this as `install-fonts.sh`:

```bash
#!/bin/bash
echo "Installing international fonts for Universal Translator..."

# Detect package manager
if command -v apt-get &> /dev/null; then
    # Debian/Ubuntu
    sudo apt-get update
    sudo apt-get install -y \
        fonts-noto-cjk \
        fonts-noto-cjk-extra \
        fonts-noto-core \
        fonts-noto-color-emoji \
        fonts-bengali \
        fonts-lohit-beng-bengali \
        fonts-noto-arabic \
        fonts-liberation \
        fonts-dejavu \
        fonts-ipafont
        
elif command -v dnf &> /dev/null; then
    # Fedora/RHEL
    sudo dnf install -y google-noto-*
    
elif command -v pacman &> /dev/null; then
    # Arch Linux
    sudo pacman -S --noconfirm noto-fonts noto-fonts-cjk noto-fonts-emoji
fi

# Refresh font cache
echo "Refreshing font cache..."
fc-cache -fv

echo "✓ Font installation complete!"
echo "Please restart the Universal Translator application."
```

Run with:
```bash
chmod +x install-fonts.sh
./install-fonts.sh
```

---

## Verify Font Installation

After installing fonts, you can verify they're available:

### Linux
```bash
# List all available fonts
fc-list | grep -i noto

# Check for Japanese fonts specifically
fc-list :lang=ja

# Check for Bengali fonts
fc-list :lang=bn

# Check for Arabic fonts
fc-list :lang=ar
```

### Application Detection

When you run the Universal Translator, it will report which fonts are available:

```
=== Initializing OS Font Rendering ===
Font Support Status:
  Japanese: ✓ Available
  Chinese: ✓ Available
  Korean: ✓ Available
  Bengali: ✓ Available
  Arabic: ✓ Available

Available CJK Fonts: Noto Sans CJK JP, Noto Sans CJK KR, Noto Sans CJK SC
```

---

## How JavaFX Uses OS Fonts

The application is configured to use your operating system's native font rendering:

1. **Font Fallback Chain**: The CSS specifies a comprehensive list of fonts, and JavaFX will use the first available font for each character.

2. **OS Integration**: JavaFX uses:
   - **Linux**: FreeType library for rendering
   - **Windows**: DirectWrite API
   - **macOS**: Core Text framework

3. **Automatic Fallback**: When a character can't be displayed with the primary font, the OS automatically tries fallback fonts.

---

## Troubleshooting

### Still seeing rectangles after installing fonts?

1. **Restart the application** - Font cache needs to refresh
2. **Restart your terminal/IDE** - Environment may need to reload
3. **Verify font installation**:
   ```bash
   fc-list | grep -i "noto.*cjk"
   ```
4. **Check application output** - Look for font detection messages

### Fonts installed but not detected by application?

```bash
# Rebuild font cache
fc-cache -fv

# Remove old cache
rm -rf ~/.cache/fontconfig

# Rebuild
fc-cache -fv
```

### Different font rendering between applications?

This is normal - each application uses different text rendering engines. The Universal Translator now uses your OS's native rendering, which should match other native applications.

---

## Font Configuration Examples

### For Best Japanese Rendering (Linux)

Create `~/.config/fontconfig/fonts.conf`:

```xml
<?xml version="1.0"?>
<!DOCTYPE fontconfig SYSTEM "fonts.dtd">
<fontconfig>
  <!-- Japanese font preferences -->
  <alias>
    <family>sans-serif</family>
    <prefer>
      <family>Noto Sans CJK JP</family>
      <family>IPAGothic</family>
    </prefer>
  </alias>
  
  <!-- Enable anti-aliasing -->
  <match target="font">
    <edit name="antialias" mode="assign">
      <bool>true</bool>
    </edit>
  </match>
  
  <!-- LCD filter for better rendering -->
  <match target="font">
    <edit name="lcdfilter" mode="assign">
      <const>lcddefault</const>
    </edit>
  </match>
</fontconfig>
```

Then run: `fc-cache -fv`

---

## Testing Character Rendering

Copy and paste these test strings into the translator to verify font rendering:

- **Japanese**: こんにちは世界 (Hiragana + Kanji)
- **Japanese**: カタカナテスト (Katakana)
- **Chinese (Simplified)**: 你好世界
- **Chinese (Traditional)**: 你好世界
- **Korean**: 안녕하세요
- **Bengali**: হ্যালো বিশ্ব
- **Arabic**: مرحبا بالعالم
- **Thai**: สวัสดีชาวโลก
- **Hebrew**: שלום עולם

All of these should render as proper characters, not rectangles.

---

## Minimal Font Requirements

If you have limited disk space, install at minimum:

```bash
# Ubuntu/Debian minimal
sudo apt-get install fonts-noto-cjk fonts-noto-core

# This provides basic coverage for:
# - Japanese, Chinese, Korean
# - Most European languages
# - Common symbols and emoji
```

Size: ~200-300 MB

## Full Font Package

For complete international support:

```bash
# Ubuntu/Debian complete
sudo apt-get install fonts-noto-*

# This provides comprehensive coverage for:
# - All major writing systems
# - Mathematical symbols
# - Music notation
# - Historical scripts
```

Size: ~1-2 GB

---

## Why This Matters

The Universal Translator now uses **OS-native font rendering** instead of Java's internal renderer. This means:

✓ Better text quality
✓ Proper complex script shaping
✓ Correct bidirectional text (Arabic, Hebrew)
✓ Native font fallback
✓ Consistent with other OS applications

But it requires the fonts to be installed on your system, just like any other native application.

---

## Need Help?

If you're still having issues after following this guide:

1. Check the application console output for font detection information
2. Verify fonts are installed: `fc-list | grep -i noto`
3. Try testing in another application (browser, text editor) to confirm fonts work system-wide
4. Make sure you've restarted the application after installing fonts

---

**Last Updated**: November 12, 2025
**Application**: Universal Translator v2.0 (JavaFX)

