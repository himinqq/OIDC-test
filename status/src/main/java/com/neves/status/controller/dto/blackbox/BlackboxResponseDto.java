package com.neves.status.controller.dto.blackbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neves.status.repository.Blackbox;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "블랙박스 응답 DTO")
@Getter @Setter
@ToString
@AllArgsConstructor
public class BlackboxResponseDto {
    @Schema(description = "블랙박스 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    @JsonProperty("uuid")
    private final String uuid;
    @Schema(description = "블랙박스 닉네임", example = "my car blackbox")
    @JsonProperty("nickname")
    private final String nickname;
    @Schema(description = "등록된 시간", example = "2024-06-15T12:34:56Z")
    @JsonProperty("created_at")
    private final LocalDateTime createdAt;
    @Schema(description = "블랙박스 헬스 체크", example = "HEALTHY")
    @JsonProperty("health_status")
    private BlackboxStatus healthStatus;
    @Schema(description = "마지막 서버와 연결 시간", example = "2024-06-15T12:34:56Z")
    @JsonProperty("last_connected_at")
    private LocalDateTime lastConnectedAt;

    public BlackboxResponseDto(Blackbox blackbox) {
        this.uuid = blackbox.getUuid();
        this.nickname = blackbox.getNickname();
        this.createdAt = blackbox.getCreatedAt();

    }
}
