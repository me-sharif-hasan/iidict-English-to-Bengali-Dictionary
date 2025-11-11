package com.iishanto;

import java.awt.*;

/**
 * Test utility to verify font support for Japanese and Bengali characters
 */
public class FontTest {
    public static void main(String[] args) {
        System.out.println("=== Font Support Test ===\n");

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();

        char japaneseChar = 'あ';  // Hiragana 'a'
        char bengaliChar = 'ব';    // Bengali 'ba'

        System.out.println("Testing for Japanese (あ) and Bengali (ব) character support:\n");

        int supportedCount = 0;
        for (String fontName : fonts) {
            Font font = new Font(fontName, Font.PLAIN, 12);
            boolean supportsJapanese = font.canDisplay(japaneseChar);
            boolean supportsBengali = font.canDisplay(bengaliChar);

            if (supportsJapanese && supportsBengali) {
                System.out.println("✓ " + fontName + " - supports BOTH");
                supportedCount++;
                if (supportedCount <= 5) {
                    System.out.println("  Font family: " + font.getFamily());
                }
            }
        }

        System.out.println("\nTotal fonts with full support: " + supportedCount);

        // Test the specific fonts we're looking for
        System.out.println("\n=== Testing Preferred Fonts ===");
        String[] testFonts = {
            "Noto Sans CJK JP",
            "Noto Sans Bengali",
            "Noto Sans",
            "DejaVu Sans",
            "Liberation Sans",
            "SansSerif"
        };

        for (String fontName : testFonts) {
            Font font = new Font(fontName, Font.PLAIN, 12);
            System.out.println("\nFont: " + fontName);
            System.out.println("  Actual: " + font.getFontName());
            System.out.println("  Japanese (あ): " + font.canDisplay(japaneseChar));
            System.out.println("  Bengali (ব): " + font.canDisplay(bengaliChar));
        }
    }
}

