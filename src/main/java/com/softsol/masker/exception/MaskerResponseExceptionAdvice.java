package com.softsol.masker.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public class MaskerResponseExceptionAdvice {

	Logger logger = LoggerFactory.getLogger(MaskerResponseExceptionAdvice.class);

	@ExceptionHandler({ RuntimeException.class, MaskerFileStorageException.class, MaskerException.class })
	public void handleRunTimeException(RuntimeException e) {
		MaskApiError apiError = new MaskApiError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
		buildResponseEntity(apiError);
	}

	@ExceptionHandler({ UploadException.class })
	public void handleUploadException(UploadException e) {
		MaskApiError apiError = new MaskApiError(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		buildResponseEntity(apiError);
	}

	@ExceptionHandler({ MaskerFileNotFoundException.class })
	public void handleNotFoundException(UploadException e) {
		MaskApiError apiError = new MaskApiError(HttpStatus.NOT_FOUND, e.getMessage(), e);
		buildResponseEntity(apiError);
	}

	private @ResponseBody ResponseEntity<Object> buildResponseEntity(MaskApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}

}
