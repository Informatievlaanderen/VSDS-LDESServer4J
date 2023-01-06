package be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DeletedFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
		extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { MissingFragmentException.class })
	protected ResponseEntity<Object> handleMissingFragmentException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(value = { DeletedFragmentException.class })
	protected ResponseEntity<Object> handleDeletedFragmentException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.GONE, request);
	}

	@ExceptionHandler(value = { RdfFormatException.class })
	protected ResponseEntity<Object> handleRdfFormatException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
	}

	private ResponseEntity<Object> handleException(
			RuntimeException ex, HttpStatus status, WebRequest request) {
		logger.error(ex.getMessage());
		String bodyOfResponse = ex.getMessage();
		return handleExceptionInternal(ex, bodyOfResponse,
				new HttpHeaders(), status, request);
	}
}