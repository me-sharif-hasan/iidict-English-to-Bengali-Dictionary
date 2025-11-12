package com.iishanto;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class Window {
    // IntelliJ-style dark theme colors
    private static final Color BACKGROUND_PRIMARY = new Color(43, 43, 43);      // #2B2B2B
    private static final Color BACKGROUND_SECONDARY = new Color(60, 63, 65);    // #3C3F41
    private static final Color BACKGROUND_TERTIARY = new Color(69, 73, 74);     // #45494A
    private static final Color TEXT_PRIMARY = new Color(187, 187, 187);         // #BBBBBB
    private static final Color TEXT_SECONDARY = new Color(128, 128, 128);       // #808080
    private static final Color ACCENT_BLUE = new Color(76, 146, 204);           // #4C92CC
    private static final Color ACCENT_HOVER = new Color(94, 161, 214);          // #5EA1D6
    private static final Color BORDER_COLOR = new Color(34, 34, 34);            // #222222
    private static final Color SELECTION_BG = new Color(38, 79, 120);           // #264F78

    private JComboBox<LanguageItem> sourceLanguageCombo;
    private JComboBox<LanguageItem> targetLanguageCombo;
    private JTextArea sourceTextArea;
    private JTextArea translationTextArea;
    private final List<String> translationHistory = new ArrayList<>();
    private boolean isAlwaysOnTop = true; // Track always-on-top state - DEFAULT TO TRUE
    private final JFrame mainFrame; // Reference to the main frame

    // Language class to hold language code and display name
    private static class LanguageItem {
        String code;
        String name;

        LanguageItem(String code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // Load languages from JSON file
    private LanguageItem[] loadLanguagesFromJson() {
        try {
            InputStream langStream = Tools.getConfig().getRes("/languages.json");
            if (langStream == null) {
                System.err.println("Warning: languages.json not found, using fallback languages");
                return getFallbackLanguages();
            }

            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(new InputStreamReader(langStream), JsonObject.class);
            JsonArray languagesArray = jsonObject.getAsJsonArray("languages");

            List<LanguageItem> languageList = new ArrayList<>();
            for (int i = 0; i < languagesArray.size(); i++) {
                JsonObject langObj = languagesArray.get(i).getAsJsonObject();
                String code = langObj.get("code").getAsString();
                String name = langObj.get("name").getAsString();
                languageList.add(new LanguageItem(code, name));
            }

            System.out.println("Successfully loaded " + languageList.size() + " languages from languages.json");
            return languageList.toArray(new LanguageItem[0]);

        } catch (Exception e) {
            System.err.println("Error loading languages.json: " + e.getMessage());
            return getFallbackLanguages();
        }
    }

    // Fallback languages if JSON loading fails
    private LanguageItem[] getFallbackLanguages() {
        return new LanguageItem[]{
            new LanguageItem("auto", "Auto Detect"),
            new LanguageItem("en", "English"),
            new LanguageItem("bn", "Bengali"),
            new LanguageItem("hi", "Hindi"),
            new LanguageItem("ja", "Japanese"),
            new LanguageItem("zh-CN", "Chinese (Simplified)"),
            new LanguageItem("zh-TW", "Chinese (Traditional)"),
            new LanguageItem("ko", "Korean"),
            new LanguageItem("ar", "Arabic"),
            new LanguageItem("es", "Spanish"),
            new LanguageItem("fr", "French"),
            new LanguageItem("de", "German"),
            new LanguageItem("it", "Italian"),
            new LanguageItem("pt", "Portuguese"),
            new LanguageItem("ru", "Russian"),
            new LanguageItem("tr", "Turkish"),
            new LanguageItem("vi", "Vietnamese"),
            new LanguageItem("th", "Thai")
        };
    }

    public Window(){
        mainFrame = new JFrame("Universal Translator");
        try {
            InputStream iconStream = Tools.getConfig().getRes("/icon.png");
            if(iconStream != null) {
                mainFrame.setIconImage(ImageIO.read(iconStream));
            } else {
                System.err.println("Warning: Could not load icon.png from resources");
            }
        } catch (IOException e) {
            System.err.println("Could not load icon: " + e.getMessage());
        }

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 300);
        mainFrame.setMinimumSize(new Dimension(600, 100));
        mainFrame.setLayout(new BorderLayout(0, 0));
        mainFrame.setAlwaysOnTop(true);  // SET ALWAYS ON TOP BY DEFAULT

        // Set dark background
        mainFrame.getContentPane().setBackground(BACKGROUND_PRIMARY);

        // Top toolbar
        JPanel toolbar = createToolbar();
        mainFrame.add(toolbar, BorderLayout.NORTH);

        // Main content area - two columns with center button
        JPanel mainPanel = createMainPanelWithCenterButton();
        mainFrame.add(mainPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        mainFrame.add(statusBar, BorderLayout.SOUTH);

        // Apply universal font
        Font universalFont = getUniversalFont();
        applyFontToComponent(mainFrame, universalFont);

        mainFrame.setVisible(true);
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(BACKGROUND_SECONDARY);
        toolbar.setBorder(new EmptyBorder(8, 12, 8, 12));

        // Left side - Language selection
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        languagePanel.setBackground(BACKGROUND_SECONDARY);

        // Load languages from JSON file
        LanguageItem[] languages = loadLanguagesFromJson();

        sourceLanguageCombo = createStyledComboBox(languages);
        sourceLanguageCombo.setSelectedIndex(0); // Default to Auto Detect

        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setForeground(TEXT_SECONDARY);
        arrowLabel.setFont(new Font("Dialog", Font.BOLD, 16));

        // Find Bengali index (should be at index 9 in full list, but search to be safe)
        int bengaliIndex = 0;
        for (int i = 0; i < languages.length; i++) {
            if ("bn".equals(languages[i].code)) {
                bengaliIndex = i;
                break;
            }
        }

        targetLanguageCombo = createStyledComboBox(languages);
        targetLanguageCombo.setSelectedIndex(bengaliIndex); // Default to Bengali

        // Language selection listeners
        sourceLanguageCombo.addActionListener(e -> {
            LanguageItem selected = (LanguageItem) sourceLanguageCombo.getSelectedItem();
            if (selected != null) {
                Tools.getConfig().setSourceLanguage(selected.code);
            }
        });

        targetLanguageCombo.addActionListener(e -> {
            LanguageItem selected = (LanguageItem) targetLanguageCombo.getSelectedItem();
            if (selected != null) {
                Tools.getConfig().setTargetLanguage(selected.code);
            }
        });

        // Initialize default languages
        Tools.getConfig().setSourceLanguage("auto");
        Tools.getConfig().setTargetLanguage("bn");

        languagePanel.add(sourceLanguageCombo);
        languagePanel.add(arrowLabel);
        languagePanel.add(targetLanguageCombo);

        // Right side - History button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setBackground(BACKGROUND_SECONDARY);

        // Always-on-top toggle button - using text icon instead of emoji
        JButton alwaysOnTopButton = createIconButton("📍", "Toggle Always On Top");
        alwaysOnTopButton.setFont(new Font("Dialog", Font.BOLD, 14));
        alwaysOnTopButton.setText("PIN");  // Use text as fallback
        alwaysOnTopButton.addActionListener(e -> toggleAlwaysOnTop(alwaysOnTopButton));
        rightPanel.add(alwaysOnTopButton);

        // History button - using text icon instead of emoji
        JButton historyButton = createIconButton("📋", "Show History");
        historyButton.setFont(new Font("Dialog", Font.BOLD, 14));
        historyButton.setText("HIST");  // Use text as fallback
        historyButton.addActionListener(e -> showHistoryDialog());
        rightPanel.add(historyButton);

        toolbar.add(languagePanel, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);

        return toolbar;
    }

    private JPanel createMainPanelWithCenterButton() {
        // Use JLayeredPane to overlay button on top of text panels
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null); // Use null layout for manual positioning

        // Container for the two text panels
        JPanel textPanelsContainer = new JPanel(new GridLayout(1, 2, 1, 0));
        textPanelsContainer.setBackground(BORDER_COLOR);

        // Left panel - Source text (without translate button)
        JPanel leftPanel = createTextPanel("Source Text", true);

        // Right panel - Translation
        JPanel rightPanel = createTextPanel("Translation", false);

        textPanelsContainer.add(leftPanel);
        textPanelsContainer.add(rightPanel);

        // Add text panels to the base layer
        textPanelsContainer.setBounds(0, 0, 900, 600);
        layeredPane.add(textPanelsContainer, Integer.valueOf(JLayeredPane.DEFAULT_LAYER));

        // Create the center translate button
        JButton translateButton = createCenterTranslateButton();
        layeredPane.add(translateButton, Integer.valueOf(JLayeredPane.PALETTE_LAYER));

        // Add component listener to position elements when window resizes
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Resize the text panels container to fill the layered pane
                textPanelsContainer.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());

                // Position the translate button at bottom center with 60px spacing from bottom
                int x = (layeredPane.getWidth() - translateButton.getWidth()) / 2;
                int y = layeredPane.getHeight() - translateButton.getHeight() - 60; // 60px from bottom
                translateButton.setBounds(x, y, translateButton.getWidth(), translateButton.getHeight());
            }
        });

        // Wrap in a panel for proper integration with BorderLayout
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(layeredPane, BorderLayout.CENTER);

        return wrapper;
    }

    private JButton createCenterTranslateButton() {
        // Create a rounded button with triangle icon
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded background
                if (getModel().isPressed()) {
                    g2.setColor(ACCENT_BLUE.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT_HOVER);
                } else {
                    g2.setColor(ACCENT_BLUE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);

                // Draw triangle icon (play/forward symbol)
                g2.setColor(Color.WHITE);
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int[] xPoints = {centerX - 8, centerX + 10, centerX - 8};
                int[] yPoints = {centerY - 10, centerY, centerY + 10};
                g2.fillPolygon(xPoints, yPoints, 3);

                g2.dispose();
            }
        };

        button.setPreferredSize(new Dimension(60, 60));
        button.setSize(60, 60);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Translate (or press Enter)");

        button.addActionListener(e -> performTranslation());

        return button;
    }

    private JPanel createTextPanel(String title, boolean isSource) {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BACKGROUND_PRIMARY);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND_TERTIARY);
        header.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        header.add(titleLabel, BorderLayout.WEST);

        panel.add(header, BorderLayout.NORTH);

        // Text area
        JTextArea textArea = new JTextArea();
        textArea.setBackground(BACKGROUND_PRIMARY);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setCaretColor(TEXT_PRIMARY);
        textArea.setSelectionColor(SELECTION_BG);
        textArea.setSelectedTextColor(TEXT_PRIMARY);
        // Preserve formatting: disable word wrap to keep tabs and line breaks intact
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(false);
        textArea.setTabSize(4);  // Set consistent tab size
        textArea.setMargin(new Insets(12, 12, 12, 12));
        textArea.setBorder(BorderFactory.createEmptyBorder());

        if (isSource) {
            sourceTextArea = textArea;
            textArea.setEditable(true);

            // Add Enter key listener for quick translation
            textArea.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
                        performTranslation();
                        e.consume();
                    }
                }
            });
        } else {
            translationTextArea = textArea;
            textArea.setEditable(false);
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBackground(BACKGROUND_PRIMARY);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_PRIMARY);

        // Style scrollbar
        styleScrollBar(scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(BACKGROUND_SECONDARY);
        statusBar.setBorder(new EmptyBorder(4, 12, 4, 12));

        JLabel statusLabel = new JLabel("Monitoring clipboard • Press Ctrl+Enter to translate");
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 11));

        statusBar.add(statusLabel, BorderLayout.WEST);

        return statusBar;
    }

    private JComboBox<LanguageItem> createStyledComboBox(LanguageItem[] items) {
        JComboBox<LanguageItem> comboBox = new JComboBox<>(items);
        comboBox.setBackground(BACKGROUND_TERTIARY);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(2, 4, 2, 4)
        ));
        comboBox.setFocusable(false);

        // Style the renderer
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? SELECTION_BG : BACKGROUND_TERTIARY);
                setForeground(TEXT_PRIMARY);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });

        return comboBox;
    }

    private JButton createFlatButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT_BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add rounded border
        button.setBorder(new EmptyBorder(6, 16, 6, 16));
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_HOVER);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_BLUE);
                button.repaint();
            }
        });

        // Custom painting for rounded corners
        button.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                button.repaint();
            }
        });

        // Override paint to draw rounded rectangle
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                JButton btn = (JButton) c;
                if (btn.getModel().isPressed()) {
                    g2.setColor(btn.getBackground().darker());
                } else {
                    g2.setColor(btn.getBackground());
                }
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);

                g2.setColor(btn.getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int x = (c.getWidth() - fm.stringWidth(btn.getText())) / 2;
                int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(btn.getText(), x, y);

                g2.dispose();
            }
        });

        return button;
    }

    private JButton createIconButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setBackground(BACKGROUND_TERTIARY);
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Dialog", Font.PLAIN, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        // Rounded button
        button.setBorder(new EmptyBorder(6, 12, 6, 12));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BACKGROUND_PRIMARY);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BACKGROUND_TERTIARY);
                button.repaint();
            }
        });

        // Custom painting for rounded background
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                JButton btn = (JButton) c;
                if (btn.getModel().isRollover()) {
                    g2.setColor(btn.getBackground());
                    g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);
                }

                g2.setColor(btn.getForeground());
                g2.setFont(btn.getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (c.getWidth() - fm.stringWidth(btn.getText())) / 2;
                int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(btn.getText(), x, y);

                g2.dispose();
            }
        });

        return button;
    }

    private void styleScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
        verticalBar.setBackground(BACKGROUND_PRIMARY);
        verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = BACKGROUND_TERTIARY;
                this.trackColor = BACKGROUND_PRIMARY;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
    }

    private void performTranslation() {
        String text = sourceTextArea.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        Tools.getConfig().regNewText(text);
        Tools.getConfig().callEvent("new_text");
    }

    private void showHistoryDialog() {
        JDialog dialog = new JDialog((Frame) null, "Translation History", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_PRIMARY);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND_SECONDARY);
        header.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel("Translation History");
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        header.add(titleLabel, BorderLayout.WEST);

        JButton closeButton = createIconButton("✕", "Close");
        closeButton.addActionListener(e -> dialog.dispose());
        header.add(closeButton, BorderLayout.EAST);

        dialog.add(header, BorderLayout.NORTH);

        // History list
        JTextArea historyArea = new JTextArea();
        historyArea.setBackground(BACKGROUND_PRIMARY);
        historyArea.setForeground(TEXT_PRIMARY);
        historyArea.setCaretColor(TEXT_PRIMARY);
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setMargin(new Insets(12, 12, 12, 12));
        historyArea.setFont(getUniversalFont().deriveFont(14f));

        StringBuilder historyText = new StringBuilder();
        if (translationHistory.isEmpty()) {
            historyText.append("No translation history yet.\n\nCopy text or use the Translate button to start.");
        } else {
            for (int i = translationHistory.size() - 1; i >= 0; i--) {
                historyText.append(translationHistory.get(i));
                if (i > 0) historyText.append("\n---\n\n");
            }
        }
        historyArea.setText(historyText.toString());

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBackground(BACKGROUND_PRIMARY);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        styleScrollBar(scrollPane);

        dialog.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bottomPanel.setBackground(BACKGROUND_SECONDARY);

        JButton clearButton = createFlatButton("Clear History");
        clearButton.addActionListener(e -> {
            translationHistory.clear();
            historyArea.setText("History cleared.");
        });

        bottomPanel.add(clearButton);

        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void applyFontToComponent(Component component, Font font) {
        if (component instanceof JTextArea || component instanceof JTextField) {
            component.setFont(font.deriveFont(14f));
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyFontToComponent(child, font);
            }
        }
    }

    private void setupEventHandlers() {
        Tools.getConfig().addEvent(new Event() {
            @Override
            public void event() {
                // Get source and translation separately - no more colon splitting!
                String source = Tools.getConfig().getLatestSource();
                String translation = Tools.getConfig().getLatestTranslation();

                if (source != null && !source.isEmpty() && translation != null && !translation.isEmpty()) {
                    sourceTextArea.setText(source);
                    translationTextArea.setText(translation);

                    // Add formatted version to history
                    String historyEntry = Tools.getConfig().getLatestTranslationFormatted();
                    translationHistory.add(historyEntry);
                    if (translationHistory.size() > 50) {
                        translationHistory.remove(0);
                    }
                }
            }
        });
    }

    private Font getUniversalFont() {
        System.out.println("=== Configuring Universal Font Support ===");

        Font baseFont = new Font("SansSerif", Font.PLAIN, 14);

        boolean canDisplayJapanese = baseFont.canDisplay('あ');
        boolean canDisplayBengali = baseFont.canDisplay('ব');

        System.out.println("Font capabilities:");
        System.out.println("  Japanese: " + (canDisplayJapanese ? "✓" : "✗"));
        System.out.println("  Bengali: " + (canDisplayBengali ? "✓" : "✗"));
        System.out.println("  Arabic: " + (baseFont.canDisplay('م') ? "✓" : "✗"));
        System.out.println("  Chinese: " + (baseFont.canDisplay('中') ? "✓" : "✗"));

        if (canDisplayJapanese && canDisplayBengali) {
            System.out.println("✓ Font fallback working correctly!");
        }

        return baseFont;
    }

    private void toggleAlwaysOnTop(JButton button) {
        isAlwaysOnTop = !isAlwaysOnTop;
        mainFrame.setAlwaysOnTop(isAlwaysOnTop);

        // Update button appearance to show state
        if (isAlwaysOnTop) {
            button.setForeground(ACCENT_BLUE);  // Blue when active
            button.setToolTipText("Always On Top (ON) - Click to disable");
        } else {
            button.setForeground(TEXT_SECONDARY);  // Gray when inactive
            button.setToolTipText("Always On Top (OFF) - Click to enable");
        }
        button.repaint();
    }

    {
        // Constructor block - setup event handlers after UI is created
        SwingUtilities.invokeLater(this::setupEventHandlers);
    }
}
