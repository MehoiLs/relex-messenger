package com.mehoil.relex.general.email;

import com.sun.mail.util.MailConnectException;
import jakarta.mail.MessagingException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.util.Arrays;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender mailSender) {
        this.javaMailSender = mailSender;
    }

    @Async
    @Retryable(
            retryFor = { MailException.class },
            backoff = @Backoff(delay = 10000)
    )
    public void sendEmailAsync(@NonNull SimpleMailMessage mailMessage) {
        if(mailMessage.getTo() == null) return;
        javaMailSender.send(mailMessage);
        log.info("[MAIL SERVICE] The letter: \"" + mailMessage.getSubject() + "\" has been successfully sent to " +
                Arrays.toString(mailMessage.getTo()) + ".");
    }
}
