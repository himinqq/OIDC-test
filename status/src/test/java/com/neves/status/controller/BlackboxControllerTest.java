package com.neves.status.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neves.status.controller.dto.blackbox.BlackboxRegisterRequestDto;
import com.neves.status.controller.dto.blackbox.BlackboxRenameRequestDto;
import com.neves.status.repository.Blackbox;
import com.neves.status.repository.BlackboxRepository;
import com.neves.status.repository.Metadata;
import com.neves.status.repository.MetadataRepository;
import com.neves.status.utils.JwtUtils;
import com.neves.status.utils.TestUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BlackboxControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BlackboxRepository blackboxRepository;
    @Autowired
    private MetadataRepository metadataRepository;

    private static final String BASE_URL = "/blackboxes";

    @BeforeEach
    void setUp() {
        blackboxRepository.deleteAll();
    }

    @Test
    @DisplayName("블랙박스 등록 성공")
    void registerBlackboxSuccess() throws Exception {
        //given
        String nickname = "my blackbox";
        BlackboxRegisterRequestDto requestDto = new BlackboxRegisterRequestDto(TestUtils.DEFAULT_BLACKBOX_UUID,
                nickname);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        mockMvc.perform(post(BASE_URL)
                        .header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        //then
        Blackbox savedBlackbox = blackboxRepository.findByUuid(TestUtils.DEFAULT_BLACKBOX_UUID).orElse(null);
        Assertions.assertThat(savedBlackbox).isNotNull();
        Assertions.assertThat(savedBlackbox.getNickname()).isEqualTo(nickname);
        Assertions.assertThat(savedBlackbox.getUserId()).isEqualTo(TestUtils.TEST_USER_ID);
        Assertions.assertThat(savedBlackbox.getUuid()).isEqualTo(TestUtils.DEFAULT_BLACKBOX_UUID);
    }

    @Test
    @DisplayName("블랙박스 목록 조회 성공")
    void getBlackboxListSuccess() throws Exception {
        // given
        Blackbox blackbox1 = Blackbox.builder()
                .uuid(TestUtils.DEFAULT_BLACKBOX_UUID)
                .nickname("My Car")
                .userId(TestUtils.TEST_USER_ID)
                .createdAt(LocalDateTime.now())
                .build();

        Blackbox blackbox2 = Blackbox.builder()
                .uuid(java.util.UUID.randomUUID().toString())
                .nickname("My Bike")
                .userId(TestUtils.TEST_USER_ID)
                .createdAt(LocalDateTime.now())
                .build();

        blackboxRepository.save(blackbox1);
        blackboxRepository.save(blackbox2);

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].uuid").value(TestUtils.DEFAULT_BLACKBOX_UUID))
                .andExpect(jsonPath("$[0].nickname").value("My Car"))
                .andExpect(jsonPath("$[1].nickname").value("My Bike"));
    }

    @Test
    @DisplayName("블랙박스 목록이 비어있을 때 빈 리스트 반환")
    void getBlackboxListEmpty() throws Exception {
        // given
        // @BeforeEach로 인해 데이터베이스 비어있는 상태

        // when & then
        mockMvc.perform(get(BASE_URL)
                        .header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("블랙박스 이름 변경 성공")
    void renameBlackboxSuccess() throws Exception {
        // given
        String uuid = TestUtils.DEFAULT_BLACKBOX_UUID;
        String originalNickname = "기존 블랙박스 이름";
        String newNickname = "새로운 블랙박스 이름";

        Blackbox blackbox = Blackbox.builder()
                .uuid(uuid)
                .nickname(originalNickname)
                .userId(TestUtils.TEST_USER_ID)
                .createdAt(LocalDateTime.now())
                .build();
        blackboxRepository.save(blackbox);

        BlackboxRenameRequestDto requestDto = new BlackboxRenameRequestDto(newNickname);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when
        mockMvc.perform(put(BASE_URL + "/" + uuid)
                        .header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid))
                .andExpect(jsonPath("$.nickname").value(newNickname));

        // then
        Blackbox updatedBlackbox = blackboxRepository.findByUuid(uuid).orElseThrow();
        Assertions.assertThat(updatedBlackbox.getNickname()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("존재하지 않는 블랙박스 이름 변경 시 실패")
    void renameBlackboxNotFound() throws Exception {
        // given
        String nonExistentUuid = "존재하지-않는-UUID";
        String newNickname = "새로운 블랙박스 이름";

        BlackboxRenameRequestDto requestDto = new BlackboxRenameRequestDto(newNickname);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(put(BASE_URL + "/" + nonExistentUuid)
                        .header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("최근 메타데이터의 마지막 시간이 30분 전인 경우 HEALTHY")
    void getBlackboxStatusHealthy() throws Exception {
        // given
        String uuid = TestUtils.DEFAULT_BLACKBOX_UUID;
        Blackbox blackbox = Blackbox.builder()
                .uuid(uuid)
                .nickname("My Car")
                .userId(TestUtils.TEST_USER_ID)
                .createdAt(LocalDateTime.now())
                .build();
        blackboxRepository.save(blackbox);

        Metadata recentMetadata = Metadata.builder()
                .id("1")
                .blackbox(blackbox)
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .build();
        metadataRepository.save(recentMetadata);

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + uuid + "/status")
                        .header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.health_status").value("HEALTHY"));
    }

    @Test
    @DisplayName("최근 메타데이터의 마지막 시간이 61분 전인 경우(1시간 이상) UNHEALTHY")
    void getBlackboxStatusUnhealthy() throws Exception {
        // given
        String uuid = TestUtils.DEFAULT_BLACKBOX_UUID;
        Blackbox blackbox = Blackbox.builder()
                .uuid(uuid)
                .nickname("My Car")
                .userId(TestUtils.TEST_USER_ID)
                .createdAt(LocalDateTime.now())
                .build();
        blackboxRepository.save(blackbox);

        Metadata oldMetadata = Metadata.builder()
                .id("1")
                .blackbox(blackbox)
                .createdAt(LocalDateTime.now().minusMinutes(61))
                .build();
        metadataRepository.save(oldMetadata);

        // when & then
        mockMvc.perform(get(BASE_URL + "/" + uuid + "/status")
                        .header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.health_status").value("UNHEALTHY"));
    }

}