package com.sanderdsz.security.application.controller;

import com.sanderdsz.security.domain.dto.EmailRecoveryDTO;
import com.sanderdsz.security.infrastructure.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recovery")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<?> recoveryMail(
            @RequestBody
            EmailRecoveryDTO recovery
    ) {

        String status = emailService.sendRecovery(recovery);

        return ResponseEntity.ok(status);
    }
}
