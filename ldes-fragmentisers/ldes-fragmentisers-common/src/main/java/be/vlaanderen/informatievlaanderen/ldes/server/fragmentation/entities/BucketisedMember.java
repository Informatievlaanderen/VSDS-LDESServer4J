package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

public record BucketisedMember(long memberId, ViewName viewName, String bucketDescriptor) {
	public String viewNameAsString() {
		return viewName.asString();
	}
}
