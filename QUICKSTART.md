# Quick Start Guide - Universal Translator

## What's New ✨

### 1. **Universal Font Support** 🌐
- **FIXED:** Japanese, Chinese, Korean, Arabic, and other non-English fonts now display correctly
- Uses system fonts with excellent Unicode support (DejaVu Sans, Liberation Sans, Noto fonts)
- Automatic font detection and fallback system

### 2. **Language Selection** 🔄
- **NEW:** Select source language from dropdown (18 languages + Auto Detect)
- **NEW:** Select target language from dropdown
- Real-time language switching - no restart needed!

### 3. **Modern UI Design** 🎨
- Clean, professional interface
- Better organized panels
- Status bar showing application state
- Improved spacing and colors

## Running the Application

### Method 1: Using the Run Script (Easiest)
```bash
./run.sh
```

### Method 2: Manual Command
```bash
java -cp "libs/*:out" com.iishanto.Main
```

## How to Use

1. **Application starts automatically monitoring your clipboard**
   - Copy ANY text and it will be translated instantly
   
2. **Select Languages**
   - **From:** dropdown - Choose source language (or "Auto Detect")
   - **To:** dropdown - Choose target language

3. **Manual Translation**
   - Type text in the "Search:" field
   - Press Enter to translate

4. **View Results**
   - All translations appear in the main text area
   - Format: `Original: Translation`

## Supported Languages (18 + Auto Detect)

✅ English, Bengali, Hindi
✅ Japanese, Chinese (Simplified & Traditional), Korean
✅ Arabic, Spanish, French, German, Italian
✅ Portuguese, Russian, Turkish, Vietnamese, Thai

## Testing Different Scripts

Copy these to test the universal font support:

**Japanese:** こんにちは
**Chinese:** 你好世界
**Korean:** 안녕하세요
**Arabic:** مرحبا بالعالم
**Bengali:** হ্যালো বিশ্ব
**Hindi:** नमस्ते
**Russian:** Здравствуй
**Thai:** สวัสดี

All should display correctly! 🎉

## Font Information

Your system has these excellent fonts available:
- ✅ DejaVu Sans (excellent Unicode support)
- ✅ Liberation Sans (cross-platform compatibility)
- ✅ Noto fonts (Google's universal font family)

The application automatically uses the best font available.

## Tips

- Keep the window "Always on Top" (enabled by default) for easy access
- Use "Auto Detect" if you're copying text in multiple languages
- The search box remembers your last translation
- All translations are saved in the text area (scrollable)

## Troubleshooting

**Problem:** Characters appear as boxes □□□
**Solution:** Your system needs better Unicode fonts. Install with:
```bash
sudo apt install fonts-noto fonts-noto-cjk fonts-noto-color-emoji
```

**Problem:** Application doesn't start
**Solution:** Make sure you compiled first:
```bash
javac -cp "libs/*:src" -d out src/com/iishanto/*.java
```

**Problem:** Translation fails
**Solution:** Check your internet connection (uses Google Translate API)

## Features Summary

| Feature | Status |
|---------|--------|
| Universal Font Support | ✅ Fixed |
| Multi-language Support | ✅ 18 Languages |
| Language Selection UI | ✅ Added |
| Auto Detect Language | ✅ Available |
| Clipboard Monitoring | ✅ Working |
| Manual Translation | ✅ Working |
| Modern UI Design | ✅ Improved |

Enjoy your improved translator! 🚀

