package com.neves.status.service;

import com.neves.status.controller.dto.blackbox.MetadataRegisterRequest;
import com.neves.status.controller.dto.metadata.MetadataResponse;
import com.neves.status.handler.AuthorizationException;
import com.neves.status.handler.ErrorMessage;
import com.neves.status.repository.Blackbox;
import com.neves.status.repository.BlackboxRepository;
import com.neves.status.repository.Metadata;
import com.neves.status.repository.MetadataRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
				.orElse(null);

		Metadata metadata = Metadata.builder()
				.id(metadataId.toString())
				.blackbox(blackbox)
				.streamStartedAt(request.getStreamStartedAt())
				.createdAt(request.getCreatedAt())
				.fileSize(request.getFileSize())
				.duration(request.getDuration())
				.objectKey(request.getObjectKey())
				.fileType(request.getFileType())
				.isDeleted(Objects.isNull(blackbox))
				.build();
		repository.save(metadata);
	}

	public List<MetadataResponse> list(String userId,String blackboxId, LocalDateTime targetDate) {
		Blackbox blackbox = blackboxRepository.findByUuid(blackboxId)
				.orElseThrow(() -> new NoSuchElementException(ErrorMessage.BLACKBOX_NOT_FOUND.getMessage(blackboxId)));
		if (!blackbox.getUserId().equals(userId)) {
			throw new AuthorizationException(ErrorMessage.FORBIDDEN.getMessage());
		}
		LocalDateTime startOfDay = targetDate.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
		List<Metadata> metadataList = repository.findByBlackboxUuidBetween(
				blackboxId,
				startOfDay,
				endOfDay
		);
		return toResponseList(metadataList);
	}

	public void delete(String userId,String metadataId) {
		Metadata metadata = repository.findById(metadataId)
				.orElseThrow(() -> new NoSuchElementException(ErrorMessage.METADATA_NOT_FOUND.getMessage(metadataId)));

        if (metadata.getBlackbox() == null || !metadata.getBlackbox().getUserId().equals(userId)) {
            throw new AuthorizationException(ErrorMessage.FORBIDDEN.getMessage());
        }
        if (!metadata.isDeleted()){
            metadata.setDeleted(true);
        }
	}

	private List<MetadataResponse> toResponseList(List<Metadata> metadataList) {
		List<MetadataResponse> ret = new ArrayList<>();
		for (Metadata data : metadataList) {
			ret.add(MetadataResponse.builder()
					.objectKey(data.getObjectKey())
					.duration(data.getDuration())
					.createdAt(data.getCreatedAt())
					.fileSize(data.getFileSize())
					.fileType(data.getFileType()).build());
		}
		return ret;
	}
}
