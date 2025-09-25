package com.neves.status.scheduler;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class BlackboxMailDto {
	private final String userId;
	private final String nickname;
	private final String blackboxId;
	private final LocalDateTime lastConnectedAt;
}
