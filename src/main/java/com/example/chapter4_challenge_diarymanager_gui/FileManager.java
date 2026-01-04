package com.example.chapter4_challenge_diarymanager_gui;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager {
    private final Path storageDir = Paths.get("diary_entries");

    public FileManager() {
        try {
            if (Files.notExists(storageDir)) {
                Files.createDirectory(storageDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveEntry(DiaryEntry entry) throws IOException {
        Path filePath = storageDir.resolve(entry.getTitle() + ".html");
        Files.writeString(filePath, entry.getContent(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public List<EntryMetadata> listEntriesMetadata() throws IOException {
        try (Stream<Path> stream = Files.list(storageDir)) {
            return stream
                    .filter(path -> path.toString().endsWith(".html"))
                    .map(path -> {
                        String title = path.getFileName().toString().replace(".html", "");
                        LocalDateTime modifiedTime = LocalDateTime.now();
                        try {
                            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                            modifiedTime = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneId.systemDefault());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return new EntryMetadata(title, modifiedTime);
                    })
                    .collect(Collectors.toList());
        }
    }
    
    // Kept for backward compatibility if needed, but listEntriesMetadata is preferred
    public List<String> listEntries() throws IOException {
        try (Stream<Path> stream = Files.list(storageDir)) {
            return stream
                    .map(path -> path.getFileName().toString().replace(".html", ""))
                    .collect(Collectors.toList());
        }
    }

    public String readEntry(String title) throws IOException {
        return Files.readString(storageDir.resolve(title + ".html"));
    }

    public void deleteEntry(String title) throws IOException {
        Files.deleteIfExists(storageDir.resolve(title + ".html"));
    }

    public void clearAllEntries() throws IOException {
        try (Stream<Path> stream = Files.list(storageDir)) {
            stream.forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}