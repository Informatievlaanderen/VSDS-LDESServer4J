package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import java.util.List;

public class EventStreamInfo {
	private final Model shacl;
	private final boolean isView;

	public EventStreamInfo(Model shacl, boolean isView) {
		this.shacl = shacl;
		this.isView = isView;
	}

	public List<Statement> convertToStatements() {
		if (isView) {
			return shacl.listStatements().toList();
		} else {
			return List.of();
		}
	}
}
