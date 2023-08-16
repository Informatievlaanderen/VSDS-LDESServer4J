package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.exceptions.MissingDcatServerException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.*;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.DuplicateViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.MissingViewDcatException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.MissingViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.exception.SnapshotCreationException;
import org.apache.jena.riot.RiotException;
import org.apache.jena.shared.PropertyNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AdminRestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { MissingShaclShapeException.class,
			MissingEventStreamException.class, MissingViewException.class, MissingViewDcatException.class,
			MissingDcatServerException.class, MissingResourceException.class })
	protected ResponseEntity<Object> handleMissingResourceException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(value = { LdesShaclValidationException.class, RiotException.class,
			DcatAlreadyConfiguredException.class, IllegalArgumentException.class, DuplicateViewException.class,
			MissingStatementException.class })
	protected ResponseEntity<Object> handleBadRequest(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = { PropertyNotFoundException.class })
	protected ResponseEntity<Object> handlePropertyNotFoundException(
			RuntimeException ex, WebRequest request) {
		String message = "Could not find property of type: " + ex.getMessage();
		return handleExceptionWithCustomMessage(ex, message, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = { SnapshotCreationException.class })
	protected ResponseEntity<Object> handleSnapshotCreationException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler(value = { ExistingResourceException.class })
	protected ResponseEntity<Object> handleExistingResourceException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = { Exception.class })
	protected ResponseEntity<Object> fallbackHandleException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	private ResponseEntity<Object> handleException(
			RuntimeException ex, HttpStatus status, WebRequest request) {
		logger.error(ex.getMessage());
		String bodyOfResponse = ex.getMessage();
		return handleExceptionInternal(ex, bodyOfResponse,
				new HttpHeaders(), status, request);
	}

	private ResponseEntity<Object> handleExceptionWithCustomMessage(
			RuntimeException ex, String body, HttpStatus status, WebRequest request) {
		logger.error(body);
		return handleExceptionInternal(ex, body,
				new HttpHeaders(), status, request);
	}
}
