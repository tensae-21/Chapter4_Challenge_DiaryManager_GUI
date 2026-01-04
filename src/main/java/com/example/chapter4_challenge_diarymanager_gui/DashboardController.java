package com.example.chapter4_challenge_diarymanager_gui;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.Duration;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DashboardController {
    @FXML private BorderPane mainContainer;
    @FXML private StackPane contentArea;

    // Views
    @FXML private BorderPane writingView;
    @FXML private VBox calendarView;
    @FXML private VBox settingsView;
    @FXML private VBox statisticsView;

    // Writing View Components
    @FXML private VBox welcomePane;
    @FXML private BorderPane editorPane;
    @FXML private TextField titleField;
    @FXML private HTMLEditor contentEditor;
    @FXML private ListView<EntryMetadata> entryList;
    @FXML private Label statusLabel;

    // Search field in the sub-sidebar
    @FXML private TextField searchField;

    // Global Components
    @FXML private ToggleButton themeToggle;
    @FXML private GridPane calendarGrid;
    @FXML private Label calendarMonthLabel;
    @FXML private CheckBox autoSaveCheckbox;
    @FXML private ComboBox<Integer> fontSizeComboBox;

    // Icon Toolbar Buttons (New additions)
    @FXML private Button fileWordButton;
    @FXML private Button questionButton;
    @FXML private Button cogButton;
    @FXML private Button imageButton;
    @FXML private Button linkButton;
    @FXML private Button tableButton;
    @FXML private Button symbolButton;

    // Formatting Buttons
    @FXML private ToggleButton boldButton;
    @FXML private ToggleButton italicButton;
    @FXML private ToggleButton underlineButton;
    @FXML private ToggleButton justifyLeftButton;
    @FXML private ToggleButton justifyCenterButton;
    @FXML private ToggleButton justifyRightButton;
    @FXML private ToggleButton justifyJustifyButton;
    @FXML private ColorPicker fontColorPicker;

    // Statistics Components
    @FXML private Label totalEntriesLabel;
    @FXML private Label wordsPerEntryLabel;

    private final FileManager fileManager = new FileManager();
    private final PauseTransition autoSaveTimer = new PauseTransition(Duration.seconds(2));
    private boolean isAutoSaveEnabled = true;
    private YearMonth currentYearMonth;

    @FXML
    public void initialize() {
        setupEntryListCellFactory();
        loadEntryList();
        currentYearMonth = YearMonth.now();
        populateCalendar(currentYearMonth);

        // Initialize Settings
        fontSizeComboBox.setItems(FXCollections.observableArrayList(10, 12, 14, 16, 18));
        fontSizeComboBox.setValue(12);
        fontSizeComboBox.setOnAction(e -> contentEditor.setStyle("-fx-font-size: " + fontSizeComboBox.getValue() + "px;"));

        // Default view
        showWritingView();

        // Search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterEntries(newVal));

        // Entry Selection
        entryList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showEditor();
                isAutoSaveEnabled = false;
                loadEntryContent(newVal.getTitle());
                isAutoSaveEnabled = true;
            }
        });

        // Auto-save
        autoSaveTimer.setOnFinished(e -> {
            if (isAutoSaveEnabled) performAutoSave();
        });
        titleField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isAutoSaveEnabled && !newVal.isEmpty()) {
                statusLabel.setText("Unsaved changes...");
                autoSaveTimer.playFromStart();
            }
        });

        // Custom Format Actions
        setupCustomFormatActions();
    }

    private void setupCustomFormatActions() {
        WebView webView = (WebView) contentEditor.lookup(".web-view");
        if (webView != null) {
            // Formatting buttons
            boldButton.setOnAction(e -> webView.getEngine().executeScript("document.execCommand('bold', false, null)"));
            italicButton.setOnAction(e -> webView.getEngine().executeScript("document.execCommand('italic', false, null)"));
            underlineButton.setOnAction(e -> webView.getEngine().executeScript("document.execCommand('underline', false, null)"));

            // Alignment buttons
            justifyLeftButton.setOnAction(e -> webView.getEngine().executeScript("document.execCommand('justifyLeft', false, null)"));
            justifyCenterButton.setOnAction(e -> webView.getEngine().executeScript("document.execCommand('justifyCenter', false, null)"));
            justifyRightButton.setOnAction(e -> webView.getEngine().executeScript("document.execCommand('justifyRight', false, null)"));
            justifyJustifyButton.setOnAction(e -> webView.getEngine().executeScript("document.execCommand('justifyFull', false, null)"));

            // Color picker
            fontColorPicker.setOnAction(e -> {
                Color color = fontColorPicker.getValue();
                String hexColor = String.format("#%02X%02X%02X",
                        (int) (color.getRed() * 255),
                        (int) (color.getGreen() * 255),
                        (int) (color.getBlue() * 255));
                webView.getEngine().executeScript("document.execCommand('forecolor', false, '" + hexColor + "')");
            });
        }
    }

    // --- Icon Toolbar Handler Methods ---
    @FXML
    public void handleFileWord() {
        statusLabel.setText("Export to Word feature");
        showAlert("Export", "This would export the current entry to Word format.", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleQuestionCircle() {
        statusLabel.setText("Help");
        showAlert("Help", "Diary Editor Help:\n\n• Use the toolbar icons to format your text\n• Click on icons to apply formatting\n• Select text before applying formatting for best results", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleCog() {
        statusLabel.setText("Editor Settings");
        showAlert("Editor Settings", "This would open editor-specific settings.", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void handleImage() {
        WebView webView = (WebView) contentEditor.lookup(".web-view");
        if (webView != null) {
            TextInputDialog dialog = new TextInputDialog("https://picsum.photos/400/300");
            dialog.setTitle("Insert Image");
            dialog.setHeaderText("Enter image URL");
            dialog.setContentText("URL:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(url -> {
                String html = "<img src=\"" + url + "\" alt=\"Image\" style=\"max-width: 100%;\">";
                webView.getEngine().executeScript("document.execCommand('insertHTML', false, '" + html + "')");
                statusLabel.setText("Image inserted");
            });
        }
    }

    @FXML
    public void handleLink() {
        WebView webView = (WebView) contentEditor.lookup(".web-view");
        if (webView != null) {
            TextInputDialog dialog = new TextInputDialog("https://example.com");
            dialog.setTitle("Insert Link");
            dialog.setHeaderText("Enter URL");
            dialog.setContentText("URL:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(url -> {
                webView.getEngine().executeScript("document.execCommand('createLink', false, '" + url + "')");
                statusLabel.setText("Link inserted");
            });
        }
    }

    @FXML
    public void handleTable() {
        WebView webView = (WebView) contentEditor.lookup(".web-view");
        if (webView != null) {
            TextInputDialog rowsDialog = new TextInputDialog("3");
            rowsDialog.setTitle("Insert Table");
            rowsDialog.setHeaderText("Enter number of rows");
            rowsDialog.setContentText("Rows:");

            Optional<String> rowsResult = rowsDialog.showAndWait();
            rowsResult.ifPresent(rowsStr -> {
                TextInputDialog colsDialog = new TextInputDialog("3");
                colsDialog.setTitle("Insert Table");
                colsDialog.setHeaderText("Enter number of columns");
                colsDialog.setContentText("Columns:");

                Optional<String> colsResult = colsDialog.showAndWait();
                colsResult.ifPresent(colsStr -> {
                    try {
                        int rows = Integer.parseInt(rowsStr);
                        int cols = Integer.parseInt(colsStr);
                        String tableHtml = createTableHTML(rows, cols);
                        webView.getEngine().executeScript("document.execCommand('insertHTML', false, '" + tableHtml + "')");
                        statusLabel.setText("Table inserted: " + rows + "x" + cols);
                    } catch (NumberFormatException e) {
                        showAlert("Error", "Please enter valid numbers", Alert.AlertType.ERROR);
                    }
                });
            });
        }
    }

    @FXML
    public void handleSymbol() {
        WebView webView = (WebView) contentEditor.lookup(".web-view");
        if (webView != null) {
            TextInputDialog dialog = new TextInputDialog("★ ☆ ♥ ♦ ♣ ♠");
            dialog.setTitle("Insert Symbol");
            dialog.setHeaderText("Enter symbol(s) to insert");
            dialog.setContentText("Symbol:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(symbol -> {
                webView.getEngine().executeScript("document.execCommand('insertText', false, '" + symbol + "')");
                statusLabel.setText("Symbol inserted");
            });
        }
    }

    private String createTableHTML(int rows, int cols) {
        StringBuilder tableHTML = new StringBuilder("<table style='border-collapse: collapse; width: 100%; margin: 10px 0; border: 1px solid #ddd;'>");
        for (int i = 0; i < rows; i++) {
            tableHTML.append("<tr>");
            for (int j = 0; j < cols; j++) {
                tableHTML.append("<td style='border: 1px solid #ddd; padding: 8px;'>Cell ").append(i + 1).append("-").append(j + 1).append("</td>");
            }
            tableHTML.append("</tr>");
        }
        tableHTML.append("</table>");
        return tableHTML.toString();
    }

    // ... [Keep all the other existing methods exactly as they were] ...

    // Rest of your existing code remains exactly the same
    // Only add the new handler methods above and keep everything else

    private void setupEntryListCellFactory() {
        entryList.setCellFactory(param -> new ListCell<EntryMetadata>() {
            @Override
            protected void updateItem(EntryMetadata item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String dateStr = item.getLastModified().format(DateTimeFormatter.ofPattern("dd MMM"));
                    VBox vBox = new VBox(0);
                    Label titleLabel = new Label(item.getTitle());
                    titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: inherit; -fx-font-size: 12px;");
                    Label dateLabel = new Label(dateStr);
                    dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: inherit; -fx-opacity: 0.7;");
                    vBox.getChildren().addAll(titleLabel, dateLabel);
                    setGraphic(vBox);
                }
            }
        });
    }

    // --- Navigation ---
    @FXML public void showWritingView() {
        writingView.setVisible(true);
        calendarView.setVisible(false);
        settingsView.setVisible(false);
        statisticsView.setVisible(false);
        welcomePane.setVisible(true);
        editorPane.setVisible(false);
    }

    @FXML public void showCalendarView() {
        writingView.setVisible(false);
        calendarView.setVisible(true);
        settingsView.setVisible(false);
        statisticsView.setVisible(false);
        populateCalendar(currentYearMonth);
    }

    @FXML public void showSettingsView() {
        writingView.setVisible(false);
        calendarView.setVisible(false);
        settingsView.setVisible(true);
        statisticsView.setVisible(false);
    }

    @FXML public void showStatisticsView() {
        writingView.setVisible(false);
        calendarView.setVisible(false);
        settingsView.setVisible(false);
        statisticsView.setVisible(true);
        updateStatistics();
    }

    // --- Actions ---
    @FXML public void handleEditProfile() {
        TextInputDialog dialog = new TextInputDialog("Tensae");
        dialog.setTitle("Sign In");
        dialog.setHeaderText("Enter your username to sign in.");
        dialog.setContentText("Username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> showAlert("Signed In", "Welcome, " + name + "!", Alert.AlertType.INFORMATION));
    }

    @FXML public void handleToday() {
        showWritingView();
        handleNewEntry();
        titleField.setText("Morning Reflection - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        contentEditor.setHtmlText("<h3>Morning Reflection</h3><p><b>1. How am I feeling today?</b></p><p>...</p><p><b>2. What is my main goal?</b></p><p>...</p>");
    }

    @FXML public void handlePlus() {
        showEditor();
        handleNewEntry();
    }

    @FXML public void handleBack() {
        entryList.getSelectionModel().clearSelection();
        welcomePane.setVisible(true);
        editorPane.setVisible(false);
    }

    @FXML public void handleAutoSaveToggle() {
        isAutoSaveEnabled = autoSaveCheckbox.isSelected();
        showAlert("Settings", "Auto-save has been " + (isAutoSaveEnabled ? "enabled" : "disabled"), Alert.AlertType.INFORMATION);
    }

    private void showEditor() {
        welcomePane.setVisible(false);
        editorPane.setVisible(true);
    }

    // --- Calendar Logic ---
    @FXML private void handlePrevMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);
    }

    @FXML private void handleNextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);
    }

    private void populateCalendar(YearMonth yearMonth) {
        calendarMonthLabel.setText(yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        calendarGrid.getChildren().clear();

        try {
            Set<LocalDate> entryDates = fileManager.listEntriesMetadata().stream()
                    .map(e -> e.getLastModified().toLocalDate())
                    .collect(Collectors.toSet());

            String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (int i = 0; i < 7; i++) {
                Label header = new Label(days[i]);
                header.getStyleClass().add("calendar-day-header");
                calendarGrid.add(header, i, 0);
            }

            LocalDate firstOfMonth = yearMonth.atDay(1);
            int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
            int daysInMonth = yearMonth.lengthOfMonth();
            int row = 1;
            int col = dayOfWeek - 1;

            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = yearMonth.atDay(day);
                Label dayLabel = new Label(String.valueOf(day));
                dayLabel.getStyleClass().add("calendar-day");
                if (date.equals(LocalDate.now())) dayLabel.getStyleClass().add("today");
                if (entryDates.contains(date)) dayLabel.getStyleClass().add("has-entry");

                dayLabel.setOnMouseClicked(e -> {
                    for (EntryMetadata entry : entryList.getItems()) {
                        if (entry.getLastModified().toLocalDate().equals(date)) {
                            showWritingView();
                            entryList.getSelectionModel().select(entry);
                            break;
                        }
                    }
                });

                dayLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                calendarGrid.add(dayLabel, col, row);
                col = (col + 1) % 7;
                if (col == 0) row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Statistics Logic ---
    private void updateStatistics() {
        try {
            List<EntryMetadata> entries = fileManager.listEntriesMetadata();
            totalEntriesLabel.setText(String.valueOf(entries.size()));

            long totalWords = 0;
            for (EntryMetadata entry : entries) {
                String content = fileManager.readEntry(entry.getTitle());
                totalWords += content.replaceAll("<[^>]*>", "").trim().split("\\s+").length;
            }

            double wordsPerEntry = (entries.isEmpty()) ? 0 : (double) totalWords / entries.size();
            wordsPerEntryLabel.setText(String.format("%.1f", wordsPerEntry));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- File Operations ---
    private void performAutoSave() {
        String title = titleField.getText();
        if (title == null || title.trim().isEmpty()) return;
        statusLabel.setText("Auto-saving...");
        Task<Void> saveTask = new Task<>() {
            @Override protected Void call() throws Exception {
                fileManager.saveEntry(new DiaryEntry(title, contentEditor.getHtmlText()));
                return null;
            }
        };
        saveTask.setOnSucceeded(e -> {
            statusLabel.setText("Saved");
            loadEntryList();
        });
        new Thread(saveTask).start();
    }

    @FXML public void handleSave() { performAutoSave(); }

    @FXML public void handleDelete() {
        EntryMetadata selected = entryList.getSelectionModel().getSelectedItem();
        String titleToDelete = (selected != null) ? selected.getTitle() : titleField.getText();
        if (titleToDelete == null || titleToDelete.isEmpty()) return;
        try {
            fileManager.deleteEntry(titleToDelete);
            loadEntryList();
            handleNewEntry();
            handleBack();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML public void handleNewEntry() {
        isAutoSaveEnabled = false;
        titleField.clear();
        contentEditor.setHtmlText("");
        entryList.getSelectionModel().clearSelection();
        statusLabel.setText("New Entry");
        isAutoSaveEnabled = true;
    }

    @FXML public void handleClearHistory() {
        try {
            fileManager.clearAllEntries();
            loadEntryList();
            handleBack();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadEntryList() {
        try {
            EntryMetadata current = entryList.getSelectionModel().getSelectedItem();
            List<EntryMetadata> entries = fileManager.listEntriesMetadata();
            entryList.getItems().setAll(entries);
            if (current != null) {
                for (EntryMetadata item : entryList.getItems()) {
                    if (item.getTitle().equals(current.getTitle())) {
                        entryList.getSelectionModel().select(item);
                        break;
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadEntryContent(String title) {
        try {
            titleField.setText(title);
            contentEditor.setHtmlText(fileManager.readEntry(title));
            statusLabel.setText("Loaded: " + title);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void filterEntries(String query) {
        try {
            entryList.getItems().setAll(fileManager.listEntriesMetadata().stream()
                    .filter(m -> m.getTitle().toLowerCase().contains(query.toLowerCase()))
                    .toList());
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML public void handleThemeToggle() {
        if (themeToggle.isSelected()) {
            mainContainer.getStylesheets().add(getClass().getResource("dark-theme.css").toExternalForm());
            mainContainer.getStyleClass().add("dark-mode");
            themeToggle.setText("☀️ Light");
        } else {
            mainContainer.getStyleClass().remove("dark-mode");
            themeToggle.setText("🌙 Dark");
        }
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.show();
    }
}