package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;

import java.util.List;
import java.util.Objects;

public class EventStream {
	private final String collection;
	private final String timestampPath;
	private final String versionOfPath;
	private final List<TreeNode> views;

	public EventStream(String collection, String timestampPath, String versionOfPath, List<TreeNode> views) {
		this.collection = collection;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.views = views;
	}

	public String getCollection() {
		return collection;
	}

	public String getTimestampPath() {
		return timestampPath;
	}

	public String getVersionOfPath() {
		return versionOfPath;
	}

	public List<TreeNode> getViews() {
		return views;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EventStream that)) return false;
		return Objects.equals(collection, that.collection);
	}

	@Override
	public int hashCode() {
		return Objects.hash(collection);
	}
}
