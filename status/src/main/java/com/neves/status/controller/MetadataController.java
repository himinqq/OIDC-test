package com.neves.status.controller;

import com.neves.status.controller.dto.blackbox.MetadataRegisterRequest;
import com.neves.status.controller.dto.metadata.MetadataResponse;
import com.neves.status.service.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "Metadata", description = "영상 메타데이터를 위한 API")
@Log4j2
@RestController
@RequestMapping("/metadata")
public class MetadataController {
	private final MetadataService metadataService;

	@Operation(summary = "영상 메타데이터 등록", description = "새로운 메타데이터를 저장합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "영상 메타데이터 저장 성공")
	})
	@PostMapping
	public ResponseEntity<Object> create(@RequestBody MetadataRegisterRequest request) {
		log.info("(Creating metadata) request: {}", request);
		metadataService.create(java.util.UUID.randomUUID(), request);
		return ResponseEntity.status(201).build();
	}

	@Operation(summary="블랙박스의 영상 메타데이터 조회", description="특정 블랙박스의 메타데이터를 특정 날짜에 맞게 조회합니다.")
	@ApiResponses({
			@ApiResponse(responseCode="200", description="영상 메타데이터 조회 성공"),
			@ApiResponse(responseCode="404", description="블랙박스를 찾을 수 없음")
	})
	@GetMapping
	public ResponseEntity<List<MetadataResponse>> list(
			@RequestParam
			@Schema(description = "블랙박스 UUID", example = "123e4567-e89b-12d3-a456-426614174000")
			String blackboxId,
			@RequestParam
			@Schema(description = "조회할 날짜", example = "2024-06-15T00:00:00")
			LocalDateTime date
	) {
		log.info("(Listing metadata) blackboxId: {}, date: {}", blackboxId, date);
		try {
			return ResponseEntity.ok(metadataService.list(blackboxId, date));
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@Operation(summary = "영상 삭제", description = "특정 영상을 삭제합니다.")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "영상 삭제 성공"),
			@ApiResponse(responseCode = "404", description = "해당 video_id를 찾을 수 없음")
	})
	@DeleteMapping("/{videoId}")
	public ResponseEntity<Void> delete(
			@PathVariable
			@Parameter(description = "삭제할 영상의 ID", example = "666a7b29a2862a5b67484344")
			String videoId
	) {
		log.info("(Deleting metadata) video_id: {}", videoId);
		try {
			metadataService.delete(videoId);
			return ResponseEntity.noContent().build();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
