package com.sanderdsz.security.infrastructure.service;

import com.sanderdsz.security.domain.model.User;
import com.sanderdsz.security.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("E-mail not found: " + username));
    }

    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        return user.get();
    }

    public User save(User user) {

        Optional<User> userExists = userRepository.findByEmail(user.getEmail());

        if (userExists.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail is already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User recover(Long id, String password) {

        Optional<User> userExists = userRepository.findById(id);

        if (userExists.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        userExists.get().setPassword(passwordEncoder.encode(password));

        userExists.get().setUpdatedAt(LocalDateTime.now());

        return userRepository.save(userExists.get());

    }

}
