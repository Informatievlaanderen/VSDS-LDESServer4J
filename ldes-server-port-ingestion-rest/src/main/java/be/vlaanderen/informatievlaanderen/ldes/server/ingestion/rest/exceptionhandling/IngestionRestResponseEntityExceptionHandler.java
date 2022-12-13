package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.exceptionhandling;

import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.exceptions.MalformedMemberIdException;
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
public class IngestionRestResponseEntityExceptionHandler
		extends ResponseEntityExceptionHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(IngestionRestResponseEntityExceptionHandler.class);

	@ExceptionHandler(value = { MalformedMemberIdException.class })
	protected ResponseEntity<Object> handleMalformedMemberIdException(
			RuntimeException ex, WebRequest request) {
		LOGGER.error(ex.getMessage());
		String bodyOfResponse = ex.getMessage();
		return handleExceptionInternal(ex, bodyOfResponse,
				new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
}
