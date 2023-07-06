package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

public class Allocation {
	private final ViewName viewName;
	private final String fragmentId;
	private final String memberId;

	public Allocation(ViewName viewName, String fragmentId, String memberId) {
		this.viewName = viewName;
		this.fragmentId = fragmentId;
		this.memberId = memberId;
	}


}
