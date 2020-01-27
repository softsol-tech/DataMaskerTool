package com.softsol.masker.exception;

public class MaskerFileStorageException extends RuntimeException {
	public MaskerFileStorageException(String message) {
		super(message);
	}

	public MaskerFileStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}