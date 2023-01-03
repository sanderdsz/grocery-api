package com.sanderdsz.security.application.controller;

import com.sanderdsz.security.domain.dto.LoginDTO;
import com.sanderdsz.security.domain.dto.PasswordRecoveryDTO;
import com.sanderdsz.security.domain.dto.SignupDTO;
import com.sanderdsz.security.domain.dto.TokenDTO;
import com.sanderdsz.security.infrastructure.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
   AuthService authService;

    @GetMapping("/alive")
    public ResponseEntity<?> alive() {
        return ResponseEntity.ok("Alive!");
    };

    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<?> signUp(
            @RequestBody
            SignupDTO dto
    ) {

        TokenDTO tokenDTO = authService.save(dto);

        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(
            @RequestBody
            LoginDTO dto
    ) {

        TokenDTO tokenDTO = authService.login(dto);

        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<?> logout(
            @RequestBody
            TokenDTO dto
    ) {

        authService.logout(dto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    @Transactional
    public ResponseEntity<?> reset(
            @RequestBody
            PasswordRecoveryDTO dto
    ) {
        authService.reset(dto);

        return ResponseEntity.ok().build();
    }

}
