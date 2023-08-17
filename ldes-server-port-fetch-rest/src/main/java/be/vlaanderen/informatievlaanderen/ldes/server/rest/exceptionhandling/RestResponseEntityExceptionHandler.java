package be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.CollectionNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesShaclValidationException;
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

	@ExceptionHandler(value = { MissingFragmentException.class, CollectionNotFoundException.class })
	protected ResponseEntity<Object> handleNotFoundException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(value = { RdfFormatException.class })
	protected ResponseEntity<Object> handleRdfFormatException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
	}

	@ExceptionHandler(value = { LdesShaclValidationException.class })
	protected ResponseEntity<Object> handleInternalServerError(
			RuntimeException ex, WebRequest request) {
		return handleExceptionWithoutDetails(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	private ResponseEntity<Object> handleException(
			RuntimeException ex, HttpStatus status, WebRequest request) {
		// logger.error(ex.getMessage()); // TODO: 14/08/23 figure out what is wrong
		// here
		String bodyOfResponse = ex.getMessage();
		return handleExceptionInternal(ex, bodyOfResponse,
				new HttpHeaders(), status, request);
	}

	private ResponseEntity<Object> handleExceptionWithoutDetails(
			RuntimeException ex, HttpStatus status, WebRequest request) {
		// logger.error(ex.getMessage()); // TODO: 14/08/23 figure out what is wrong
		// here
		return handleExceptionInternal(ex, null, new HttpHeaders(), status, request);
	}
}