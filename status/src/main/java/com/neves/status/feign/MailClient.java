package com.neves.status.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "mailClient", url = "${feign.mail.url}")
public interface MailClient {
	String BLACKBOX_UNCONNECTED = "BLACKBOX_UNCONNECTED";
	@PostMapping("/api/email")
	void sendMail(MailDto dto);
}
