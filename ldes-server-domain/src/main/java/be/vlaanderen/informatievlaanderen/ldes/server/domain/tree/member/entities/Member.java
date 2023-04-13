package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities;

import org.apache.jena.rdf.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;

public class Member {

	private final Model memberModel;
	private final String collectionName;
	private final Long sequenceNr;
	private final String memberId;
	private final String versionOf;
	private final LocalDateTime timestamp;
	private final List<String> treeNodeReferences;

	public Member(String memberId, String collectionName, Long sequenceNr, String versionOf, LocalDateTime timestamp,
			final Model memberModel, List<String> treeNodeReferences) {
		this.collectionName = collectionName;
		this.memberId = memberId;
		this.sequenceNr = sequenceNr;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
		this.memberModel = memberModel;
		this.treeNodeReferences = treeNodeReferences;
	}

	public Model getModel() {
		return memberModel;
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
		return memberModel
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

	public String getLdesMemberId() {
		return memberId;
	}

	private Optional<Statement> getCurrentTreeMemberStatement() {
		return memberModel.listStatements(null, TREE_MEMBER, (Resource) null).nextOptional();
	}

	public List<String> getTreeNodeReferences() {
		return treeNodeReferences;
	}

	public void removeTreeMember() {
		getCurrentTreeMemberStatement().ifPresent(memberModel::remove);
	}

	public String getVersionOf() {
		return versionOf;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Long getSequenceNr() {
		return sequenceNr;
	}
}
