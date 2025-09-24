package com.neves.status.utils;

import com.neves.status.repository.Blackbox;
import com.neves.status.repository.Metadata;
import java.time.LocalDateTime;

public abstract class TestUtils {
	public static final String TEST_USER_ID = "testUser123";
	public static final String FORMAT_USER_ID = "{\"%s\":\"%s\"}";
	public static final String DEFAULT_PAYLOAD = java.util.Base64.getEncoder().encodeToString(String.format(FORMAT_USER_ID, JwtUtils.USER_KEY, TEST_USER_ID).getBytes());
	public static final String DEFAULT_JWT= "Bearer header." + DEFAULT_PAYLOAD + ".signature";
	public static final String DEFAULT_BLACKBOX_UUID = "blackbox-e89b-12d3-a456-426614174000";
	public static final String DEFAULT_METADATA_UUID = "metadata-e89b-12d3-a456-426614174000";
	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(1999, 9, 29, 10, 41, 0);

	public static final Blackbox DEFAULT_BLACKBOX = Blackbox.builder()
		.uuid(DEFAULT_BLACKBOX_UUID)
		.nickname("test blackbox")
		.userId(TEST_USER_ID)
		.createdAt(DEFAULT_TIME)
		.build();

	public static final Metadata DEFAULT_METADATA = Metadata.builder()
			.id(DEFAULT_METADATA_UUID)
			.blackbox(DEFAULT_BLACKBOX)
			.streamStartedAt(DEFAULT_TIME)
			.createdAt(DEFAULT_TIME)
			.fileSize(101010L)
			.duration(101010L)
			.objectKey("videos/test.mp4")
			.fileType("mp4")
			.build();

	public static String encodeUserId(String userId) {
		return java.util.Base64.getEncoder().encodeToString(String.format(FORMAT_USER_ID, JwtUtils.JWT_HEADER, userId).getBytes());
	}
}
