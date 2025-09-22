package com.neves.status.controller.dto.blackbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "영상 메타데이터 등록 요청")
@Getter @Setter
@AllArgsConstructor
public class MetadataRegisterRequest {
	@Schema(description = "블랙박스 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
	@JsonProperty("blackbox_uuid")
	private final String blackboxUuid;
	@Schema(description = "영상이 스트림이 시작된 시간", example = "2024-06-15T12:34:56")
	@JsonProperty("stream_started_at")
	private final LocalDateTime streamStartedAt;
	@Schema(description = "영상이 촬영 시작시간", example = "2024-06-15T12:39:56")
	@JsonProperty("created_at")
	private final LocalDateTime createdAt;
	@Schema(description = "영상 파일 크기 (바이트 단위)", example = "104857600")
	@JsonProperty("file_size")
	private final Long fileSize;
	@Schema(description = "영상 길이 (밀리초 단위)", example = "600000")
	@JsonProperty("duration")
	private final Long duration;
	@Schema(description = "영상이 저장된 객체 스토리지의 키", example = "videos/asdf.mp4")
	@JsonProperty("object_key")
	private final String objectKey;
	@Schema(description = "영상 파일 타입", example = "mp4")
	@JsonProperty("file_type")
	private final String fileType;
}
