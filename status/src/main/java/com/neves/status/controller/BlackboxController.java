package com.neves.status.controller;

import com.neves.status.controller.dto.blackbox.BlackboxRegisterRequestDto;
import com.neves.status.controller.dto.blackbox.BlackboxRenameRequestDto;
import com.neves.status.controller.dto.blackbox.BlackboxResponseDto;
import com.neves.status.service.BlackboxService;
import com.neves.status.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Blackbox", description = "블랙박스 관리를 위한 API")
@RestController
@RequestMapping("/blackboxes")
@RequiredArgsConstructor
public class BlackboxController {
	private final BlackboxService blackboxService;

	@Operation(summary="블랙박스 등록", description="특정 유저의 새로운 블랙박스를 등록합니다.")
	@ApiResponses({
			@ApiResponse(responseCode="200", description="블랙박스 등록 성공"),
			@ApiResponse(responseCode="409", description="이미 등록된 블랙박스")
	})
	@PostMapping
	public ResponseEntity<BlackboxResponseDto> register(
			@RequestBody BlackboxRegisterRequestDto request,
			@RequestHeader(JwtUtils.JWT_HEADER) String jwtToken
	) {
		String userId = JwtUtils.extractUserIdFromJwt(jwtToken);
		try {
			BlackboxResponseDto response = blackboxService.register(userId, request);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(409).build();
		}
	}

	@Operation(summary="블랙박스 이름 변경", description="등록된 블랙박스의 이름을 변경합니다.")
	@ApiResponses({
			@ApiResponse(responseCode="200", description="블랙박스 이름 변경 성공"),
			@ApiResponse(responseCode="404", description="블랙박스를 찾을 수 없음")
	})
	@PutMapping("/{blackbox_id}")
	public ResponseEntity<Object> rename(
			@PathVariable("blackbox_id") String blackbox_id,
			@RequestBody BlackboxRenameRequestDto request) {
		try {
			BlackboxResponseDto response = blackboxService.rename(blackbox_id, request);
			return ResponseEntity.ok(response);
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(404).build();
		}
	}

	@Operation(summary="유저의 블랙박스 목록 조회", description="특정 유저가 등록한 모든 블랙박스의 목록을 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode="200", description="블랙박스 목록 조회 성공"),
			@ApiResponse(responseCode="404", description="유저를 찾을 수 없음")
	})
	@GetMapping
	public ResponseEntity<List<BlackboxResponseDto>> list(
			@RequestHeader(JwtUtils.JWT_HEADER) String jwtToken
	) {
		List<BlackboxResponseDto> blackboxes = blackboxService.list(JwtUtils.extractUserIdFromJwt(jwtToken));
		return ResponseEntity.ok(blackboxes);
	}
	@Operation(summary = "블랙박스 헬스 체크", description = "블랙박스의 연결 상태를 확인합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "블랙박스 상태 조회 성공"),
			@ApiResponse(responseCode = "404", description = "블랙박스를 찾을 수 없음")
	})
	@GetMapping("/{blackbox_id}/status")
	public ResponseEntity<BlackboxResponseDto> getBlackboxStatus(
			@PathVariable("blackbox_id") String blackboxId) {
		try {
			BlackboxResponseDto response = blackboxService.getBlackboxStatus(blackboxId);
			return ResponseEntity.ok(response);
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(404).build();
		}
	}
}
