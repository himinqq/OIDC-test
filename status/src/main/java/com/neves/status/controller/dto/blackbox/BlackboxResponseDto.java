package com.neves.status.controller.dto.blackbox;

import com.neves.status.repository.Blackbox;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "블랙박스 응답 DTO")
@Getter
@Setter
@AllArgsConstructor
public class BlackboxResponseDto {
    @Schema(description = "블랙박스 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    private final String uuid;
    @Schema(description = "블랙박스 닉네임", example = "my car blackbox")
    private final String nickname;
    @Schema(description = "등록된 시간", example = "2024-06-15T12:34:56Z")
    private final LocalDateTime created_at;
    @Schema(description = "블랙박스 헬스 체크", example = "HEALTHY")
    private final String health_status = "";
    @Schema(description = "마지막 서버와 연결 시간", example = "2024-06-15T12:34:56Z")
    private final String last_connected_at = "";

    public BlackboxResponseDto(Blackbox blackbox) {
        this.uuid = blackbox.getUuid();
        this.nickname = blackbox.getNickname();
        this.created_at = blackbox.getCreatedAt();

    }
}
