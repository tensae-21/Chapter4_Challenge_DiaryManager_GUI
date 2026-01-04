package com.example.chapter4_challenge_diarymanager_gui;

import java.time.LocalDateTime;

public class DiaryEntry {
    private String title;
    private String content;
    private LocalDateTime timestamp;

    public DiaryEntry(String title, String content) {
        this.title = title;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
}