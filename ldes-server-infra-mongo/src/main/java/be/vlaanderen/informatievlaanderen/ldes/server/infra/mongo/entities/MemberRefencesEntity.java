package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("memberreferences")
public class MemberRefencesEntity {
	@Id
	private final String id;
	private final List<String> treeNodesRefences;

	public MemberRefencesEntity(String id, List<String> treeNodesRefences) {
		this.id = id;
		this.treeNodesRefences = treeNodesRefences;
	}

	public void addMemberReference(String treeNodeId) {
		treeNodesRefences.add(treeNodeId);
	}

	public boolean hasMemberReferences() {
		return !treeNodesRefences.isEmpty();
	}

	public void removeMemberReference(String treeNodeId) {
		treeNodesRefences.remove(treeNodeId);
	}
}
