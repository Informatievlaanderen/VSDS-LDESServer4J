package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MemberProperties {
	private final String id;
	private final String collectionName;
	private final String versionOf;
	private final LocalDateTime timestamp;
	private final List<String> viewReferences;

	public MemberProperties(String id, String collectionName, List<String> views, String versionOf,
			LocalDateTime timestamp) {
		this.id = id;
		this.collectionName = collectionName;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
		this.viewReferences=new ArrayList<>();
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

	public void addViewReference(String viewName){
		viewReferences.add(viewName);
	}

	public void deleteViewReference(String viewName){
		viewReferences.remove(viewName);
	}
}
