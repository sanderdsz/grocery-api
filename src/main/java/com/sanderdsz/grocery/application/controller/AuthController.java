package com.sanderdsz.grocery.application.controller;

import com.sanderdsz.grocery.domain.dto.SignupDTO;
import com.sanderdsz.grocery.domain.dto.TokenDTO;
import com.sanderdsz.grocery.infrastructure.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> signUp(
            @RequestBody
            SignupDTO dto
    ) {

        TokenDTO tokenDTO = authService.save(dto);

        return ResponseEntity.ok(tokenDTO);
    }
}
