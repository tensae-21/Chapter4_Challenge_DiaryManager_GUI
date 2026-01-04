module com.example.chapter4_challenge_diarymanager_gui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chapter4_challenge_diarymanager_gui to javafx.fxml;
    exports com.example.chapter4_challenge_diarymanager_gui;
}