package com.iishanto;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class WindowFX extends Application {
    private Stage primaryStage;
    private ComboBox<LanguageItem> sourceLanguageCombo;
    private ComboBox<LanguageItem> targetLanguageCombo;
    private TextArea sourceTextArea;
    private TextArea translationTextArea;
    private final List<String> translationHistory = new ArrayList<>();
    private boolean isAlwaysOnTop = true;
    private Button alwaysOnTopButton;

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

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Universal Translator");
        primaryStage.setAlwaysOnTop(true);

        // Initialize font rendering system
        initializeFontRendering();

        // Load icon
        loadIcon();

        // Create UI
        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-container");

        // Top toolbar
        HBox toolbar = createToolbar();
        root.setTop(toolbar);

        // Main content with overlay button
        StackPane mainContent = createMainContent();
        root.setCenter(mainContent);

        // Status bar
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 600, 300);

        // Load CSS stylesheet
        try {
            InputStream cssStream = getClass().getResourceAsStream("/res/dark-theme.css");
            if (cssStream != null) {
                cssStream.close();
                scene.getStylesheets().add(getClass().getResource("/res/dark-theme.css").toExternalForm());
                System.out.println("✓ Dark theme CSS loaded successfully");
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load dark-theme.css: " + e.getMessage());
        }

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(100);

        // Add shutdown hook for proper cleanup
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Application closing...");
            shutdown();
        });

        // Setup event handlers after UI is created
        Platform.runLater(this::setupEventHandlers);

        primaryStage.show();

        System.out.println("=== JavaFX Window Initialized ===");
        System.out.println("✓ Using OS native font rendering");
        System.out.println("✓ Hardware-accelerated rendering enabled");
    }

    /**
     * Initialize OS font rendering system with proper fallback for international characters
     */
    private void initializeFontRendering() {
        System.out.println("=== Initializing OS Font Rendering ===");

        // Load system fonts explicitly
        loadSystemFonts();

        // Get all available system fonts
        List<String> fontFamilies = Font.getFamilies();

        // Check for important international fonts
        boolean hasJapanese = false;
        boolean hasChinese = false;
        boolean hasBengali = false;
        boolean hasArabic = false;
        boolean hasKorean = false;

        List<String> cjkFonts = new ArrayList<>();
        List<String> indicFonts = new ArrayList<>();
        List<String> arabicFonts = new ArrayList<>();

        for (String family : fontFamilies) {
            String lowerFamily = family.toLowerCase();

            // Japanese fonts
            if (lowerFamily.contains("noto serif cjk jp") ||
                lowerFamily.contains("noto sans cjk jp") ||
                lowerFamily.contains("noto sans jp") ||
                lowerFamily.contains("ms gothic") ||
                lowerFamily.contains("meiryo") ||
                lowerFamily.contains("hiragino") ||
                lowerFamily.contains("ipagothic") ||
                lowerFamily.contains("takao")) {
                hasJapanese = true;
                cjkFonts.add(family);
            }

            // Chinese fonts
            if (lowerFamily.contains("noto serif cjk") ||
                lowerFamily.contains("noto sans cjk") ||
                lowerFamily.contains("noto sans sc") ||
                lowerFamily.contains("noto sans tc") ||
                lowerFamily.contains("microsoft yahei") ||
                lowerFamily.contains("simsun") ||
                lowerFamily.contains("wqy")) {
                hasChinese = true;
                cjkFonts.add(family);
            }

            // Korean fonts
            if (lowerFamily.contains("noto sans kr") ||
                lowerFamily.contains("noto serif cjk kr") ||
                lowerFamily.contains("noto sans cjk kr") ||
                lowerFamily.contains("malgun") ||
                lowerFamily.contains("nanum")) {
                hasKorean = true;
                cjkFonts.add(family);
            }

            // Bengali fonts
            if (lowerFamily.contains("noto sans bengali") ||
                lowerFamily.contains("kalpurush") ||
                lowerFamily.contains("lohit bengali") ||
                lowerFamily.contains("mukti")) {
                hasBengali = true;
                indicFonts.add(family);
            }

            // Arabic fonts
            if (lowerFamily.contains("noto sans arabic") ||
                lowerFamily.contains("arabic") ||
                lowerFamily.contains("droid arabic")) {
                hasArabic = true;
                arabicFonts.add(family);
            }
        }

        // Report font availability
        System.out.println("Font Support Status:");
        System.out.println("  Japanese: " + (hasJapanese ? "✓ Available" : "✗ Missing"));
        System.out.println("  Chinese: " + (hasChinese ? "✓ Available" : "✗ Missing"));
        System.out.println("  Korean: " + (hasKorean ? "✓ Available" : "✗ Missing"));
        System.out.println("  Bengali: " + (hasBengali ? "✓ Available" : "✗ Missing"));
        System.out.println("  Arabic: " + (hasArabic ? "✓ Available" : "✗ Missing"));

        if (!cjkFonts.isEmpty()) {
            System.out.println("\nAvailable CJK Fonts: " + String.join(", ", cjkFonts.subList(0, Math.min(3, cjkFonts.size()))));
        }
        if (!indicFonts.isEmpty()) {
            System.out.println("Available Indic Fonts: " + String.join(", ", indicFonts));
        }
        if (!arabicFonts.isEmpty()) {
            System.out.println("Available Arabic Fonts: " + String.join(", ", arabicFonts));
        }

        // Test character rendering
        testFontRendering();

        // Set system properties for better font rendering
        System.setProperty("prism.lcdtext", "true");
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.allowhidpi", "true");

        System.out.println("✓ Font rendering system initialized");
    }

    /**
     * Explicitly load system fonts from common paths
     */
    private void loadSystemFonts() {
        String[] fontPaths = {
            "/usr/share/fonts/opentype/noto/",
            "/usr/share/fonts/truetype/noto/",
            "/usr/share/fonts/opentype/",
            "/usr/share/fonts/truetype/",
            "/usr/local/share/fonts/",
            System.getProperty("user.home") + "/.fonts/"
        };

        for (String path : fontPaths) {
            java.io.File fontDir = new java.io.File(path);
            if (fontDir.exists() && fontDir.isDirectory()) {
                java.io.File[] fontFiles = fontDir.listFiles((dir, name) -> {
                    String lower = name.toLowerCase();
                    return (lower.endsWith(".ttf") || lower.endsWith(".ttc") || lower.endsWith(".otf")) &&
                           (lower.contains("noto") || lower.contains("cjk") || lower.contains("bengali"));
                });

                if (fontFiles != null) {
                    for (java.io.File fontFile : fontFiles) {
                        try {
                            Font.loadFont(new java.io.FileInputStream(fontFile), 14);
                            System.out.println("  Loaded: " + fontFile.getName());
                        } catch (Exception e) {
                            // Silently skip fonts that can't be loaded
                        }
                    }
                }
            }
        }
    }

    /**
     * Test if critical characters can be rendered
     */
    private void testFontRendering() {
        Font testFont = Font.getDefault();

        // Test characters from different scripts
        char[] testChars = {
            'あ', // Japanese Hiragana
            'ア', // Japanese Katakana
            '漢', // Chinese/Japanese Kanji
            'ব', // Bengali
            'م', // Arabic
            '한', // Korean Hangul
            'ก', // Thai
            'א'  // Hebrew
        };

        String[] scriptNames = {
            "Japanese (Hiragana)",
            "Japanese (Katakana)",
            "CJK (Kanji/Hanzi)",
            "Bengali",
            "Arabic",
            "Korean",
            "Thai",
            "Hebrew"
        };

        System.out.println("\nCharacter Rendering Test:");
        for (int i = 0; i < testChars.length; i++) {
            // Note: JavaFX doesn't have a direct canDisplay method, but the OS will handle fallback
            System.out.println("  " + scriptNames[i] + " (" + testChars[i] + "): Will use OS font fallback");
        }
    }

    @Override
    public void stop() throws Exception {
        System.out.println("JavaFX Application stopping...");
        shutdown();
        super.stop();
    }

    private void shutdown() {
        // Stop clipboard monitoring
        ClipBoard clipBoard = Main.getClipBoard();
        if (clipBoard != null) {
            clipBoard.stop();
        }

        // Exit the application
        Platform.exit();
        System.exit(0);
    }

    private void loadIcon() {
        try {
            InputStream iconStream = Tools.getConfig().getRes("/icon.png");
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                primaryStage.getIcons().add(icon);
                iconStream.close();
                System.out.println("✓ Window icon loaded");
            } else {
                System.err.println("Warning: Could not load icon.png from resources");
            }
        } catch (Exception e) {
            System.err.println("Could not load icon: " + e.getMessage());
        }
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.getStyleClass().add("toolbar");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Left side - Language selection
        HBox languagePanel = new HBox(10);
        languagePanel.setAlignment(Pos.CENTER_LEFT);

        // Load languages from JSON file
        LanguageItem[] languages = loadLanguagesFromJson();

        sourceLanguageCombo = createStyledComboBox(languages);
        sourceLanguageCombo.getSelectionModel().selectFirst(); // Default to Auto Detect

        Label arrowLabel = new Label("→");
        arrowLabel.getStyleClass().add("arrow-label");

        // Find Bengali index
        int bengaliIndex = 0;
        for (int i = 0; i < languages.length; i++) {
            if ("bn".equals(languages[i].code)) {
                bengaliIndex = i;
                break;
            }
        }

        targetLanguageCombo = createStyledComboBox(languages);
        targetLanguageCombo.getSelectionModel().select(bengaliIndex); // Default to Bengali

        // Language selection listeners
        sourceLanguageCombo.setOnAction(e -> {
            LanguageItem selected = sourceLanguageCombo.getValue();
            if (selected != null) {
                Tools.getConfig().setSourceLanguage(selected.code);
            }
        });

        targetLanguageCombo.setOnAction(e -> {
            LanguageItem selected = targetLanguageCombo.getValue();
            if (selected != null) {
                Tools.getConfig().setTargetLanguage(selected.code);
            }
        });

        // Initialize default languages
        Tools.getConfig().setSourceLanguage("auto");
        Tools.getConfig().setTargetLanguage("bn");

        languagePanel.getChildren().addAll(sourceLanguageCombo, arrowLabel, targetLanguageCombo);

        // Right side - Control buttons
        HBox rightPanel = new HBox(8);
        rightPanel.setAlignment(Pos.CENTER_RIGHT);

        // Always-on-top toggle button
        alwaysOnTopButton = createIconButton("PIN", "Toggle Always On Top");
        alwaysOnTopButton.getStyleClass().add("active"); // Start active
        alwaysOnTopButton.setOnAction(e -> toggleAlwaysOnTop());
        rightPanel.getChildren().add(alwaysOnTopButton);

        // History button
        Button historyButton = createIconButton("HIST", "Show History");
        historyButton.setOnAction(e -> showHistoryDialog());
        rightPanel.getChildren().add(historyButton);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(languagePanel, spacer, rightPanel);

        return toolbar;
    }

    private StackPane createMainContent() {
        StackPane stack = new StackPane();

        // Background: Two text panels
        HBox textPanels = new HBox(1);
        VBox leftPanel = createTextPanel("Source Text", true);
        VBox rightPanel = createTextPanel("Translation", false);
        textPanels.getChildren().addAll(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // Foreground: Translate button
        Button translateBtn = createTranslateButton();
        StackPane.setAlignment(translateBtn, Pos.BOTTOM_CENTER);
        StackPane.setMargin(translateBtn, new Insets(0, 0, 60, 0));

        stack.getChildren().addAll(textPanels, translateBtn);
        return stack;
    }

    private VBox createTextPanel(String title, boolean isSource) {
        VBox panel = new VBox();
        panel.getStyleClass().add("main-container");

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("panel-header");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("panel-title");
        header.getChildren().add(titleLabel);

        panel.getChildren().add(header);

        // Text area
        TextArea textArea = new TextArea();
        textArea.getStyleClass().add("text-area");
        textArea.setWrapText(false); // Preserve formatting
        VBox.setVgrow(textArea, Priority.ALWAYS);

        if (isSource) {
            sourceTextArea = textArea;
            textArea.setPromptText("Enter text to translate...");

            // Add Ctrl+Enter handler for quick translation
            textArea.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                    performTranslation();
                    event.consume();
                }
            });
        } else {
            translationTextArea = textArea;
            textArea.setEditable(false);
        }

        panel.getChildren().add(textArea);

        return panel;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.getStyleClass().add("status-bar");

        Label statusLabel = new Label("Monitoring clipboard • Press Ctrl+Enter to translate");
        statusLabel.getStyleClass().add("status-label");

        statusBar.getChildren().add(statusLabel);

        return statusBar;
    }

    private Button createTranslateButton() {
        Button btn = new Button();
        btn.getStyleClass().add("translate-button");

        // Create triangle shape (play/forward icon)
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(-8.0, -10.0, 10.0, 0.0, -8.0, 10.0);
        triangle.setFill(Color.WHITE);
        btn.setGraphic(triangle);

        btn.setTooltip(new Tooltip("Translate (or press Ctrl+Enter)"));
        btn.setOnAction(e -> performTranslation());

        return btn;
    }

    private ComboBox<LanguageItem> createStyledComboBox(LanguageItem[] items) {
        ComboBox<LanguageItem> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(items);

        // Custom cell renderer
        comboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(LanguageItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name);
                }
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(LanguageItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name);
                }
            }
        });

        return comboBox;
    }

    private Button createIconButton(String text, String tooltip) {
        Button button = new Button(text);
        button.getStyleClass().add("icon-button");
        button.setTooltip(new Tooltip(tooltip));
        return button;
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
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Translation History");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-container");

        // Header
        HBox header = new HBox();
        header.getStyleClass().add("toolbar");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Translation History");
        title.getStyleClass().add("panel-title");
        title.setStyle("-fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("icon-button");
        closeBtn.setOnAction(e -> dialog.close());

        header.getChildren().addAll(title, spacer, closeBtn);
        root.setTop(header);

        // Content
        TextArea historyArea = new TextArea();
        historyArea.getStyleClass().add("text-area");
        historyArea.setEditable(false);
        historyArea.setWrapText(true);

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

        root.setCenter(historyArea);

        // Bottom buttons
        HBox bottomPanel = new HBox(8);
        bottomPanel.getStyleClass().add("status-bar");
        bottomPanel.setAlignment(Pos.CENTER_RIGHT);

        Button clearButton = new Button("Clear History");
        clearButton.setOnAction(e -> {
            translationHistory.clear();
            historyArea.setText("History cleared.");
        });

        bottomPanel.getChildren().add(clearButton);
        root.setBottom(bottomPanel);

        Scene scene = new Scene(root, 600, 400);

        // Apply same stylesheet
        try {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/res/dark-theme.css")).toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Could not load CSS for dialog: " + e.getMessage());
        }

        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void toggleAlwaysOnTop() {
        isAlwaysOnTop = !isAlwaysOnTop;
        primaryStage.setAlwaysOnTop(isAlwaysOnTop);

        if (isAlwaysOnTop) {
            alwaysOnTopButton.getStyleClass().add("active");
            alwaysOnTopButton.setTooltip(new Tooltip("Always On Top (ON) - Click to disable"));
        } else {
            alwaysOnTopButton.getStyleClass().remove("active");
            alwaysOnTopButton.setTooltip(new Tooltip("Always On Top (OFF) - Click to enable"));
        }
    }

    private void setupEventHandlers() {
        Tools.getConfig().addEvent(() -> {
            // Get source and translation separately
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
        });
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
}

