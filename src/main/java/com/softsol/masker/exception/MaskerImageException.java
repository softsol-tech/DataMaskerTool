package com.softsol.masker.exception;


public class MaskerImageException extends RuntimeException {
	public MaskerImageException(String message) {
		super(message);
	}

	public MaskerImageException(String message, Throwable cause) {
		super(message, cause);
	}
}
