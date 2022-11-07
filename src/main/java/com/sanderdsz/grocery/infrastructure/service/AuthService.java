package com.sanderdsz.grocery.infrastructure.service;

import com.sanderdsz.grocery.domain.dto.LoginDTO;
import com.sanderdsz.grocery.domain.dto.SignupDTO;
import com.sanderdsz.grocery.domain.dto.TokenDTO;
import com.sanderdsz.grocery.domain.jwt.JwtHelper;
import com.sanderdsz.grocery.domain.model.RefreshToken;
import com.sanderdsz.grocery.domain.model.User;
import com.sanderdsz.grocery.domain.repository.RefreshTokenRepository;
import com.sanderdsz.grocery.domain.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
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

        Optional<User> userExists = userRepository.findByEmail(dto.getEmail());

        if (userExists.isEmpty()) {

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

            userRepository.save(user);

            refreshTokenRepository.save(refreshToken);

            String refreshTokenString = jwtHelper.generateRefreshToken(user, refreshToken);

            String accessTokenString = jwtHelper.generateAccessToken(user);

            return new TokenDTO(user.getEmail(), refreshTokenString, accessTokenString);

        } else {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail already registered");
        }
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

            refreshTokenRepository.save(refreshToken);

            String refreshTokenString = jwtHelper.generateRefreshToken(user.get(), refreshToken);

            return new TokenDTO(user.get().getEmail(), refreshTokenString, accessToken);

        } else {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

    }

    public void logout(TokenDTO dto) {

        String refreshTokenString = dto.getRefreshToken();

        boolean isValid = jwtHelper.validateRefreshToken(dto.getRefreshToken());

        log.info("isValid: ", isValid);

        Long tokenId = jwtHelper.getTokenIdFromRefreshToken(refreshTokenString);

        log.info("tokenId: ", tokenId);

        Long refreshTokenId = jwtHelper.getTokenIdFromRefreshToken(refreshTokenString);

        if (jwtHelper.validateRefreshToken(refreshTokenString) &&
            refreshTokenRepository.existsById(refreshTokenId)
        ) {

            Long userId = Long.parseLong(jwtHelper.getUserIdFromRefreshToken(refreshTokenString));

            refreshTokenRepository.deleteByUser_Id(userId);

        } else {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token");
        }
    }

}
