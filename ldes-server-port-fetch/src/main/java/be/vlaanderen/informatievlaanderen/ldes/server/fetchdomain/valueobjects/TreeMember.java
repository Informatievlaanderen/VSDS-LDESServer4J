package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.valueobjects;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.constants.RdfConstants.TREE_MEMBER;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.rdf.model.ResourceFactory.createStatement;

public class TreeMember {
	private final String treeMemberIdentifier;
	private final Model model;

	public TreeMember(String treeMemberIdentifier, Model model) {
		this.treeMemberIdentifier = treeMemberIdentifier;
		this.model = model;
	}

	public Collection<Statement> convertToStatements(Resource eventStreamResource) {
		List<Statement> statements = new ArrayList<>();
		statements.add(createStatement(eventStreamResource, TREE_MEMBER, createResource(treeMemberIdentifier)));
		statements.addAll(model.listStatements().toList());
		return statements;
	}

	public String getMemberId() {
		return treeMemberIdentifier;
	}
}
