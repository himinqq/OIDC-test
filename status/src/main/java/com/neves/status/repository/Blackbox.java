package com.neves.status.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Add this

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@Entity
public class Blackbox {
	@Id
	private String uuid;
	private String nickname;
	private String userId;
	private LocalDateTime createdAt;
}