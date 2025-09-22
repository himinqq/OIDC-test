package com.neves.status.service;

import com.neves.status.controller.dto.blackbox.BlackboxRegisterRequestDto;
import com.neves.status.controller.dto.blackbox.BlackboxResponseDto;
import com.neves.status.repository.Blackbox;
import com.neves.status.repository.BlackboxRepository;
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

    @Transactional
    public BlackboxResponseDto register(String userId, BlackboxRegisterRequestDto request) {
        if (blackboxRepository.findByUuid(request.getUuid()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 블랙박스입니다.");
        }

        Blackbox newBlackbox = Blackbox.builder()
                .uuid(request.getUuid())
                .nickname(request.getNickname())
                .userId(userId) // 실제 사용자 ID는 인증 정보에서 가져와야 함
                .createdAt(LocalDateTime.now())
                .build();

        Blackbox savedBlackbox = blackboxRepository.save(newBlackbox);
        return new BlackboxResponseDto(savedBlackbox);
    }

    @Transactional
    public BlackboxResponseDto rename(String blackboxId, BlackboxRegisterRequestDto request) {
        Blackbox blackbox = blackboxRepository.findByUuid(blackboxId)
                .orElseThrow(() -> new NoSuchElementException("블랙박스를 찾을 수 없습니다."));

        blackbox.setNickname(request.getNickname()); // 엔티티에 세터(setter)가 필요

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
}
