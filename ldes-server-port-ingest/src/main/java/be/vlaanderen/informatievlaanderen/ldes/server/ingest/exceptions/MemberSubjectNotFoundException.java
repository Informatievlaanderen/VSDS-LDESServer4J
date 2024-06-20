package be.vlaanderen.informatievlaanderen.ldes.server.ingest.exceptions;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;

public class MemberSubjectNotFoundException extends RuntimeException {

	private final transient Model model;

	public MemberSubjectNotFoundException(Model model) {
		this.model = model;
	}

	@Override
	public String getMessage() {
		final String modelAsString = RDFWriter.source(model).lang(Lang.TURTLE).asString();
		return "Member id could not be extracted from model: %n%n%s".formatted(modelAsString);
	}

}
