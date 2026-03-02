package com.wedding.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SMSService {
    @Value("${twilio.accountSid:}")
    private String accountSid;
    @Value("${twilio.authToken:}")
    private String authToken;
    @Value("${twilio.fromNumber:}")
    private String fromNumber;

    public void sendSMS(String to, String body) {
        if (accountSid.isEmpty() || authToken.isEmpty() || fromNumber.isEmpty()) {
            // not configured, just log the message
            System.out.println("[SMS] to=" + to + " msg=" + body);
            return;
        }
        try {
            com.twilio.Twilio.init(accountSid, authToken);
            com.twilio.rest.api.v2010.account.Message.creator(
                    new com.twilio.type.PhoneNumber(to),
                    new com.twilio.type.PhoneNumber(fromNumber),
                    body)
                .create();
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
        }
    }
}
