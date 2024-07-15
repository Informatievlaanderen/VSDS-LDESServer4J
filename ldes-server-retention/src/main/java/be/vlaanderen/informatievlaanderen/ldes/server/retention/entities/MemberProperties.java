package be.vlaanderen.informatievlaanderen.ldes.server.retention.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemberProperties {
	private final Long id;
	private final String collectionName;
	private final String versionOf;
	private final LocalDateTime timestamp;
	private final boolean isInEventSource;
	private final boolean isInView;

	public MemberProperties(Long id, String collectionName, String versionOf,
                            LocalDateTime timestamp, boolean isInEventSource, boolean isInView) {
		this.id = id;
		this.collectionName = collectionName;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
        this.isInEventSource = isInEventSource;
		this.isInView = isInView;
	}

	public Long getId() {
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

	public boolean isInEventSource() {
		return isInEventSource;
	}

	public boolean isInView() {
		return isInView;
	}
}
