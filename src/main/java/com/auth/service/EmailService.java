package com.auth.service;

public interface EmailService {

    void sendEmail(String email, String resetLink);
}
