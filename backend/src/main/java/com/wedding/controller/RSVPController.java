package com.wedding.controller;

import com.wedding.model.RSVP;
import com.wedding.repository.RSVPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rsvp")
public class RSVPController {

    @Autowired
    private RSVPRepository repo;

    @GetMapping
    public List<RSVP> all() {
        return repo.findAll();
    }

    @PostMapping
    public RSVP create(@RequestBody RSVP rsvp) {
        rsvp.setCreatedAt(LocalDateTime.now());
        return repo.save(rsvp);
    }
}
