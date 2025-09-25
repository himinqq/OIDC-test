package com.neves.status.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter @Builder
@Entity
public class Metadata {
	@Id
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "blackbox_uuid", referencedColumnName = "uuid", nullable = true)
	private Blackbox blackbox;
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
	@Builder.Default
	private boolean isDeleted = false;
}
