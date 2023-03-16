package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class LdesStreamShaclValidationException extends RuntimeException {
	private final String validationReport;

	public LdesStreamShaclValidationException(String validationReport) {
		this.validationReport = validationReport;
	}

	@Override
	public String getMessage() {
		return "Shacl validation failed for ldes stream configuration: \n\n" + validationReport;
	}
}
