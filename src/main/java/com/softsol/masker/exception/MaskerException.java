package com.softsol.masker.exception;

public class MaskerException extends RuntimeException {
	public MaskerException(String message) {
		super(message);
	}

	public MaskerException(String message, Throwable cause) {
		super(message, cause);
	}
}
