# Personal Diary Manager - JavaFX GUI

A modern, feature-rich desktop application for managing your personal diary entries. Built with JavaFX, this application offers a clean, word-processor-like interface for writing, organizing, and securing your daily thoughts.

##  Features

*   **Modern User Interface**: A clean, responsive layout with a collapsible navigation bar and a professional look.
*   **Rich Text Editor**: A powerful editor with a Microsoft Word-style ribbon toolbar, supporting:
    *   **Formatting**: Bold, Italic, Underline, Strikethrough.
    *   **Alignment**: Left, Center, Right, Justify.
    *   **Lists**: Ordered (numbered) and Unordered (bulleted) lists.
    *   **Colors**: Custom font colors via a color picker.
    *   **Insert**: Tables, Charts (placeholder), and Shapes (placeholder).
*   **Entry Management**:
    *   Create, read, update, and delete diary entries.
    *   Search entries by title.
    *   Clear all history with a single click.
*   **Calendar View**: An interactive calendar to visualize your writing habits and navigate to entries by date.
*   **Statistics**: Track your writing progress with metrics like "Total Entries" and "Words Per Entry".
*   **Settings**:
    *   Toggle **Auto-Save** functionality.
    *   Adjust the default font size.
    *   Switch between **Light** and **Dark** themes.
*   **Profile**: A personalized profile section (currently a placeholder for sign-in).

## 🛠 Technical Architecture

The project follows the **Model-View-Controller (MVC)** architectural pattern to ensure separation of concerns and maintainability.

### 1. View (FXML & CSS)
*   **`MainDashboard.fxml`**: The main layout file defining the structure of the application. It uses a `BorderPane` for the overall layout, with a `StackPane` in the center to switch between different views (Writing, Calendar, Settings, Statistics).
*   **`styles.css`**: A comprehensive stylesheet that defines the visual appearance. It includes:
    *   Custom styling for the navigation bar and buttons.
    *   A "Dark Mode" theme that can be toggled dynamically.
    *   Specific styles for the custom ribbon toolbar and the `HTMLEditor` to give it a polished, modern look.

### 2. Controller (Java)
*   **`DashboardController.java`**: The brain of the application. It handles:
    *   User interactions (button clicks, navigation).
    *   Data binding between the view and the model.
    *   Logic for the rich text editor, including executing JavaScript commands for formatting.
    *   Calendar population and navigation logic.
    *   File I/O operations via the `FileManager`.

### 3. Model (Java)
*   **`DiaryEntry.java`**: Represents a single diary entry with a title, content, and timestamp.
*   **`EntryMetadata.java`**: A lightweight class used for listing entries efficiently, containing only the title and last modified date.
*   **`FileManager.java`**: Handles all file system operations using Java NIO.2 (`java.nio.file`). It saves entries as HTML files in a local `diary_entries` directory.

### 4. Main Application
*   **`MainApp.java`**: The entry point of the JavaFX application. It loads the FXML, applies the CSS, and sets up the primary stage (window).

## 🚀 How to Run

1.  **Prerequisites**: Ensure you have Java 21 (or later) and Maven installed.
2.  **Clone/Download**: Download the project source code.
3.  **Build**: Open a terminal in the project root and run:
    ```bash
    mvn clean javafx:run
    ```
4.  **Launch**: The application window will open.

## 📂 Project Structure

```
Chapter4_Challenge_DiaryManager_GUI/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/chapter4_challenge_diarymanager_gui/
│   │   │       ├── MainApp.java            # Application Entry Point
│   │   │       ├── DashboardController.java # UI Logic & Event Handling
│   │   │       ├── FileManager.java        # File I/O Operations
│   │   │       ├── DiaryEntry.java         # Data Model
│   │   │       └── EntryMetadata.java      # Lightweight Data Model
│   │   └── resources/
│   │       └── com/example/chapter4_challenge_diarymanager_gui/
│   │           ├── MainDashboard.fxml      # UI Layout
│   │           └── styles.css              # Styling & Themes
├── diary_entries/                          # Local storage for diary files
└── pom.xml                                 # Maven dependencies
```

## 🎨 CSS Styling Highlights

The application uses advanced JavaFX CSS to achieve its look:
*   **Custom Ribbon**: The `TabPane` and `ToolBar` are styled to mimic a modern office suite ribbon.
*   **Hidden Editor Chrome**: The default toolbars of the `HTMLEditor` are hidden (`-fx-opacity: 0`) to allow the custom ribbon to take control.
*   **Dark Mode**: A `.dark-mode` class is applied to the root container, which cascades changes to colors, backgrounds, and text throughout the app.

## 📝 Usage Guide

1.  **Writing**: Click the big green **+** button or "Write" in the sidebar. Enter a title and start typing. Use the ribbon to format your text.
2.  **Saving**: Click the "Save" button at the bottom right. Auto-save can be enabled in Settings.
3.  **Navigation**: Use the sidebar to switch between your daily writing, the calendar view, and your statistics.
4.  **Theme**: Toggle the "Moon/Sun" icon in the bottom left to switch themes.

---
*Developed as part of the Chapter 4 Challenge.*
