package com.journal.journal.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {

    @Autowired
    private EmailService emailService;

    @Test
    public void testSendEmail() {
        emailService.sendEmail(
                "ashishkr27062003@gmail.com",
                "Testing SpringBoot Email",
                "This is email is sent to test Java SpringBoot email functionality"
        );
    }
}
