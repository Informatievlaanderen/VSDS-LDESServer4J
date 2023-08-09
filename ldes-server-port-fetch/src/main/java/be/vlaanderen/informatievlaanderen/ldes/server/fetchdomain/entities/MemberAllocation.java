package be.vlaanderen.informatievlaanderen.ldes.server.fetchdomain.entities;

public class MemberAllocation {
	private final String id;
	private final String collectionName;
	private final String viewName;
	private final String fragmentId;
	private final String memberId;

	public MemberAllocation(String id, String collectionName, String viewName, String fragmentId, String memberId) {
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
