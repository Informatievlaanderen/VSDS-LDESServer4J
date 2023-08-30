package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.exceptions.ExistingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.exceptions.DcatAlreadyConfiguredException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.exceptions.MissingDcatServerException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.exceptions.MissingStatementException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.exceptions.MissingShaclShapeException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.DuplicateViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.MissingViewDcatException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.MissingViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.LdesShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.*;
import org.apache.jena.riot.RiotException;
import org.apache.jena.shared.PropertyNotFoundException;
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
public class AdminRestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(AdminRestResponseEntityExceptionHandler.class);

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
		log.error(ex.getMessage());
		String bodyOfResponse = ex.getMessage();
		return handleExceptionInternal(ex, bodyOfResponse,
				new HttpHeaders(), status, request);
	}

	private ResponseEntity<Object> handleExceptionWithCustomMessage(
			RuntimeException ex, String body, HttpStatus status, WebRequest request) {
		log.error(body);
		return handleExceptionInternal(ex, body,
				new HttpHeaders(), status, request);
	}
}
