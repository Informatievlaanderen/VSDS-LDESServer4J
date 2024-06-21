package be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation;


import org.springframework.context.ApplicationEvent;

public final class MemberAllocatedEvent extends ApplicationEvent {
	private final String memberId;
	private final String collectionName;
	private final String viewName;
	private final String fragmentId;

	public MemberAllocatedEvent(Object source, String memberId, String collectionName,
	                            String viewName, String fragmentId) {
		super(source);
		this.memberId = memberId;
		this.collectionName = collectionName;
		this.viewName = viewName;
		this.fragmentId = fragmentId;
	}

	public String memberId() {
		return memberId;
	}

	public String collectionName() {
		return collectionName;
	}

	public String viewName() {
		return viewName;
	}

	public String fragmentId() {
		return fragmentId;
	}

}
