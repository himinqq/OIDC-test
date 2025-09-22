package com.neves.status.controller.dto.metadata;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "블랙박스 정보 응답 요청")
@Getter @Setter
@Builder
@AllArgsConstructor
public class MetadataResponse {
    @Schema(description = "영상이 저장된 객체 스토리지의 키", example = "videos/asdf.mp4")
    private String object_key;
    @Schema(description = "영상 길이 (밀리초 단위)", example = "600000")
    private Long duration;
    @Schema(description = "영상이 촬영 시작시간", example = "2024-06-15T12:39:56")
    private LocalDateTime created_at;
    @Schema(description = "영상 파일 크기 (바이트 단위)", example = "104857600")
    private Long file_size;
    @Schema(description = "영상 파일 타입", example = "mp4")
    private String file_type;

    public static MetadataResponse example(){
        return new MetadataResponse(
                "videos/asdf.mp4",
                600000L,
                LocalDateTime.of(2024, 6, 15, 12, 39, 56),
                104857600L,
                "mp4"
        );
    }
}
