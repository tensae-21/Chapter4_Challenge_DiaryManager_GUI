package com.example.chapter4_challenge_diarymanager_gui;

import java.time.LocalDateTime;

public class EntryMetadata {
    private final String title;
    private final LocalDateTime lastModified;

    public EntryMetadata(String title, LocalDateTime lastModified) {
        this.title = title;
        this.lastModified = lastModified;
    }

    public String getTitle() { return title; }
    public LocalDateTime getLastModified() { return lastModified; }

    @Override
    public String toString() { return title; }
}