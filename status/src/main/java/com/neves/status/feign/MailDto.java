package com.neves.status.feign;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class MailDto {
	@JsonProperty("to")
	private final String to;
	@JsonProperty("format")
	private final String format;
	@JsonProperty("parameters")
	private final List<String> parameters;
}
