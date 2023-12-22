package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception;

import org.apache.jena.rdf.model.Model;

public class MemberIdNotFoundException extends RuntimeException {

	private final transient Model model;

	public MemberIdNotFoundException(Model model) {
		this.model = model;
	}

	@Override
	public String getMessage() {
		return "Member id could not be extracted from model: %n%n%s".formatted(model.listStatements().toList());
	}

}
