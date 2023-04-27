package be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities;

import org.apache.jena.rdf.model.*;

import java.util.List;
import java.util.Optional;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;

// TODO: 27/04/2023 cleanup and only keep what we need
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

	public Object getFragmentationObject(String subjectFilter, String fragmentationPredicate) {
		// @formatter:off
		return getFragmentationObjects(subjectFilter, fragmentationPredicate)
				.stream()
				.findFirst()
				.orElse(null);
		// @formatter:on
	}

	public List<Object> getFragmentationObjects(String subjectFilter, String fragmentationProperty) {
		// @formatter:off
		return model
				.listStatements(null, ResourceFactory.createProperty(fragmentationProperty), (Resource) null)
				.toList()
				.stream()
				.filter(statement -> statement.getSubject().toString().matches(subjectFilter))
				.map(Statement::getObject)
				.map(RDFNode::asLiteral)
				.map(Literal::getValue)
				.toList();
		// @formatter:on
	}

	public String getId() {
		return id;
	}

	private Optional<Statement> getCurrentTreeMemberStatement() {
		return model.listStatements(null, TREE_MEMBER, (Resource) null).nextOptional();
	}

	public void removeTreeMember() {
		getCurrentTreeMemberStatement().ifPresent(model::remove);
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Long getSequenceNr() {
		return sequenceNr;
	}
}
