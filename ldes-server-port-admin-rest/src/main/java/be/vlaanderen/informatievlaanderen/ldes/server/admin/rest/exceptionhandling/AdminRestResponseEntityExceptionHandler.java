package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptions.InvalidModelException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesStreamShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesStreamException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AdminRestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { MissingLdesStreamException.class })
	protected ResponseEntity<Object> handleMissingLdesStreamException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(value = { LdesStreamShaclValidationException.class })
	protected ResponseEntity<Object> handleLdesStreamShacleValidationException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = { InvalidModelException.class })
	protected ResponseEntity<Object> handleInvalidModelException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.BAD_REQUEST, request);
	}

	private ResponseEntity<Object> handleException(
			RuntimeException ex, HttpStatus status, WebRequest request) {
		logger.error(ex.getMessage());
		String bodyOfResponse = ex.getMessage();
		return handleExceptionInternal(ex, bodyOfResponse,
				new HttpHeaders(), status, request);
	}
}
