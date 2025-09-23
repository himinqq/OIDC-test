package com.neves.status.service;

import com.neves.status.controller.dto.blackbox.BlackboxRegisterRequestDto;
import com.neves.status.controller.dto.blackbox.BlackboxRenameRequestDto;
import com.neves.status.controller.dto.blackbox.BlackboxResponseDto;
import com.neves.status.controller.dto.blackbox.BlackboxStatus;
import com.neves.status.repository.Blackbox;
import com.neves.status.repository.BlackboxRepository;
import com.neves.status.repository.Metadata;
import com.neves.status.repository.MetadataRepository;
import java.time.Duration;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlackboxService {

    private final BlackboxRepository blackboxRepository;
    private final MetadataRepository metadataRepository;

    private static final long UNHEALTHY_THRESHOLD_HOURS = 1;

    @Transactional
    public BlackboxResponseDto register(String userId, BlackboxRegisterRequestDto request) {
        if (blackboxRepository.findByUuid(request.getUuid()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 블랙박스입니다.");
        }

        Blackbox newBlackbox = Blackbox.builder()
                .uuid(request.getUuid())
                .nickname(request.getNickname())
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();

        Blackbox savedBlackbox = blackboxRepository.save(newBlackbox);
        return new BlackboxResponseDto(savedBlackbox);
    }

    @Transactional
    public BlackboxResponseDto rename(String blackboxId, BlackboxRenameRequestDto request) {
        Blackbox blackbox = blackboxRepository.findByUuid(blackboxId)
                .orElseThrow(() -> new NoSuchElementException("블랙박스를 찾을 수 없습니다."));

        blackbox.setNickname(request.getNickname());

        Blackbox updatedBlackbox = blackboxRepository.save(blackbox);
        return new BlackboxResponseDto(updatedBlackbox);
    }

    @Transactional(readOnly = true)
    public List<BlackboxResponseDto> list(String userId) {
        List<Blackbox> blackboxes = blackboxRepository.findByUserId(userId);
        return blackboxes.stream()
                .map(BlackboxResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BlackboxResponseDto getBlackboxStatus(String blackboxId) {
        Blackbox blackbox = blackboxRepository.findByUuid(blackboxId)
                .orElseThrow(() -> new NoSuchElementException("블랙박스를 찾을 수 없습니다."));

        return mapToDtoWithHealthStatus(blackbox, LocalDateTime.now());
    }

    private BlackboxResponseDto mapToDtoWithHealthStatus(Blackbox blackbox, LocalDateTime now) {
        BlackboxResponseDto dto = new BlackboxResponseDto(blackbox);

        Metadata lastMetadata = metadataRepository.findFirstByBlackboxOrderByCreatedAtDesc(blackbox)
                .orElse(null);

        if (lastMetadata != null) {
            LocalDateTime lastConnectedAt = lastMetadata.getCreatedAt();

            dto.setHealthStatus(determineHealthStatus(lastConnectedAt, now));
            dto.setLastConnectedAt(lastConnectedAt);
        } else {
            dto.setHealthStatus(BlackboxStatus.UNHEALTHY);
            dto.setLastConnectedAt(null);
        }
        return dto;
    }

    private BlackboxStatus determineHealthStatus(LocalDateTime lastConnectedAt, LocalDateTime now) {
        Duration duration = Duration.between(lastConnectedAt, now);
        if (duration.toHours() >= UNHEALTHY_THRESHOLD_HOURS) {
            return BlackboxStatus.UNHEALTHY;
        }
        return BlackboxStatus.HEALTHY;
    }
}
