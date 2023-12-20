package be.vlaanderen.informatievlaanderen.ldes.server.rest.exceptionhandling;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RelativeUrlException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	@ExceptionHandler(value = { MissingResourceException.class })
	protected ResponseEntity<Object> handleNotFoundException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(value = { RdfFormatException.class, RelativeUrlException.class })
	protected ResponseEntity<Object> handleRdfFormatException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
	}

	@ExceptionHandler(value = { ShaclValidationException.class })
	protected ResponseEntity<Object> handleInternalServerError(
			RuntimeException ex, WebRequest request) {
		return handleExceptionWithoutDetails(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	private ResponseEntity<Object> handleException(
			RuntimeException ex, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		String bodyOfResponse = ex.getMessage();
		return handleExceptionInternal(ex, bodyOfResponse,
				new HttpHeaders(), status, request);
	}

	private ResponseEntity<Object> handleExceptionWithoutDetails(
			RuntimeException ex, HttpStatus status, WebRequest request) {
		log.error(ex.getMessage());
		return handleExceptionInternal(ex, null, new HttpHeaders(), status, request);
	}
}
