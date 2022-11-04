package com.sanderdsz.grocery.domain.repository;

import com.sanderdsz.grocery.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> { }