# JavaFX Migration Plan for Universal Translator

## Executive Summary

This document outlines a comprehensive plan to migrate the Universal Translator application from Java Swing to JavaFX. The application is a multi-language translator with clipboard monitoring, featuring a modern dark theme UI.

---

## Current Architecture Analysis

### Technology Stack
- **Java Version**: 11
- **Build Tool**: Maven
- **UI Framework**: Java Swing
- **Dependencies**: 
  - Gson 2.10.1 (JSON parsing)
  - Java HTTP Client (translation API)
  - AWT Clipboard API

### Core Components
1. **Window.java** (670 lines) - Main UI with Swing components
2. **Main.java** - Application entry point
3. **ClipBoard.java** - Clipboard monitoring thread
4. **GoogleTranslateClient.java** - Translation API client
5. **Tools.java** - Configuration and event management
6. **Event.java** - Event callback interface

### Key Features to Preserve
- ✓ Multi-language translation (18+ languages)
- ✓ Clipboard monitoring with auto-translate
- ✓ Dark theme (IntelliJ-inspired)
- ✓ Always-on-top toggle
- ✓ Translation history
- ✓ Language selection dropdowns
- ✓ Unicode/multi-script font support
- ✓ Resource loading (icons, fonts, JSON)
- ✓ Manual translation with Ctrl+Enter

---

## Migration Strategy

### Phase 1: Setup & Dependencies (Day 1)

#### 1.1 Update pom.xml
Add JavaFX dependencies while maintaining existing libraries:

```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <javafx.version>21.0.1</javafx.version>
</properties>

<dependencies>
    <!-- Existing dependency -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
    
    <!-- JavaFX Core Modules -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>${javafx.version}</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>${javafx.version}</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-graphics</artifactId>
        <version>${javafx.version}</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- JavaFX Maven Plugin -->
        <plugin>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-maven-plugin</artifactId>
            <version>0.0.8</version>
            <configuration>
                <mainClass>com.iishanto.Main</mainClass>
            </configuration>
        </plugin>
        
        <!-- Keep existing shade plugin for JAR packaging -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.0</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <transformers>
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>com.iishanto.Main</mainClass>
                            </transformer>
                        </transformers>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

#### 1.2 Create JavaFX CSS Stylesheet
Create `src/res/dark-theme.css` for consistent styling:

```css
/* Root colors - IntelliJ Dark Theme */
.root {
    -fx-background-primary: #2B2B2B;
    -fx-background-secondary: #3C3F41;
    -fx-background-tertiary: #45494A;
    -fx-text-primary: #BBBBBB;
    -fx-text-secondary: #808080;
    -fx-accent-blue: #4C92CC;
    -fx-accent-hover: #5EA1D6;
    -fx-border-color: #222222;
    -fx-selection-bg: #264F78;
}

.root {
    -fx-base: #2B2B2B;
    -fx-background: #2B2B2B;
    -fx-control-inner-background: #2B2B2B;
    -fx-font-family: "System";
    -fx-font-size: 14px;
}

/* Main application background */
.main-container {
    -fx-background-color: #2B2B2B;
}

/* Toolbar styling */
.toolbar {
    -fx-background-color: #3C3F41;
    -fx-padding: 8 12 8 12;
}

/* Text areas */
.text-area {
    -fx-background-color: #2B2B2B;
    -fx-text-fill: #BBBBBB;
    -fx-prompt-text-fill: #808080;
    -fx-highlight-fill: #264F78;
    -fx-highlight-text-fill: #BBBBBB;
    -fx-font-size: 14px;
}

.text-area .content {
    -fx-background-color: #2B2B2B;
}

/* ComboBox styling */
.combo-box {
    -fx-background-color: #45494A;
    -fx-text-fill: #BBBBBB;
    -fx-border-color: #222222;
}

.combo-box .list-cell {
    -fx-background-color: #45494A;
    -fx-text-fill: #BBBBBB;
}

.combo-box-popup .list-view {
    -fx-background-color: #45494A;
}

.combo-box-popup .list-view .list-cell:hover {
    -fx-background-color: #264F78;
}

/* Buttons */
.button {
    -fx-background-color: #4C92CC;
    -fx-text-fill: white;
    -fx-font-weight: bold;
    -fx-background-radius: 8;
    -fx-cursor: hand;
}

.button:hover {
    -fx-background-color: #5EA1D6;
}

.button:pressed {
    -fx-background-color: #3A7AB8;
}

/* Icon buttons */
.icon-button {
    -fx-background-color: transparent;
    -fx-text-fill: #BBBBBB;
    -fx-background-radius: 8;
}

.icon-button:hover {
    -fx-background-color: #2B2B2B;
}

/* Translate button */
.translate-button {
    -fx-background-color: #4C92CC;
    -fx-background-radius: 30;
    -fx-pref-width: 60;
    -fx-pref-height: 60;
    -fx-min-width: 60;
    -fx-min-height: 60;
    -fx-cursor: hand;
}

.translate-button:hover {
    -fx-background-color: #5EA1D6;
}

/* ScrollBar styling */
.scroll-bar {
    -fx-background-color: #2B2B2B;
}

.scroll-bar .thumb {
    -fx-background-color: #45494A;
    -fx-background-radius: 4;
}

.scroll-bar .increment-button,
.scroll-bar .decrement-button {
    -fx-opacity: 0;
    -fx-pref-height: 0;
    -fx-pref-width: 0;
}

/* Status bar */
.status-bar {
    -fx-background-color: #3C3F41;
    -fx-padding: 4 12 4 12;
}

.status-label {
    -fx-text-fill: #808080;
    -fx-font-size: 11px;
}

/* Panel headers */
.panel-header {
    -fx-background-color: #45494A;
    -fx-padding: 8 12 8 12;
}

.panel-title {
    -fx-text-fill: #BBBBBB;
    -fx-font-weight: bold;
    -fx-font-size: 12px;
}
```

---

### Phase 2: Core UI Migration (Days 2-3)

#### 2.1 Create WindowFX.java (JavaFX equivalent)

**Key Mappings:**
| Swing Component | JavaFX Component | Notes |
|----------------|------------------|-------|
| JFrame | Stage | Main window |
| JPanel | Pane/VBox/HBox/BorderPane | Layout containers |
| JTextArea | TextArea | Text input/display |
| JComboBox | ComboBox | Dropdown lists |
| JButton | Button | Clickable buttons |
| JScrollPane | ScrollPane | Scrollable content |
| JLabel | Label | Text labels |
| JDialog | Stage (MODALITY) | Modal dialogs |

**Structure:**
```java
public class WindowFX extends Application {
    private Stage primaryStage;
    private ComboBox<LanguageItem> sourceLanguageCombo;
    private ComboBox<LanguageItem> targetLanguageCombo;
    private TextArea sourceTextArea;
    private TextArea translationTextArea;
    private List<String> translationHistory = new ArrayList<>();
    private boolean isAlwaysOnTop = true;
    
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Universal Translator");
        primaryStage.setAlwaysOnTop(true);
        
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
        scene.getStylesheets().add(getClass().getResource("/res/dark-theme.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(100);
        primaryStage.show();
        
        setupEventHandlers();
    }
    
    // Additional methods...
}
```

#### 2.2 Update Main.java

**Current Swing initialization:**
```java
public static void main(String[] args) {
    GoogleTranslateClient googleTranslateClient = GoogleTranslateClient.getInstance();
    new Window();
    new ClipBoard();
}
```

**New JavaFX initialization:**
```java
public static void main(String[] args) {
    GoogleTranslateClient googleTranslateClient = GoogleTranslateClient.getInstance();
    new ClipBoard();
    Application.launch(WindowFX.class, args);
}
```

---

### Phase 3: Component-by-Component Migration (Days 4-5)

#### 3.1 Toolbar Migration

**Swing Code (Current):**
```java
JPanel toolbar = new JPanel(new BorderLayout(10, 0));
JComboBox<LanguageItem> sourceLanguageCombo = new JComboBox<>(languages);
```

**JavaFX Code (Target):**
```java
HBox toolbar = new HBox(10);
toolbar.getStyleClass().add("toolbar");
toolbar.setAlignment(Pos.CENTER_LEFT);

ComboBox<LanguageItem> sourceLanguageCombo = new ComboBox<>();
sourceLanguageCombo.getItems().addAll(languages);
sourceLanguageCombo.setCellFactory(lv -> new LanguageListCell());
sourceLanguageCombo.setButtonCell(new LanguageListCell());
```

#### 3.2 Text Areas Migration

**Key Differences:**
- JavaFX TextArea uses `textProperty()` for listeners instead of KeyListener
- Font loading is done via CSS `-fx-font-family` or Font.loadFont()
- Selection color via CSS instead of Java methods

**JavaFX Implementation:**
```java
TextArea sourceTextArea = new TextArea();
sourceTextArea.setPromptText("Enter text to translate...");
sourceTextArea.setWrapText(false); // Preserve formatting

// Ctrl+Enter handler
sourceTextArea.setOnKeyPressed(event -> {
    if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
        performTranslation();
        event.consume();
    }
});
```

#### 3.3 Center Translate Button with StackPane

**JavaFX StackPane Approach:**
```java
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

private Button createTranslateButton() {
    Button btn = new Button();
    btn.getStyleClass().add("translate-button");
    
    // Create triangle shape
    Polygon triangle = new Polygon();
    triangle.getPoints().addAll(-8.0, -10.0, 10.0, 0.0, -8.0, 10.0);
    triangle.setFill(Color.WHITE);
    btn.setGraphic(triangle);
    
    btn.setOnAction(e -> performTranslation());
    return btn;
}
```

#### 3.4 Modal Dialog Migration

**History Dialog in JavaFX:**
```java
private void showHistoryDialog() {
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.setTitle("Translation History");
    
    BorderPane root = new BorderPane();
    root.getStyleClass().add("main-container");
    
    // Header
    HBox header = new HBox();
    header.getStyleClass().add("toolbar");
    Label title = new Label("Translation History");
    Button closeBtn = new Button("✕");
    closeBtn.setOnAction(e -> dialog.close());
    // ... layout code
    
    // Content
    TextArea historyArea = new TextArea();
    historyArea.setEditable(false);
    historyArea.setText(formatHistory());
    
    Scene scene = new Scene(root, 600, 400);
    scene.getStylesheets().add(getClass().getResource("/res/dark-theme.css").toExternalForm());
    dialog.setScene(scene);
    dialog.showAndWait();
}
```

---

### Phase 4: Resource Loading & Assets (Day 6)

#### 4.1 Icon Loading

**Swing:**
```java
InputStream iconStream = Tools.getConfig().getRes("/icon.png");
mainFrame.setIconImage(ImageIO.read(iconStream));
```

**JavaFX:**
```java
InputStream iconStream = Tools.getConfig().getRes("/icon.png");
if (iconStream != null) {
    Image icon = new Image(iconStream);
    primaryStage.getIcons().add(icon);
}
```

#### 4.2 Font Loading

**Swing:**
```java
Font baseFont = new Font("SansSerif", Font.PLAIN, 14);
```

**JavaFX:**
```java
// Load custom font
InputStream fontStream = getClass().getResourceAsStream("/res/kalpurush.ttf");
Font customFont = Font.loadFont(fontStream, 14);

// Or use system fonts with fallback
Font universalFont = Font.font("System", 14);
```

**Apply via CSS:**
```css
.text-area {
    -fx-font-family: "Kalpurush", "Noto Sans", "System";
    -fx-font-size: 14px;
}
```

#### 4.3 JSON Resource Loading

No changes needed - Gson and Tools.getRes() work the same:
```java
InputStream langStream = Tools.getConfig().getRes("/languages.json");
JsonObject jsonObject = gson.fromJson(new InputStreamReader(langStream), JsonObject.class);
```

---

### Phase 5: Event Handling & Threading (Day 7)

#### 5.1 Clipboard Monitoring Integration

**Key Consideration:** JavaFX UI updates must happen on the JavaFX Application Thread

**Current ClipBoard.java uses Swing EDT:**
```java
// No explicit EDT handling in current code
```

**JavaFX requires Platform.runLater():**
```java
// In ClipBoard.java - add import
import javafx.application.Platform;

// When calling event (around line 25)
Tools.getConfig().regNewText(text);
Platform.runLater(() -> {
    Tools.getConfig().callEvent("new_text");
});
```

#### 5.2 Event Handler in WindowFX

**Swing version (current):**
```java
Tools.getConfig().addEvent(new Event() {
    @Override
    public void event() {
        String source = Tools.getConfig().getLatestSource();
        String translation = Tools.getConfig().getLatestTranslation();
        sourceTextArea.setText(source);
        translationTextArea.setText(translation);
    }
});
```

**JavaFX version (no changes needed if Platform.runLater used in ClipBoard):**
```java
Tools.getConfig().addEvent(new Event() {
    @Override
    public void event() {
        String source = Tools.getConfig().getLatestSource();
        String translation = Tools.getConfig().getLatestTranslation();
        
        // Already on FX thread due to Platform.runLater in ClipBoard
        sourceTextArea.setText(source);
        translationTextArea.setText(translation);
        
        translationHistory.add(Tools.getConfig().getLatestTranslationFormatted());
        if (translationHistory.size() > 50) {
            translationHistory.remove(0);
        }
    }
});
```

---

### Phase 6: Advanced Features (Day 8)

#### 6.1 Always-On-Top Toggle

**JavaFX Implementation:**
```java
private void toggleAlwaysOnTop(Button button) {
    isAlwaysOnTop = !isAlwaysOnTop;
    primaryStage.setAlwaysOnTop(isAlwaysOnTop);
    
    if (isAlwaysOnTop) {
        button.setStyle("-fx-text-fill: #4C92CC;");
        button.setTooltip(new Tooltip("Always On Top (ON)"));
    } else {
        button.setStyle("-fx-text-fill: #808080;");
        button.setTooltip(new Tooltip("Always On Top (OFF)"));
    }
}
```

#### 6.2 Custom ComboBox Rendering

**JavaFX Cell Factory:**
```java
private ComboBox<LanguageItem> createStyledComboBox(LanguageItem[] items) {
    ComboBox<LanguageItem> comboBox = new ComboBox<>();
    comboBox.getItems().addAll(items);
    
    // Custom cell renderer
    comboBox.setCellFactory(lv -> new ListCell<LanguageItem>() {
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
    
    comboBox.setButtonCell(new ListCell<LanguageItem>() {
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
```

#### 6.3 Rounded Button with Custom Painting

**Using CSS (Recommended):**
```css
.translate-button {
    -fx-background-radius: 30;
    -fx-background-color: #4C92CC;
}
```

**Using Shape (Alternative):**
```java
Button btn = new Button();
Circle clip = new Circle(30);
clip.setCenterX(30);
clip.setCenterY(30);
btn.setClip(clip);
```

---

### Phase 7: Testing & Validation (Day 9-10)

#### 7.1 Testing Checklist

- [ ] **Window Functionality**
  - [ ] Window opens with correct size (600x300)
  - [ ] Window can be resized (min 600x100)
  - [ ] Window icon loads correctly
  - [ ] Always-on-top toggle works
  - [ ] Window maintains dark theme

- [ ] **Language Selection**
  - [ ] Source language dropdown loads 18+ languages
  - [ ] Target language dropdown loads 18+ languages
  - [ ] Default: Auto → Bengali
  - [ ] Language changes persist during session

- [ ] **Translation Features**
  - [ ] Manual translation via center button
  - [ ] Ctrl+Enter hotkey works
  - [ ] Clipboard monitoring auto-translates
  - [ ] Multi-line text preserves formatting
  - [ ] Tab characters are preserved
  - [ ] Unicode/multi-script text displays correctly

- [ ] **History Dialog**
  - [ ] Opens as modal dialog
  - [ ] Displays last 50 translations
  - [ ] Clear history button works
  - [ ] Scrollable content
  - [ ] Dark theme consistent

- [ ] **Performance**
  - [ ] No UI freezing during translation
  - [ ] Clipboard monitoring runs smoothly
  - [ ] Memory usage acceptable
  - [ ] JAR builds and runs standalone

#### 7.2 Migration Testing Script

```bash
#!/bin/bash
# Test JavaFX migration

echo "Building JavaFX version..."
mvn clean package

echo "Testing JAR execution..."
java -jar target/translator-1.0.jar

echo "Testing with JavaFX runtime..."
mvn javafx:run

echo "Checking for Swing dependencies..."
jar tf target/translator-1.0.jar | grep -i "swing"

echo "Migration test complete!"
```

---

## Detailed Component Mapping Reference

### Layout Managers

| Swing | JavaFX | Usage |
|-------|--------|-------|
| BorderLayout | BorderPane | Top/Bottom/Left/Right/Center |
| FlowLayout | FlowPane / HBox | Horizontal flow |
| GridLayout | GridPane | Grid structure |
| BoxLayout | VBox / HBox | Vertical/Horizontal |
| null (absolute) | Pane + layoutX/Y | Absolute positioning |

### Event Handling

| Swing | JavaFX | Example |
|-------|--------|---------|
| ActionListener | EventHandler<ActionEvent> | Button clicks |
| KeyListener | setOnKeyPressed/Typed/Released | Keyboard input |
| MouseListener | setOnMouseClicked/Entered/Exited | Mouse events |
| ComponentListener | widthProperty().addListener() | Resize events |

### Styling Comparison

| Aspect | Swing | JavaFX |
|--------|-------|--------|
| Colors | `new Color(r,g,b)` | `Color.rgb(r,g,b)` or CSS |
| Fonts | `new Font(name, style, size)` | `Font.font(family, size)` or CSS |
| Borders | `BorderFactory` | CSS `-fx-border-*` |
| Background | `setBackground(color)` | CSS `-fx-background-color` |
| Padding | `EmptyBorder` | CSS `-fx-padding` or Insets |

---

## Potential Challenges & Solutions

### Challenge 1: System Tray Support
**Issue:** JavaFX doesn't have native system tray support
**Solution:** Continue using AWT's `SystemTray` for tray icon if needed

### Challenge 2: Module System (Java 9+)
**Issue:** JavaFX requires module configuration for Java 9+
**Solution:** Add `module-info.java` if targeting Java 11+:
```java
module com.iishanto.translator {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;
    requires java.desktop; // For AWT clipboard
    
    exports com.iishanto;
}
```

### Challenge 3: Font Rendering Differences
**Issue:** JavaFX font rendering may differ from Swing
**Solution:** Test with all target languages, adjust font fallback chain in CSS

### Challenge 4: ScrollPane Behavior
**Issue:** JavaFX ScrollPane has different scroll behavior
**Solution:** Configure viewport bounds and scroll policy:
```java
scrollPane.setFitToWidth(false);
scrollPane.setFitToHeight(false);
scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
```

### Challenge 5: Clipboard Monitoring with JavaFX
**Issue:** Need to ensure thread safety
**Solution:** Always wrap UI updates in `Platform.runLater()`

---

## Migration Timeline

| Phase | Duration | Deliverable |
|-------|----------|-------------|
| Phase 1: Setup | 1 day | Updated pom.xml, CSS file |
| Phase 2: Core UI | 2 days | WindowFX.java basic structure |
| Phase 3: Components | 2 days | All UI components migrated |
| Phase 4: Resources | 1 day | Icons, fonts, JSON loading |
| Phase 5: Events | 1 day | Clipboard + event handling |
| Phase 6: Advanced | 1 day | Custom styling, behaviors |
| Phase 7: Testing | 2 days | Full QA, bug fixes |
| **Total** | **10 days** | Production-ready JavaFX app |

---

## Post-Migration Benefits

### Performance
- **Hardware acceleration** - JavaFX uses GPU rendering
- **Better memory management** - Modern rendering pipeline
- **Smoother animations** - Built-in transition support

### Development
- **CSS styling** - Easier theming and maintenance
- **FXML support** - Declarative UI definition option
- **Better separation** - Cleaner MVC architecture
- **Scene graph** - More intuitive component hierarchy

### Features
- **Modern controls** - TabPane, TreeView, TableView
- **Built-in effects** - Shadows, blur, lighting
- **Charts & media** - Rich visualization options
- **Web integration** - WebView component available

### Cross-platform
- **Consistent look** - Better cross-platform consistency
- **Active development** - OpenJFX actively maintained
- **Future-proof** - Swing is in maintenance mode

---

## Rollback Plan

If migration issues arise:

1. **Keep Swing version**: Maintain `Window.java` alongside `WindowFX.java`
2. **Conditional launch**: Use system property to choose UI:
   ```java
   if (System.getProperty("use.javafx", "true").equals("true")) {
       Application.launch(WindowFX.class, args);
   } else {
       new Window();
   }
   ```
3. **Separate branches**: Keep Swing in `main`, JavaFX in `javafx-migration`
4. **Separate artifacts**: Build both JARs: `translator-swing-1.0.jar`, `translator-javafx-1.0.jar`

---

## Code Structure After Migration

```
src/
├── com/iishanto/
│   ├── Main.java (modified for JavaFX)
│   ├── WindowFX.java (new - replaces Window.java)
│   ├── Window.java (kept for reference/rollback)
│   ├── ClipBoard.java (modified - Platform.runLater)
│   ├── GoogleTranslateClient.java (no changes)
│   ├── Tools.java (no changes)
│   └── Event.java (no changes)
├── res/
│   ├── dark-theme.css (new)
│   ├── db.csv (existing)
│   ├── icon.png (existing)
│   ├── kalpurush.ttf (existing)
│   ├── languages.json (existing)
│   └── logo.png (existing)
└── module-info.java (optional for Java 9+)
```

---

## Additional Resources

### Documentation
- [OpenJFX Documentation](https://openjfx.io/openjfx-docs/)
- [JavaFX CSS Reference](https://openjfx.io/javafx-docs/21/javafx.graphics/javafx/scene/doc-files/cssref.html)
- [Swing to JavaFX Guide](https://docs.oracle.com/javase/8/javafx/interoperability-tutorial/swing-fx-interoperability.htm)

### Tools
- **Scene Builder** - Visual FXML editor (if using FXML approach)
- **JFXSceneBuilder** - Alternative Scene Builder fork
- **JavaFX Maven Plugin** - For running/packaging JavaFX apps

### Testing
- **TestFX** - JavaFX testing framework for automated UI tests
- **Monocle** - Headless testing for JavaFX

---

## Conclusion

This migration plan provides a structured approach to converting the Universal Translator from Swing to JavaFX while preserving all existing functionality. The estimated 10-day timeline accounts for thorough testing and potential adjustments.

### Key Success Factors:
1. ✓ Incremental migration (keep Swing working)
2. ✓ Thread safety with Platform.runLater()
3. ✓ CSS-based styling for maintainability
4. ✓ Comprehensive testing of all features
5. ✓ Performance validation

### Next Steps:
1. Review and approve this plan
2. Set up development branch
3. Begin Phase 1 (Setup & Dependencies)
4. Daily progress review
5. Final migration and deployment

---

**Document Version:** 1.0  
**Date:** November 12, 2025  
**Author:** Migration Planning Team  
**Status:** Ready for Implementation

