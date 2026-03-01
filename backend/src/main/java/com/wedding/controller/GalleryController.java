package com.wedding.controller;

import com.wedding.model.GalleryItem;
import com.wedding.repository.GalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
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

    @DeleteMapping("/{id}
")
    public ResponseEntity<?> delete(@PathVariable String id) {
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }

    // multipart upload endpoint (images or videos)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GalleryItem uploadMedia(@RequestParam("file") MultipartFile file,
                                   @RequestParam("title") String title) throws IOException {
        // determine type from content type
        String type = file.getContentType().startsWith("video") ? "video" : "image";

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

        GalleryItem item = new GalleryItem();
        item.setTitle(title);
        item.setMediaUrl(url);
        item.setType(type);
        item.setUploadedAt(LocalDateTime.now());
        return repo.save(item);
    }
}
