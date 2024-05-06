package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemberProperties {
	private final String id;
	private final String collectionName;
	private final String versionOf;
	private final LocalDateTime timestamp;
	private final Set<String> viewReferences;
	private final boolean isInEventSource;

	public MemberProperties(String id, String collectionName, String versionOf,
                            LocalDateTime timestamp, boolean isInEventSource) {
		this.id = id;
		this.collectionName = collectionName;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
        this.isInEventSource = isInEventSource;
        this.viewReferences = new HashSet<>();
	}

	public String getId() {
		return id;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getVersionOf() {
		return versionOf;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public Set<String> getViewReferences() {
		return viewReferences;
	}

	public boolean isInEventSource() {
		return isInEventSource;
	}

	public boolean containsViewReference(String viewName) {
		return viewReferences.contains(viewName);
	}

	public void addViewReference(String viewName) {
		viewReferences.add(viewName);
	}

	public void addAllViewReferences(List<String> viewNames) {
		viewReferences.addAll(viewNames);
	}

	public void deleteViewReference(String viewName) {
		viewReferences.remove(viewName);
	}
}
