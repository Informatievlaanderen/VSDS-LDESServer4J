package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collation = "eventstreams")
public class EventStreamEntity {
	@Id
	private final String id;
	private final String timestampPath;
	private final String versionOfPath;
	private final List<TreeNode> views;

	public EventStreamEntity(String id, String timestampPath, String versionOfPath, List<TreeNode> views) {
		this.id = id;
		this.timestampPath = timestampPath;
		this.versionOfPath = versionOfPath;
		this.views = views;
	}

	public String getId() {
		return id;
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
}
