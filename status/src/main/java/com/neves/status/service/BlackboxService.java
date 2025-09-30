package com.neves.status.service;

import com.neves.status.controller.dto.blackbox.BlackboxRegisterRequestDto;
import com.neves.status.controller.dto.blackbox.BlackboxRenameRequestDto;
import com.neves.status.controller.dto.blackbox.BlackboxResponseDto;
import com.neves.status.controller.dto.blackbox.BlackboxStatus;
import com.neves.status.handler.ErrorMessage;
import com.neves.status.repository.Blackbox;
import com.neves.status.repository.BlackboxRepository;
import com.neves.status.repository.Metadata;
import com.neves.status.repository.MetadataRepository;
import com.neves.status.scheduler.BlackboxMailDto;
import com.neves.status.utils.CurrentTimeHolder;
import java.time.Duration;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlackboxService {

    private final BlackboxRepository blackboxRepository;
    private final MetadataRepository metadataRepository;

    private static final long UNHEALTHY_THRESHOLD_HOURS = 1;
    private static final long MAIL_THRESHOLD_HOURS = 1;

    @Transactional
    public BlackboxResponseDto register(String userId, BlackboxRegisterRequestDto request) {
        if (blackboxRepository.findByUuid(request.getUuid()).isPresent()) {
            throw ErrorMessage.ALREADY_REGISTERED_BLACKBOX.asException();
        }

        Blackbox newBlackbox = Blackbox.builder()
                .uuid(request.getUuid())
                .nickname(request.getNickname())
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();

        Blackbox savedBlackbox = blackboxRepository.save(newBlackbox);
        return BlackboxResponseDto.createUnhealthy(savedBlackbox);
    }

    @Transactional
    public BlackboxResponseDto rename(String userId, String blackboxId, BlackboxRenameRequestDto request) {
        Blackbox blackbox = blackboxRepository.findByUuid(blackboxId)
                .orElseThrow(ErrorMessage.BLACKBOX_NOT_FOUND.withArgs(blackboxId)::asException);

        if (!blackbox.getUserId().equals(userId)) {
            throw ErrorMessage.FORBIDDEN.asException();
        }

        blackbox.setNickname(request.getNickname());
        Blackbox updatedBlackbox = blackboxRepository.save(blackbox);
        return createDtoWithHealthy(updatedBlackbox);
    }

    @Transactional(readOnly = true)
    public List<BlackboxResponseDto> list(String userId) {
        List<Blackbox> blackboxes = blackboxRepository.findByUserId(userId);
        return blackboxes.stream()
                .map(this::createDtoWithHealthy)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BlackboxMailDto> findRequiredMailBlackbox() {
        LocalDateTime thresholdTime = CurrentTimeHolder.now().minusHours(MAIL_THRESHOLD_HOURS);
        List<Blackbox> blackboxesWithNoRecentMetadata = blackboxRepository.findBlackboxesWithNoRecentMetadata(thresholdTime);
        List<BlackboxMailDto> ret = new ArrayList<>();
        for (Blackbox blackbox : blackboxesWithNoRecentMetadata) {
            ret.add(BlackboxMailDto.builder()
                    .userId(blackbox.getUserId())
                    .nickname(blackbox.getNickname())
                    .blackboxId(blackbox.getUuid())
                    .lastConnectedAt(getLastConnectedAt(blackbox))
                    .build());
        }
        return ret;
    }

    @Transactional(readOnly = true)
    public BlackboxResponseDto getBlackboxStatus(String blackboxId) {
        Blackbox blackbox = blackboxRepository.findByUuid(blackboxId)
                .orElseThrow(ErrorMessage.BLACKBOX_NOT_FOUND.withArgs(blackboxId)::asException);

        return createDtoWithHealthy(blackbox);
    }

    private LocalDateTime getLastConnectedAt(Blackbox blackbox) {
        return metadataRepository.findFirstByBlackboxOrderByCreatedAtDesc(blackbox)
                .map(Metadata::getCreatedAt)
                .orElse(null);
    }

    private BlackboxStatus determineHealthStatus(LocalDateTime lastConnectedAt, LocalDateTime now) {
        if (lastConnectedAt == null) {
            return BlackboxStatus.UNHEALTHY;
        }
        Duration duration = Duration.between(lastConnectedAt, now);
        if (duration.toHours() >= UNHEALTHY_THRESHOLD_HOURS) {
            return BlackboxStatus.UNHEALTHY;
        }
        return BlackboxStatus.HEALTHY;
    }

    private BlackboxResponseDto createDtoWithHealthy(Blackbox blackbox) {
        LocalDateTime lastConnectedAt = getLastConnectedAt(blackbox);
        BlackboxStatus blackboxStatus = determineHealthStatus(lastConnectedAt, CurrentTimeHolder.now());
        return BlackboxResponseDto.createWithHealthy(blackbox, lastConnectedAt, blackboxStatus);
    }
}
