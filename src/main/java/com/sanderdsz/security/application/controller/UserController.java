package com.sanderdsz.security.application.controller;

import com.sanderdsz.security.domain.model.User;
import com.sanderdsz.security.infrastructure.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> me(
            @AuthenticationPrincipal
            User user
    ) {
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(
            @PathVariable
            Long id,

            @RequestBody
            String password
    ) {
        userService.recover(id, password);

        return ResponseEntity.ok("Password recovery set");
    }

}
