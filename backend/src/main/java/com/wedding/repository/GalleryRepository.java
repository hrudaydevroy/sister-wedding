package com.wedding.repository;

import com.wedding.model.GalleryItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GalleryRepository extends MongoRepository<GalleryItem, String> {
    boolean existsByTitle(String title);
    boolean existsByMediaUrl(String mediaUrl);
}
