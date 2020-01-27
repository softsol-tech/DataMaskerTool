package com.softsol.masker.exception;

public class MaskerFileNotFoundException extends RuntimeException {
	public MaskerFileNotFoundException(String message) {
		super(message);
	}

	public MaskerFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
