package com.neves.status.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackboxRepository extends JpaRepository<Blackbox, String> {
    Optional<Blackbox> findByUuid(String uuid);
    List<Blackbox> findByUserId(String userId);
}