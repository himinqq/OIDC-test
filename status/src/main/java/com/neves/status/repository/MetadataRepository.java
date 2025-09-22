package com.neves.status.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, String> {
	List<Metadata> findMetadataByBlackboxUuidAndCreatedAtBetween(String blackboxUuid, LocalDateTime start, LocalDateTime end);
}
