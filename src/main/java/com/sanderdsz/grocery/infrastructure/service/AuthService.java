package com.sanderdsz.grocery.infrastructure.service;

import com.sanderdsz.grocery.domain.dto.SignupDTO;
import com.sanderdsz.grocery.domain.dto.TokenDTO;
import com.sanderdsz.grocery.domain.jwt.JwtHelper;
import com.sanderdsz.grocery.domain.model.RefreshToken;
import com.sanderdsz.grocery.domain.model.User;
import com.sanderdsz.grocery.domain.repository.RefreshTokenRepository;
import com.sanderdsz.grocery.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

}
