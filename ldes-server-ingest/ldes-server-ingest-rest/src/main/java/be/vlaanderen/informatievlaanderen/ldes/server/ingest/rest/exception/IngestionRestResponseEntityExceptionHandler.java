package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class IngestionRestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { ShaclValidationException.class })
	protected ResponseEntity<Object> handleShaclValidationException(
			ShaclValidationException ex, WebRequest request) {
		final Lang contentType = Lang.TTL;
		final HttpHeaders httpHeaders = new HttpHeaders();
		final String validationReport = RDFWriter.source(ex.getValidationReportModel()).lang(contentType).asString();
		httpHeaders.setContentType(MediaType.valueOf(contentType.getHeaderString()));
		return handleExceptionInternal(ex, validationReport, httpHeaders, HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = { MissingResourceException.class })
	protected ResponseEntity<Object> handleNotFoundException(RuntimeException ex, WebRequest request) {
		logger.error(ex.getMessage());
		String bodyOfResponse = ex.getMessage();
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

}
