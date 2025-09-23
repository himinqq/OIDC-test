package com.neves.status.controller.dto.blackbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "블랙박스 등록 요청")
@Getter @Setter
@ToString
@AllArgsConstructor
public class BlackboxRegisterRequestDto {
	@Schema(description = "블랙박스 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
	@JsonProperty("uuid")
	private final String uuid;
	@Schema(description = "변경할 블랙박스 이름", example = "my blackbox")
	@JsonProperty("nickname")
	private final String nickname;
}
