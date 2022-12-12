package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class FragmentationPropertyException extends RuntimeException {

	private final String memberId;
	private final String sparqlQuery;

	public FragmentationPropertyException(String memberId, String sparqlQuery) {
		super();
		this.memberId = memberId;
		this.sparqlQuery = sparqlQuery;
	}

	@Override
	public String getMessage() {
		return "Cannot find fragmentationProperty on member " + memberId + " with SPARQL Query " + sparqlQuery;
	}
}
