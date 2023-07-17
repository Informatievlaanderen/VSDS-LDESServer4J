package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;

public interface MemberRemover {

	void removeMemberFromView(MemberProperties memberProperties, String viewName);
}
