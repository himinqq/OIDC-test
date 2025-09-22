package com.neves.status.controller.dto.blackbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "블랙박스 이름 변경 요청 DTO")
@Getter @Setter
@AllArgsConstructor
public class BlackboxRenameRequestDto {
	@Schema(description = "블랙박스 닉네임", example = "my car blackbox")
	@JsonProperty("nickname")
	private final String nickname;
}
