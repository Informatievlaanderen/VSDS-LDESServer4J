package be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity;

import jakarta.persistence.*;

@Entity
@Table(name = MemberAllocationEntity.FETCH_ALLOCATION, indexes = {
		@Index(columnList = "collectionName"),
		@Index(columnList = "fragmentId"),
		@Index(name = "collection_view", columnList = "collectionName, viewName"),
		@Index(name = "memberId_view", columnList = "collectionName, viewName, memberId")
})
public class MemberAllocationEntity {
	public static final String FETCH_ALLOCATION = "fetch_allocation";
	@Id
	@Column(length = 1024)
	private String id;
	private String collectionName;
	private String viewName;
	private String fragmentId;
	private String memberId;

	protected MemberAllocationEntity() {}

	public MemberAllocationEntity(String id, String collectionName, String viewName, String fragmentId,
			String memberId) {
		this.id = id;
		this.collectionName = collectionName;
		this.viewName = viewName;
		this.fragmentId = fragmentId;
		this.memberId = memberId;
	}

	public String getId() {
		return id;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getViewName() {
		return viewName;
	}

	public String getFragmentId() {
		return fragmentId;
	}

	public String getMemberId() {
		return memberId;
	}
}
