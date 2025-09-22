package com.neves.status.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public abstract class JwtUtils {
	public static final String JWT_HEADER = "WWW-Authorization";
	private static final String USER_KEY = "user_id";
	private static final ObjectMapper mapper = new ObjectMapper();

	public static String extractUserIdFromJwt(String token) {
		String[] parts = token.split("\\.");
		String payload = new String(Base64.getDecoder().decode(parts[1]), StandardCharsets.UTF_8);

		try {
			Map<String, Object> payloadMap = mapper.readValue(payload, Map.class);
			return (String) payloadMap.get(USER_KEY);
		} catch (Exception e) {
			throw new RuntimeException("Failed to parse JWT payload", e);
		}
	}
}
