package br.com.americanas.estagiotech.libraryapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${application.mail.email-sender}")
    private String emailSender;

    public void sendMails(List<String> mailList, String message) {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailSender);
        mailMessage.setSubject("Livro com empréstimo atrasado");
        mailMessage.setText(message);
        mailMessage.setTo(mailList.toArray(new String[mailList.size()]));

        javaMailSender.send(mailMessage);
    }
}
