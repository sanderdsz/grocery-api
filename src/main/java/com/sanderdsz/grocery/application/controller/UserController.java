package com.sanderdsz.grocery.application.controller;

import com.sanderdsz.grocery.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @AuthenticationPrincipal
            User user
    ) {
        return ResponseEntity.ok(user);
    }

}
