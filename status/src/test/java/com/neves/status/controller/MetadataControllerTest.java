package com.neves.status.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neves.status.controller.dto.blackbox.MetadataRegisterRequest;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MetadataControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MetadataRepository metadataRepository;
	@Autowired
	private BlackboxRepository blackboxRepository;
	private static final String BASE_URL = "/metadata";

	@BeforeEach
	void setUp() {
		metadataRepository.deleteAll();
		blackboxRepository.deleteAll();
	}

	@DisplayName("영상 메타데이터 생성")
	@Test
	public void createMetadata() throws Exception {
		// given
		MetadataRegisterRequest request = new MetadataRegisterRequest(
				TestUtils.DEFAULT_METADATA.getBlackbox().getUuid(),
				TestUtils.DEFAULT_METADATA.getStreamStartedAt(),
				TestUtils.DEFAULT_METADATA.getCreatedAt(),
				TestUtils.DEFAULT_METADATA.getFileSize(),
				TestUtils.DEFAULT_METADATA.getDuration(),
				TestUtils.DEFAULT_METADATA.getObjectKey(),
				TestUtils.DEFAULT_METADATA.getFileType()
		);
		blackboxRepository.save(TestUtils.DEFAULT_BLACKBOX);
		String requestBody = objectMapper.writeValueAsString(request);

		//when
		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isCreated());

		//then
		Metadata savedMetadata = metadataRepository.findAll().getFirst();
		assertThat(savedMetadata).isNotNull();
		assertThat(savedMetadata.getBlackbox().getUuid()).isEqualTo(request.getBlackboxUuid());
	}

	@DisplayName("영상 메타데이터 조회")
	@Test
	public void listMetadata() throws Exception {
		// given
		Blackbox blackbox = blackboxRepository.save(TestUtils.DEFAULT_BLACKBOX);
		Metadata metadata = TestUtils.DEFAULT_METADATA;
		metadata.setBlackbox(blackbox);
		metadataRepository.save(metadata);
		LocalDateTime day = metadata.getCreatedAt().toLocalDate().atStartOfDay();

		String url = BASE_URL + "?blackboxId=" + blackbox.getUuid() + "&date=" + day;

		// when
		String responseBody = mockMvc.perform(get(url)
				.header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		// then
		Assertions.assertThat(responseBody).contains(metadata.getObjectKey());
	}

	@DisplayName("영상 메타데이터 조회 - 잘못된 blackboxId")
	@Test
	public void listMetadata_WrongBlackboxId() throws Exception {
		// given
		Blackbox blackbox = blackboxRepository.save(TestUtils.DEFAULT_BLACKBOX);
		Metadata metadata = TestUtils.DEFAULT_METADATA;
		metadata.setBlackbox(blackbox);
		metadataRepository.save(metadata);
		LocalDateTime day = metadata.getCreatedAt().toLocalDate().atStartOfDay();

		String url = BASE_URL + "?blackboxId=" + "wrong-id" + "&date=" + day;

		// when
		mockMvc.perform(get(url)
                        .header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT))
				.andExpect(status().isNotFound());
		// then
		assertThat(metadataRepository.findById(metadata.getId())).isPresent();
	}

	@DisplayName("영상 삭제")
	@Test
	public void deleteMetadata() throws Exception {
		// given
		Blackbox blackbox = blackboxRepository.save(TestUtils.DEFAULT_BLACKBOX);
		Metadata metadata = TestUtils.DEFAULT_METADATA;
		metadata.setBlackbox(blackbox);
		metadata = metadataRepository.save(metadata);

		String url = BASE_URL + "/" + metadata.getId();

		// when
		mockMvc.perform(delete(url)
                        .header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT))
				.andExpect(status().isNoContent());

		// then
		assertThat(metadataRepository.findById(metadata.getId()).map(Metadata::isDeleted).orElse(true)).isTrue();
		mockMvc.perform(get(BASE_URL + "?blackboxId=" + blackbox.getUuid() + "&date=" + metadata.getCreatedAt().toLocalDate().atStartOfDay())
				    .header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT))
                .andExpect(status().isOk())
			    .andExpect(content().string("[]"));
	}

	@DisplayName("영상 삭제 - 잘못된 videoId")
	@Test
	public void deleteMetadata_WrongVideoId() throws Exception {
		// given
		Blackbox blackbox = blackboxRepository.save(TestUtils.DEFAULT_BLACKBOX);
		Metadata metadata = TestUtils.DEFAULT_METADATA;
		metadata.setBlackbox(blackbox);
		metadata = metadataRepository.save(metadata);

		String url = BASE_URL + "/" + "wrong-id";

		// when
		mockMvc.perform(delete(url)
                .header(JwtUtils.JWT_HEADER, TestUtils.DEFAULT_JWT))
			.andExpect(status().isNotFound());

		// then
		assertThat(metadataRepository.findById(metadata.getId())).isPresent();
	}
}