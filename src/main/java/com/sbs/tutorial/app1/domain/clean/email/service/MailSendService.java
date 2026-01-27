package com.sbs.tutorial.app1.domain.clean.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSendService {
    private final JavaMailSender mailSender;

    @Async("mailExecutor")
    public void send(SimpleMailMessage message) {
        System.out.println("MailSendService thread = " + Thread.currentThread().getName());

        long t0 = System.currentTimeMillis();
        mailSender.send(message);
        long t1 = System.currentTimeMillis();

        System.out.println("mail(real)=" + (t1 - t0) + "ms");
    }
}