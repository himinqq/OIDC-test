package com.neves.status.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Blackbox {
	@Id
	private String uuid;
	private String nickname;
	private String userId;
	private LocalDateTime createdAt;
}
