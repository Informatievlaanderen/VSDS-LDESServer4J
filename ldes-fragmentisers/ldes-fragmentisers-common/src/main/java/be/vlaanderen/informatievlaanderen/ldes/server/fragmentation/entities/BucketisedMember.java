package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

public record BucketisedMember(String memberId, ViewName viewName, String fragmentId) {
	public String getViewName() {
		return viewName.asString();
	}
}
