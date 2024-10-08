package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;

import java.util.List;

public interface MemberRemover {

	void removeMembersFromEventSource(List<MemberProperties> memberProperties);

	void deleteMembers(List<MemberProperties> memberProperties);
}
