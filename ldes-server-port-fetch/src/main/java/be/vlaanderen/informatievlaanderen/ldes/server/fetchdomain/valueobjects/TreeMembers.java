package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.constants.RdfConstants.*;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class TreeMembers {
	private final String eventStreamIdentifier;
	private final List<TreeMember> treeMemberList;

	public TreeMembers(String eventStreamIdentifier, List<TreeMember> treeMemberList) {
		this.eventStreamIdentifier = eventStreamIdentifier;
		this.treeMemberList = treeMemberList;
	}

	public List<Statement> convertToStatements() {
		List<Statement> statements = new ArrayList<>();
		if (!treeMemberList.isEmpty()) {
			Resource eventStreamResource = createResource(eventStreamIdentifier);
			statements
					.add(createStatement(eventStreamResource, RDF_SYNTAX_TYPE, createResource(LDES_EVENT_STREAM_URI)));
			treeMemberList
					.forEach(treeMember -> statements.addAll(treeMember.convertToStatements(eventStreamResource)));
		}
		return statements;
	}
}
