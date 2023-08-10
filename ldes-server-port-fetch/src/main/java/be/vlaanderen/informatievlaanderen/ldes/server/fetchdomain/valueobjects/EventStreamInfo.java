package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.constants.RdfConstants.IS_PART_OF_PROPERTY;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class EventStreamInfo {
	private final Model shacl;
	private final boolean isView;
	private final String treeNodeIdentifier;
	private final String eventStreamIdentifier;
	private final List<Statement> dcatStatements;

	public EventStreamInfo(String treeNodeIdentifier, String eventStreamIdentifier, Model shacl, boolean isView,
			List<Statement> dcatStatements) {
		this.treeNodeIdentifier = treeNodeIdentifier;
		this.eventStreamIdentifier = eventStreamIdentifier;
		this.shacl = shacl;
		this.isView = isView;
		this.dcatStatements = dcatStatements;
	}

	public List<Statement> convertToStatements() {
		List<Statement> statements = new ArrayList<>();
		if (isView) {
			statements.addAll(shacl.listStatements().toList());
			statements.addAll(dcatStatements);
		} else {
			statements.add(createStatement(createResource(treeNodeIdentifier), IS_PART_OF_PROPERTY,
					createResource(eventStreamIdentifier)));
		}
		return statements;
	}
}
