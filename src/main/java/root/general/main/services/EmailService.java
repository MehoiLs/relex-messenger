package root.general.main.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    private final int maxSendRetries = 3;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.javaMailSender = mailSender;
    }

    @Async
    public void sendEmailAsync(@NonNull SimpleMailMessage mailMessage) {
        if(mailMessage.getTo() == null) return;

        int currentRetry = 1;
        while (currentRetry <= maxSendRetries) {
            try {
                javaMailSender.send(mailMessage);
                log.info("[MAIL SERVICE] The letter: \"" + mailMessage.getSubject() + "\" has been successfully sent to " +
                        Arrays.toString(mailMessage.getTo()) + ".");
                break;
            } catch (MailException e) {
                log.warn("[MAIL SERVICE] Attempt " + currentRetry + " to resend the letter: \"" + mailMessage.getSubject() + "\"...");
                currentRetry++;
                try { Thread.sleep(10000); } catch (InterruptedException ignored) {}
            }
        }
    }
}
