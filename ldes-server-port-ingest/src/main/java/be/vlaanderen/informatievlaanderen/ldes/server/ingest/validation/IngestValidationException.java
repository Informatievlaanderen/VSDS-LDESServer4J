package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation;

public class IngestValidationException extends RuntimeException {

	public IngestValidationException(String validationReport) {
		super("Validation failed: \n\n" + validationReport);
	}

}
