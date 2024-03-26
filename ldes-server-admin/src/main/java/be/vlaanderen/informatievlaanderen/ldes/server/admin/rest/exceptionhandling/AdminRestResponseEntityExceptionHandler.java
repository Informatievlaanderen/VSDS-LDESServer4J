package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.exceptions.MissingStatementException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.DuplicateRetentionException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AdminRestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(AdminRestResponseEntityExceptionHandler.class);

	@ExceptionHandler(value = {MissingResourceException.class})
	protected ResponseEntity<Object> handleMissingResourceException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(value = {RiotException.class, ExistingResourceException.class,
			IllegalArgumentException.class, MissingStatementException.class, DuplicateRetentionException.class})
	protected ResponseEntity<Object> handleBadRequest(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = {ShaclValidationException.class})
	protected ResponseEntity<Object> handleShaclValidationException(
			ShaclValidationException ex, WebRequest request) {
		String validationReport = RDFWriter.source(ex.getValidationReportModel()).lang(Lang.TURTLE).asString();
		var httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.valueOf(Lang.TURTLE.getHeaderString()));
		return handleExceptionInternal(ex, validationReport, httpHeaders, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = {RdfFormatException.class, RelativeUrlException.class})
	protected ResponseEntity<Object> handleUnsupportedMediaTypeException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
	}

	@ExceptionHandler(value = {Exception.class})
	protected ResponseEntity<Object> fallbackHandleException(
			RuntimeException ex, WebRequest request) {
		return handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	private ResponseEntity<Object> handleException(
			RuntimeException ex, HttpStatus status, WebRequest request) {
		log.warn(ex.getMessage());
		String bodyOfResponse = ex.getMessage();
		var httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.TEXT_PLAIN);
		return handleExceptionInternal(ex, bodyOfResponse, httpHeaders, status, request);
	}
}
