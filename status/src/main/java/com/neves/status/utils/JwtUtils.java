package com.neves.status.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neves.status.handler.ErrorMessage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import lombok.extern.log4j.Log4j2;


@Log4j2
public abstract class JwtUtils {

	public static final String JWT_HEADER = "WWW-Authorization";
	public static final String USER_KEY = "user_id";
	private static final ObjectMapper mapper = new ObjectMapper();

	public static String extractUserIdFromJwt(String token) {
		String payload;
		try {
			String[] parts = token.split("\\.");
			payload = new String(Base64.getDecoder().decode(parts[1]),
					StandardCharsets.UTF_8);
		} catch (RuntimeException e) {
			log.info("Failed to decode JWT token: {}", token);
			throw ErrorMessage.WRONG_FORMAT_JWT.asException();
		}

		try {
			Map<String, Object> payloadMap = mapper.readValue(payload, new TypeReference<>() {});
			log.info("Extracted userId from jwt token: {}", payloadMap.get(USER_KEY));
			return (String) payloadMap.get(USER_KEY);
		} catch (RuntimeException | JsonProcessingException e) {
			log.info("Failed to parse JWT payload: {}", payload);
			throw ErrorMessage.INVALID_JWT.asException();
		}
	}
}
