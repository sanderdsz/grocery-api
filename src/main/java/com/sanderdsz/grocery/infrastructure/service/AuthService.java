package com.sanderdsz.grocery.infrastructure.service;

import com.sanderdsz.grocery.domain.dto.LoginDTO;
import com.sanderdsz.grocery.domain.dto.SignupDTO;
import com.sanderdsz.grocery.domain.dto.TokenDTO;
import com.sanderdsz.grocery.domain.jwt.JwtHelper;
import com.sanderdsz.grocery.domain.model.RefreshToken;
import com.sanderdsz.grocery.domain.model.User;
import com.sanderdsz.grocery.domain.repository.RefreshTokenRepository;
import com.sanderdsz.grocery.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Value("#{${refreshTokenExpirationMinutes} * 60}")
    private int refreshTokenExpirationSeconds;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtHelper jwtHelper;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Saves the user and creates a refresh token
     * @param dto
     * @return TokenDTO (user e-mail, refreshToken)
     */
    public TokenDTO save(SignupDTO dto) {

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role("ADMIN")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .expiresDate(LocalDateTime.now().plusSeconds(refreshTokenExpirationSeconds))
                .createdAt(LocalDateTime.now())
                .build();

        String refreshTokenString = jwtHelper.generateRefreshToken(user, refreshToken);

        refreshToken.setRefreshToken(refreshTokenString);

        userRepository.save(user);

        refreshTokenRepository.save(refreshToken);

        String accessTokenString = jwtHelper.generateAccessToken(user);

        return new TokenDTO(user.getEmail(), refreshTokenString, accessTokenString);
    }

    public TokenDTO login(LoginDTO dto) {

        Optional<User> user = userRepository.findByEmail(dto.getEmail());

        if (user.isPresent()) {

            String accessToken = jwtHelper.generateAccessToken(user.get());

            RefreshToken refreshToken = RefreshToken.builder()
                            .user(user.get())
                            .expiresDate(LocalDateTime.now().plusSeconds(refreshTokenExpirationSeconds))
                            .createdAt(LocalDateTime.now())
                            .build();

            String refreshTokenString = jwtHelper.generateRefreshToken(user.get(), refreshToken);

            refreshToken.setRefreshToken(refreshTokenString);

            refreshTokenRepository.save(refreshToken);

            return new TokenDTO(user.get().getEmail(), refreshTokenString, accessToken);

        } else {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

    }

}
