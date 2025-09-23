package com.neves.status.service;

import com.neves.status.controller.dto.blackbox.MetadataRegisterRequest;
import com.neves.status.controller.dto.metadata.MetadataResponse;
import com.neves.status.repository.Blackbox;
import com.neves.status.repository.BlackboxRepository;
import com.neves.status.repository.Metadata;
import com.neves.status.repository.MetadataRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class MetadataService {
	private final MetadataRepository repository;
	private final BlackboxRepository blackboxRepository;

	public void create(UUID metadataId, MetadataRegisterRequest request) {
		Blackbox blackbox = blackboxRepository.findByUuid(request.getBlackboxUuid())
				.orElseThrow(() -> new IllegalArgumentException("UUID에 해당하는 블랙박스를 찾을 수 없습니다. UUID: : " + request.getBlackboxUuid()));

		Metadata metadata = Metadata.builder()
				.id(metadataId.toString())
				.blackbox(blackbox)
				.streamStartedAt(request.getStreamStartedAt())
				.createdAt(request.getCreatedAt())
				.fileSize(request.getFileSize())
				.duration(request.getDuration())
				.objectKey(request.getObjectKey())
				.fileType(request.getFileType())
				.build();
		repository.save(metadata);
	}

	public List<MetadataResponse> list(String blackboxId, LocalDateTime startOfDay) {
		LocalDateTime endOfDay = startOfDay.plusDays(1);
		List<Metadata> metadataList = repository.findByBlackboxUuidBetween(
				blackboxId,
				startOfDay,
				endOfDay
		);
		return metadataList.stream()
				.map(data -> MetadataResponse.builder()
						.objectKey(data.getObjectKey())
						.duration(data.getDuration())
						.createdAt(data.getCreatedAt())
						.fileSize(data.getFileSize())
						.fileType(data.getFileType()).build()
				).toList();
	}

	public void delete(String metadataId) {
		Optional<Metadata> metadata = repository.findById(metadataId);
		metadata.ifPresent(data -> data.setDeleted(true));
	}
}
