package com.sanderdsz.security.infrastructure.service;

import com.sanderdsz.security.domain.jwt.JwtHelper;
import com.sanderdsz.security.domain.dto.EmailRecoveryDTO;
import com.sanderdsz.security.domain.model.User;
import com.sanderdsz.security.domain.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${frontendUrl}")
    private String serverPort;

    public String sendRecovery(EmailRecoveryDTO recovery) {

        Optional<User> user = userRepository.findByEmail(recovery.getRecipient());

        if (user.isEmpty()) {
            return "E-mail not found";
        }

        try {

            String accessToken = jwtHelper.generateAccessToken(user.get());

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);

            mailMessage.setTo(recovery.getRecipient());

            mailMessage.setSubject("Password Recovery");

            String mailText = String.format(
                    "To set a new password, please enter in the link: %s/recovery?accessToken=%s&email=%s",
                    serverPort,
                    accessToken,
                    recovery.getRecipient()
            );

            mailMessage.setText(mailText);

            javaMailSender.send(mailMessage);

            log.info("E-mail sent to: " + recovery.getRecipient());

            return "E-mail sent to: " + recovery.getRecipient();

        } catch (Exception e) {

            log.error("Error while sending e-mail: " + e.getMessage());

            return "Error while sending e-mail";
        }
    }
}
