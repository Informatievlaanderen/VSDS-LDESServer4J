package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class LdesShaclValidationException extends RuntimeException {

	private final String validationReport;

	public LdesShaclValidationException(String validationReport) {
		super();
		this.validationReport = validationReport;
	}

	@Override
	public String getMessage() {
		return "Shacl validation failed: \n\n" + validationReport;
	}
}
