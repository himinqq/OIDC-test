package com.neves.status.utils;

import java.time.LocalDateTime;

public abstract class CurrentTimeHolder {
	private static final ThreadLocal<LocalDateTime> requestTimeHolder = new ThreadLocal<>();

	public static void clear() {
		requestTimeHolder.remove();
	}

	public static void set(LocalDateTime requestTime) {
		requestTimeHolder.set(requestTime);
	}

	public static LocalDateTime now() {
		return requestTimeHolder.get();
	}
}
