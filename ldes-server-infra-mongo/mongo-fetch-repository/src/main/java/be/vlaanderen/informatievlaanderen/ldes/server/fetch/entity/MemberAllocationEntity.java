package be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import static be.vlaanderen.informatievlaanderen.ldes.server.fetch.entity.MemberAllocationEntity.FETCH_ALLOCATION;

@Document(FETCH_ALLOCATION)
@CompoundIndex(name = "collection_view", def = "{'collectionName' : 1, 'viewName': 1}")
@CompoundIndex(name = "memberId_view", def = "{'collectionName' : 1, 'viewName': 1, 'memberId' : 1}")
public class MemberAllocationEntity {
	public static final String FETCH_ALLOCATION = "fetch_allocation";
	@Id
	private final String id;
	@Indexed
	private final String collectionName;
	private final String viewName;
	@Indexed
	private final String fragmentId;
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
