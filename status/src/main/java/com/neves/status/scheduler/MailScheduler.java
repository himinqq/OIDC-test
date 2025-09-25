package com.neves.status.scheduler;

import com.neves.status.feign.MailClient;
import com.neves.status.feign.MailDto;
import com.neves.status.service.BlackboxService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component
public class MailScheduler {
	private final BlackboxService blackboxService;
	private final MailClient mailClient;
	private final Map<String, LocalDateTime> sentBlackboxes = new HashMap<>();

	@Scheduled(cron = "0 0 * * * *")
	public void checkDisconnectedBlackboxes() {
		log.info("Checking for disconnected blackboxes...");
		LocalDateTime now = LocalDateTime.now();
		List<BlackboxMailDto> disconnectedBlackbox = blackboxService.findRequiredMailBlackbox(now);
		for (BlackboxMailDto blackbox : disconnectedBlackbox) {
			LocalDateTime dateLastConnected = sentBlackboxes.get(blackbox.getBlackboxId());
			if (Objects.isNull(dateLastConnected) || !dateLastConnected.isEqual(blackbox.getLastConnectedAt())) {
				mailClient.sendMail(MailDto.builder()
						.to(blackbox.getUserId())
						.format(MailClient.BLACKBOX_UNCONNECTED)
						.parameters(List.of(blackbox.getNickname(), blackbox.getLastConnectedAt().toString()))
						.build());
				sentBlackboxes.put(blackbox.getBlackboxId(), blackbox.getLastConnectedAt());
			}
		}
	}
}
