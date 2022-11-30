package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("memberreferences")
public class MemberReferencesEntity {
	@Id
	private final String id;
	private final List<String> treeNodesReferences;

	public MemberReferencesEntity(String id, List<String> treeNodesReferences) {
		this.id = id;
		this.treeNodesReferences = treeNodesReferences;
	}

	public void addMemberReference(String treeNodeId) {
		treeNodesReferences.add(treeNodeId);
	}

	public boolean hasMemberReferences() {
		return !treeNodesReferences.isEmpty();
	}

	public void removeMemberReference(String treeNodeId) {
		treeNodesReferences.remove(treeNodeId);
	}
}
