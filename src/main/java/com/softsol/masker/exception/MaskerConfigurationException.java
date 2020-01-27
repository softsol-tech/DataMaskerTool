package com.softsol.masker.exception;


public class MaskerConfigurationException extends RuntimeException {
	public MaskerConfigurationException(String message) {
		super(message);
	}

	public MaskerConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
