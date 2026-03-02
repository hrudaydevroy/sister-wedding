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

    @Autowired
    private com.wedding.service.SMSService smsService;

    @GetMapping
    public List<RSVP> all() {
        return repo.findAll();
    }

    @PostMapping
    public RSVP create(@RequestBody RSVP rsvp) {
        rsvp.setCreatedAt(LocalDateTime.now());
        RSVP saved = repo.save(rsvp);
        // inform host via SMS (number hardcoded as per requirements)
        String adminNumber = "+917799461935"; // international format
        String msg = "New RSVP from " + saved.getName() + " (" + saved.getEmail() + ") guests=" + saved.getGuests() + " msg='" + saved.getMessage() + "'";
        smsService.sendSMS(adminNumber, msg);
        return saved;
    }
}
