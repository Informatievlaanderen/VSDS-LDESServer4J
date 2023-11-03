package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

import org.apache.jena.rdf.model.Model;

public class ShaclValidationException extends RuntimeException {
	private final String validationReport;
	private final transient Model validationReportModel;

	public ShaclValidationException(String validationReport, Model validationReportModel) {
		this.validationReportModel = validationReportModel;
		this.validationReport = validationReport;
	}

	@Override
	public String getMessage() {
		return "Shacl validation failed: \n\n" + validationReport;
	}

	public Model getValidationReportModel() {
		return validationReportModel;
	}

}
