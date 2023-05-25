package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception.SnapshotCreationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.MissingViewException;
import org.apache.jena.riot.RiotException;
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
			MissingEventStreamException.class, MissingViewException.class, MissingServerDcatException.class})
	protected ResponseEntity<Object> handleMissingResourceException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(value = { LdesShaclValidationException.class, DcatAlreadyConfiguredException.class })
	protected ResponseEntity<Object> handleValidationException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = { RiotException.class })
	protected ResponseEntity<Object> handleRiotException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = { SnapshotCreationException.class })
	protected ResponseEntity<Object> handleSnapshotCreationException(
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
}
