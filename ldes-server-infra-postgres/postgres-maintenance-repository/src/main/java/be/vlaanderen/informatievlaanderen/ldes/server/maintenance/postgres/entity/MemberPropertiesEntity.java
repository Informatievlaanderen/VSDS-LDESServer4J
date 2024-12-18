package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = MemberPropertiesEntity.NAME, indexes = {
		@Index(columnList = "collectionName"),
		@Index(columnList = "versionOf"),
		@Index(columnList = "timestamp")
})
public class MemberPropertiesEntity {

	public static final String NAME = "retention_member_properties";

	@Id
	private String id;
	private String collectionName;
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<MemberViewsEntity> views;
	private boolean isInEventSource;
	private String versionOf;
	private LocalDateTime timestamp;

	protected MemberPropertiesEntity() {}
	public MemberPropertiesEntity(String id, String collectionName, Set<String> views, boolean isInEventSource, String versionOf,
	                              LocalDateTime timestamp) {
		this.id = id;
		this.collectionName = collectionName;
		this.views = convertViews(views);
		this.isInEventSource = isInEventSource;
		this.versionOf = versionOf;
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public Set<String> getViews() {
		return views.stream()
				.map(MemberViewsEntity::getView)
				.collect(Collectors.toSet());
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

	private Set<MemberViewsEntity> convertViews(Set<String> views) {
		return views.stream()
				.map(view -> new MemberViewsEntity(this, view))
				.collect(Collectors.toSet());
	}
}
