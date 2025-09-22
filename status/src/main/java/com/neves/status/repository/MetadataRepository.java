package com.neves.status.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepository extends JpaRepository<Metadata, String> {

	@Query("SELECT m FROM Metadata"
			+ " m WHERE m.blackboxUuid = :uuid"
			+ " AND m.isDeleted = false"
			+ " AND m.createdAt BETWEEN :start AND :end")
	List<Metadata> findByBlackboxUuidBetween(
			@Param("uuid") String uuid,
			@Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);
}
