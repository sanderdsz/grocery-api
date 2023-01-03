package com.sanderdsz.security.domain.repository;

import com.sanderdsz.security.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Long deleteByUser_Id(Long id);

    Optional<RefreshToken> findByUser_Id(Long id);

    List<RefreshToken> findAllByUser_Id(Long id);

    Optional<RefreshToken> findTopByUser_IdOrderByCreatedAtDesc(Long id);

}
