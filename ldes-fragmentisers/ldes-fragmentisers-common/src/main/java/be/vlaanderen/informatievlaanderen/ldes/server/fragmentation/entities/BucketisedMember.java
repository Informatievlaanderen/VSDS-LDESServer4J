package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

public record BucketisedMember(String memberId, ViewName viewName, String fragmentId, Long sequenceNr) {
	public String getViewName() {
		return viewName.asString();
	}
}
