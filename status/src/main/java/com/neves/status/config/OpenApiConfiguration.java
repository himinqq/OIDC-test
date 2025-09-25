package com.neves.status.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
	@Bean
	public GroupedOpenApi openApi() {
		return GroupedOpenApi.builder()
				.group("neves")
				.addOpenApiCustomizer(e -> e.info(new Info().title("Status Server")) )
			.build();
	}

}
