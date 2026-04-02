package com.wedding.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.wedding.model.GalleryItem;
import com.wedding.repository.GalleryRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/gallery")
public class GalleryController {

    @Autowired
    private GalleryRepository repo;

    @Autowired
    private Cloudinary cloudinary;

    // ================= GET ALL MEDIA =================

    @GetMapping
    public List<GalleryItem> all() {
        return repo.findAll();
    }

    // ================= DELETE MEDIA =================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("status", "deleted"));
    }

    // ================= MULTIPLE FILE UPLOAD =================

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMedia(
            @RequestParam("file") MultipartFile[] files,
            @RequestParam(value = "type", required = false) String type) {

        try {

            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No files selected"));
            }

            for (MultipartFile file : files) {

                if (file.isEmpty())
                    continue;

                String contentType = file.getContentType();

                String mediaType;

                if (type != null) {
                    mediaType = type;
                } else if (contentType != null && contentType.startsWith("video")) {
                    mediaType = "video";
                } else {
                    mediaType = "image";
                }

                String original = file.getOriginalFilename();

                if (original == null)
                    original = "file";

                Map uploadResult;

                if (contentType != null && contentType.startsWith("video")) {
                    uploadResult = cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap("resource_type", "video")
                    );
                } else {
                    uploadResult = cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.emptyMap()
                    );
                }

                String url = uploadResult.get("secure_url").toString();

                GalleryItem item = new GalleryItem();

                item.setTitle(original);
                item.setMediaUrl(url);
                item.setType(mediaType);
                item.setUploadedAt(LocalDateTime.now());

                repo.save(item);
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "uploadedFiles", files.length));

        } catch (IOException e) {

            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Upload failed",
                            "message", e.getMessage()));
        }
    }
}