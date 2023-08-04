package be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("fetch_allocation")
public class MemberAllocationEntity {
	@Id
	private final String id;
	@Indexed
	private final String collectionName;
	@Indexed
	private final String viewName;
	@Indexed
	private final String fragmentId;
	@Indexed
	private final String memberId;

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
