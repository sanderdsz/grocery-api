package com.sanderdsz.grocery.application.controller;

import com.sanderdsz.grocery.domain.model.User;
import com.sanderdsz.grocery.infrastructure.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> list() {
        return userService.listAll();
    }

    @PostMapping("/register")
    public User save(
            @RequestBody
            User user
    ) {
        return userService.save(user);
    }

}
