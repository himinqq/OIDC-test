package com.neves.status.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClientErrorException extends RuntimeException {
	private final ErrorMessage errorMessage;

	public HttpStatus getStatus() {
		return errorMessage.getStatus();
	}

	public ClientErrorException(HttpStatus status, String message) {
		super(message);
		this.errorMessage = new ErrorMessage(status, message);
	}

	public ClientErrorException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
		this.errorMessage = errorMessage;
	}
}
