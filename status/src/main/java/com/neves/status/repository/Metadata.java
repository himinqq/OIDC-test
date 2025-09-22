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
public class Metadata {
	@Id
	private String id;
	private String blackboxUuid;
	/**
	 * 영상이 연속적으로 들어오기 시작한 시간,
	 * 만약 네트워크나 내부 이슈로 연결이 한 번 끊기면,
	 * 다음 연결되었을 때가 streamStartedAt이 됨.
	 */
	private LocalDateTime streamStartedAt;
	private LocalDateTime createdAt;
	private Long fileSize;
	private Long duration;
	private String objectKey;
	private String fileType;
}
