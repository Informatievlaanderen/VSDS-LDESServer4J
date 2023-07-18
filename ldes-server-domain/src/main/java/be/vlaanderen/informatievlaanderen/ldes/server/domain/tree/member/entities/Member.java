package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;

public class Member {

	private final Model memberModel;
	private final String collectionName;
	private final Long sequenceNr;
	private final String memberId;
	private final String versionOf;
	private final LocalDateTime timestamp;
	private final List<LdesFragmentIdentifier> treeNodeReferences;

	public Member(String memberId, String collectionName, Long sequenceNr, String versionOf, LocalDateTime timestamp,
			final Model memberModel, List<LdesFragmentIdentifier> treeNodeReferences) {
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

	public String getLdesMemberId() {
		return memberId;
	}

	public String getMemberIdWithoutPrefix() {
		if (memberId.startsWith("http")) {
			throw new IllegalStateException("id '%s' does not contain a prefix".formatted(memberId));
		}
		return memberId.substring(memberId.indexOf("/") + 1);
	}

	private Optional<Statement> getCurrentTreeMemberStatement() {
		return memberModel.listStatements(null, TREE_MEMBER, (Resource) null).nextOptional();
	}

	public List<LdesFragmentIdentifier> getTreeNodeReferences() {
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Member member = (Member) o;
		return memberId.equals(member.memberId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(memberId);
	}
}
