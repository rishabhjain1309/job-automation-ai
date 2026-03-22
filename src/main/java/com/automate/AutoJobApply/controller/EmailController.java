package com.automate.AutoJobApply.controller;

import com.automate.AutoJobApply.model.EmailRequest;
import com.automate.AutoJobApply.service.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequest request) {

        emailService.sendEmail(
                request.getTo(),
                request.getSubject(),
                request.getBody()
        );

        return "Email sent!";
    }
}
