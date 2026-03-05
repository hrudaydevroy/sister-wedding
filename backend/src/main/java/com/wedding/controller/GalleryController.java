package com.wedding.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wedding.model.GalleryItem;
import com.wedding.repository.GalleryRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/gallery")
public class GalleryController {

    @Autowired
    private GalleryRepository repo;

    // ================= GET ALL MEDIA =================
    @GetMapping
    public List<GalleryItem> all() {
        return repo.findAll();
    }

    // ================= CREATE JSON ITEM =================
    @PostMapping
    public GalleryItem create(@RequestBody GalleryItem item) {
        item.setUploadedAt(LocalDateTime.now());
        return repo.save(item);
    }

    // ================= DELETE MEDIA =================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }

    // ================= UPLOAD IMAGE / VIDEO =================
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title) {

        try {

            // ---------- validation ----------
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No file selected"));
            }

            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Title is required"));
            }

            // ---------- determine type ----------
            String contentType = file.getContentType();
            String type = (contentType != null && contentType.startsWith("video"))
                    ? "video"
                    : "image";

            // ---------- check duplicate title ----------
            if (repo.existsByTitle(title)) {
                return ResponseEntity.status(409)
                        .body(Map.of("error", "Title already exists"));
            }

            // ---------- create uploads folder ----------
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // ---------- safe filename ----------
            String original = file.getOriginalFilename();
            if (original == null) {
                original = "file";
            }

            String filename = System.currentTimeMillis() + "_" + original;
            Path destination = uploadDir.resolve(filename);

            // ---------- save file ----------
            Files.copy(file.getInputStream(), destination,
                    StandardCopyOption.REPLACE_EXISTING);

            // ---------- build URL ----------
            String url = "/uploads/" + filename;

            // ---------- save to database ----------
            GalleryItem item = new GalleryItem();
            item.setTitle(title);
            item.setMediaUrl(url);
            item.setType(type);
            item.setUploadedAt(LocalDateTime.now());

            return ResponseEntity.ok(repo.save(item));

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Upload failed", "message", e.getMessage()));
        }
    }
}