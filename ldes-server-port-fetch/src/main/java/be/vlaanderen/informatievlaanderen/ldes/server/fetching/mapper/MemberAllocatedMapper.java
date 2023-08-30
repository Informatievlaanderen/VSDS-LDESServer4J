package be.vlaanderen.informatievlaanderen.ldes.server.fetching.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import org.springframework.stereotype.Component;

@Component
public class MemberAllocatedMapper {

	public MemberAllocation toEntity(MemberAllocatedEvent memberAllocatedEvent) {
		String id = memberAllocatedEvent.memberId() + "/" + memberAllocatedEvent.fragmentId();
		return new MemberAllocation(id, memberAllocatedEvent.collectionName(), memberAllocatedEvent.viewName(),
				memberAllocatedEvent.fragmentId(), memberAllocatedEvent.memberId());
	}
}
