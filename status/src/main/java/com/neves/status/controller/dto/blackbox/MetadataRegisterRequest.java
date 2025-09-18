package com.neves.status.controller.dto.blackbox;

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
	private final String blackbox_uuid;
	@Schema(description = "영상이 스트림이 시작된 시간", example = "2024-06-15T12:34:56")
	private final LocalDateTime stream_started_at;
	@Schema(description = "영상이 촬영 시작시간", example = "2024-06-15T12:39:56")
	private final LocalDateTime created_at;
	@Schema(description = "영상 파일 크기 (바이트 단위)", example = "104857600")
	private final Long file_size;
	@Schema(description = "영상 길이 (밀리초 단위)", example = "600000")
	private final Long duration;
	@Schema(description = "영상이 저장된 객체 스토리지의 키", example = "videos/asdf.mp4")
	private final String object_key;
	@Schema(description = "영상 파일 타입", example = "mp4")
	private final String file_type;
}
