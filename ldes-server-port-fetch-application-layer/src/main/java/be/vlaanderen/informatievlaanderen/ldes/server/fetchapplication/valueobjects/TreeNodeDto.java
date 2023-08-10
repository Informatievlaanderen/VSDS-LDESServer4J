package be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.valueobjects;

import org.apache.jena.rdf.model.Model;

import java.util.List;

public class TreeNodeDto {
	private final Model model;
	private final String fragmentId;
	private final List<String> treeNodeIdsInRelations;
	private final List<String> memberIds;
	private final boolean immutable;

	public TreeNodeDto(Model model, String fragmentId, List<String> treeNodeIdsInRelations,
			List<String> memberIds, boolean immutable) {
		this.model = model;
		this.fragmentId = fragmentId;
		this.treeNodeIdsInRelations = treeNodeIdsInRelations;
		this.memberIds = memberIds;
		this.immutable = immutable;
	}

	public String getFragmentId() {
		return fragmentId;
	}

	public boolean isImmutable() {
		return immutable;
	}

	public List<String> getTreeNodeIdsInRelations() {
		return treeNodeIdsInRelations;
	}

	public Model getModel() {
		return model;
	}

	public List<String> getMemberIds() {
		return memberIds;
	}
}
