package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import java.util.List;

public class EventStreamInfo {
	private final Model shacl;

	public EventStreamInfo(Model shacl) {
		this.shacl = shacl;
	}

	public List<Statement> convertToStatements() {
		return shacl.listStatements().toList();
	}
}
