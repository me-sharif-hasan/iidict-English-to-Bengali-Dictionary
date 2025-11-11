#!/bin/bash
# Run script for Universal Translator

cd "$(dirname "$0")"

echo "=========================================="
echo "  Universal Translator - Modern Dark Theme"
echo "=========================================="
echo ""

# Check if compiled
if [ ! -d "out" ]; then
    echo "Compiling project..."
    mkdir -p out
fi

echo "Compiling source files..."
javac -cp "libs/*:src" -d out src/com/iishanto/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi
echo "✓ Compilation successful!"
echo ""

# Copy resources
echo "Copying resources..."
mkdir -p out/res
cp -r src/res/* out/res/ 2>/dev/null

echo ""
echo "Starting application..."
echo "════════════════════════════════════════"
echo "Features:"
echo "  • Two-column layout (Source | Translation)"
echo "  • IntelliJ-style dark theme"
echo "  • Language selection (18 languages)"
echo "  • Clipboard monitoring"
echo "  • History popup (click 📜 icon)"
echo "  • Universal font support (Japanese, Bengali, etc.)"
echo "════════════════════════════════════════"
echo ""

# Run the application
java -cp "libs/*:out" com.iishanto.Main
