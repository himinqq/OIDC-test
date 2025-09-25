package com.neves.status.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackboxRepository extends JpaRepository<Blackbox, String> {
    Optional<Blackbox> findByUuid(String uuid);
    List<Blackbox> findByUserId(String userId);
    @Query("SELECT b FROM Blackbox b WHERE " +
            "(SELECT MAX(m.createdAt) FROM Metadata m WHERE m.blackbox = b) < :thresholdTime")
    List<Blackbox> findBlackboxesWithNoRecentMetadata(@Param("thresholdTime") LocalDateTime thresholdTime);
}