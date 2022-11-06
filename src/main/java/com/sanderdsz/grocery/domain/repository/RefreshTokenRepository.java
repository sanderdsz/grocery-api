package com.sanderdsz.grocery.domain.repository;

import com.sanderdsz.grocery.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> { }
