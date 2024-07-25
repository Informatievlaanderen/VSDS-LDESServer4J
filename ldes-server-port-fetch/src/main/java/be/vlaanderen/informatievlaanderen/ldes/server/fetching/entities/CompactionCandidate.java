package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.Objects;

public class CompactionCandidate {
	private final Long id;
	private final Integer size;
	private TreeNode treeNode;

	public CompactionCandidate(Long id, Integer size) {
		this.id = id;
		this.size = size;
	}

	public TreeNode getTreeNode() {
		if (treeNode == null) {
			throw new IllegalStateException("Fragment has not yet been initialized");
		}
		return treeNode;
	}

	public void setTreeNode(TreeNode treeNode) {
		this.treeNode = treeNode;
	}

	public Long getId() {
		return id;
	}

	public Integer getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "CompactionCandidate{ id='" + id +"'}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CompactionCandidate that = (CompactionCandidate) o;
		return Objects.equals(id, that.id) && Objects.equals(size, that.size) && Objects.equals(treeNode, that.treeNode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, size, treeNode);
	}
}
