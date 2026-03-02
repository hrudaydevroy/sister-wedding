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
@CrossOrigin(origins = "*")  // allow frontend code served from file or other origin
@RequestMapping("/api/gallery")
public class GalleryController {

    @Autowired
    private GalleryRepository repo;

    // regular JSON CRUD
    @GetMapping
    public List<GalleryItem> all() {
        return repo.findAll();
    }

    @PostMapping
    public GalleryItem create(@RequestBody GalleryItem item) {
        item.setUploadedAt(LocalDateTime.now());
        return repo.save(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }

    // multipart upload endpoint (images or videos)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMedia(@RequestParam("file") MultipartFile file,
                                   @RequestParam("title") String title) throws IOException {
        // determine type from content type
        String type = file.getContentType().startsWith("video") ? "video" : "image";

        // if item with same title already exists, treat as duplicate
        if (repo.existsByTitle(title)) {
            return ResponseEntity.status(409).body(Map.of("error", "Title already exists"));
        }

        // save to local uploads folder (backend/uploads)
        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path dest = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

        // build URL that can be served
        String url = "/uploads/" + filename;

        // also check if URL already known
        if (repo.existsByMediaUrl(url)) {
            return ResponseEntity.status(409).body(Map.of("error", "File already uploaded"));
        }

        GalleryItem item = new GalleryItem();
        item.setTitle(title);
        item.setMediaUrl(url);
        item.setType(type);
        item.setUploadedAt(LocalDateTime.now());
        return ResponseEntity.ok(repo.save(item));
    }
}
