package com.sanderdsz.security.infrastructure.service;

import com.sanderdsz.security.domain.dto.LoginDTO;
import com.sanderdsz.security.domain.dto.PasswordRecoveryDTO;
import com.sanderdsz.security.domain.dto.SignupDTO;
import com.sanderdsz.security.domain.dto.TokenDTO;
import com.sanderdsz.security.domain.jwt.JwtHelper;
import com.sanderdsz.security.domain.model.RefreshToken;
import com.sanderdsz.security.domain.model.User;
import com.sanderdsz.security.domain.repository.RefreshTokenRepository;
import com.sanderdsz.security.domain.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
     * Saves the user and creates a refresh token if the user isn't already registered
     * @param dto
     * @return TokenDTO (user e-mail, refreshToken and accessToken)
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
                    .id(UUID.randomUUID())
                    .user(user)
                    .expiresDate(LocalDateTime.now().plusSeconds(refreshTokenExpirationSeconds))
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(user);

            String refreshTokenString = jwtHelper.generateRefreshToken(user, refreshToken);

            refreshToken.setRefreshToken(refreshTokenString);

            refreshTokenRepository.save(refreshToken);

            String accessTokenString = jwtHelper.generateAccessToken(user);

            log.info("User {} created", user.getEmail());

            return new TokenDTO(user.getEmail(), refreshTokenString, accessTokenString);

        } else {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail already registered");
        }
    }

    /**
     * Checks if the user is already logged (valid refresh token),
     * then creates a new access token and refresh token if need it.
     * @param dto
     * @return TokenDTO (user e-mail, refreshToken and accessToken)
     */
    public TokenDTO login(LoginDTO dto) {

        Optional<User> user;

        Optional<RefreshToken> refreshTokenOptional;

        try {

            user = userRepository.findByEmail(dto.getEmail());

            refreshTokenOptional = refreshTokenRepository.findTopByUser_IdOrderByCreatedAtDesc(user.get().getId());

        } catch (Exception e) {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User or password incorrect");
        }

        if (user.isPresent() && passwordEncoder.matches(dto.getPassword(), user.get().getPassword())) {

            log.info("User {} logged in", user.get().getEmail());

            if (refreshTokenOptional.get().getExpiresDate().isAfter(LocalDateTime.now())) {

                String accessToken = jwtHelper.generateAccessToken(user.get());

                return new TokenDTO(user.get().getEmail(), refreshTokenOptional.get().getRefreshToken(), accessToken);

            } else {

                String accessToken = jwtHelper.generateAccessToken(user.get());

                RefreshToken refreshToken = RefreshToken.builder()
                        .id(UUID.randomUUID())
                        .user(user.get())
                        .expiresDate(LocalDateTime.now().plusSeconds(refreshTokenExpirationSeconds))
                        .createdAt(LocalDateTime.now())
                        .build();

                String refreshTokenString = jwtHelper.generateRefreshToken(user.get(), refreshToken);

                refreshToken.setRefreshToken(refreshTokenString);

                refreshTokenRepository.save(refreshToken);

                return new TokenDTO(user.get().getEmail(), refreshTokenString, accessToken);
            }

        } else {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User or password incorrect");
        }

    }

    /**
     * Removes all refresh tokens entries from database from the particular user
     * @param dto
     */
    public void logout(TokenDTO dto) {

        String refreshTokenString = dto.getRefreshToken();

        Long refreshTokenId = jwtHelper.getTokenIdFromRefreshToken(refreshTokenString);

        if (jwtHelper.validateRefreshToken(refreshTokenString) &&
            refreshTokenRepository.existsById(refreshTokenId)
        ) {

            Long userId = Long.parseLong(jwtHelper.getUserIdFromRefreshToken(refreshTokenString));

            refreshTokenRepository.deleteByUser_Id(userId);

            log.info("User ID {} logged out", userId);

        } else {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token");
        }
    }

    public void reset(PasswordRecoveryDTO dto) {

        String accessTokenString = dto.getAccessToken();

        if (jwtHelper.validateAccessToken(accessTokenString)) {

            Optional <User> user = userRepository.findByEmail(dto.getEmail());

            user.get().setPassword(passwordEncoder.encode(dto.getPassword()));

            userRepository.save(user.get());

            log.info("user {} password reset with success", user.get().getEmail());

        } else {

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid access token");
        }
    }

}
