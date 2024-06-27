package be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;

public class PageRelation {
	private final Page fromPage;
	private final Page toPage;
	private final String treeRelationType;
	private final String treeValue;
	private final String treeValueType;
	private final String treePath;

	public PageRelation(Page fromPage, Page toPage, String treeRelationType, String treeValue, String treeValueType, String treePath) {
		this.fromPage = fromPage;
		this.toPage = toPage;
		this.treeRelationType = treeRelationType;
		this.treeValue = treeValue;
		this.treeValueType = treeValueType;
		this.treePath = treePath;
	}

	public Page getFromPage() {
		return fromPage;
	}

	public Page getToPage() {
		return toPage;
	}

	public String getTreeRelationType() {
		return treeRelationType;
	}

	public String getTreeValue() {
		return treeValue;
	}

	public String getTreeValueType() {
		return treeValueType;
	}

	public String getTreePath() {
		return treePath;
	}

	public static PageRelation createGenericRelation(Page fromPage, Page toPage) {
		return new PageRelation(fromPage, toPage, RdfConstants.GENERIC_TREE_RELATION, null, null, null);
	}
}
