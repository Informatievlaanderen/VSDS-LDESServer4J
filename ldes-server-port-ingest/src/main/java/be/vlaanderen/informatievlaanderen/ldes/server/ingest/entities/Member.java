package be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

// TODO: 27/04/2023 testing
public class Member {

	public static final String TREE = "https://w3id.org/tree#";
	public static final Property TREE_MEMBER = createProperty(TREE, "member");

	private final String id;
	private final String collectionName;
	private final Long sequenceNr;
	private final Model model;

	public Member(String id, String collectionName, Long sequenceNr, Model model) {
		this.id = id;
		this.collectionName = collectionName;
		this.sequenceNr = sequenceNr;
		this.model = model;
	}

	public Model getModel() {
		return model;
	}

	public String getId() {
		return id;
	}

	public void removeTreeMember() {
		getCurrentTreeMemberStatement().ifPresent(model::remove);
	}

	private Optional<Statement> getCurrentTreeMemberStatement() {
		return model.listStatements(null, TREE_MEMBER, (Resource) null).nextOptional();
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Long getSequenceNr() {
		return sequenceNr;
	}

}
