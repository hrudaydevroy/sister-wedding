package com.wedding.repository;

import com.wedding.model.RSVP;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RSVPRepository extends MongoRepository<RSVP, String> {
}
