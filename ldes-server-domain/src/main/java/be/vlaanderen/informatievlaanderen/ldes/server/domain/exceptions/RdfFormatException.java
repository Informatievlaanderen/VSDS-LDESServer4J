package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class RdfFormatException extends RuntimeException {
	private final String inputFormat;
	private final RdfFormatContext rdfFormatContext;

	public RdfFormatException(String inputFormat, RdfFormatContext rdfFormatContext) {
		super();
		this.inputFormat = inputFormat;
		this.rdfFormatContext = rdfFormatContext;
	}

	@Override
	public String getMessage() {
		return rdfFormatContext + ": RDF format " + inputFormat + " is not a supported format.";
	}

	public enum RdfFormatContext {
		INGEST, FETCH, REST_ADMIN
	}

}
