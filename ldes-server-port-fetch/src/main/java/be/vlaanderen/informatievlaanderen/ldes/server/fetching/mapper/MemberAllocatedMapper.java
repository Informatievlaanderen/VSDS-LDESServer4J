package be.vlaanderen.informatievlaanderen.ldes.server.fetching.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.BulkMemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberAllocatedMapper {

	public MemberAllocation toEntity(MemberAllocatedEvent memberAllocatedEvent) {
		String id = memberAllocatedEvent.memberId() + "/" + memberAllocatedEvent.fragmentId();
		return new MemberAllocation(id, memberAllocatedEvent.collectionName(), memberAllocatedEvent.viewName(),
				memberAllocatedEvent.fragmentId(), memberAllocatedEvent.memberId());
	}

	public List<MemberAllocation> toEntities(BulkMemberAllocatedEvent event) {
		return event.membersOfCompactedFragments()
				.stream()
				.map(memberId -> {
					var id = memberId + event.fragmentId();
					return new MemberAllocation(id, event.collectionName(), event.viewName(), event.fragmentId(), memberId);
				}).toList();
	}
}
