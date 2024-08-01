package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MemberUnallocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MembersDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.retention.MembersRemovedFromEventSourceEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberRemoverImpl implements MemberRemover {

	private final MemberPropertiesRepository memberPropertiesRepository;

	public MemberRemoverImpl(MemberPropertiesRepository memberPropertiesRepository) {
		this.memberPropertiesRepository = memberPropertiesRepository;
	}

	@Override
	public void removeMembersFromEventSource(List<MemberProperties> memberProperties) {
		List<Long> ids = memberProperties.stream().filter(MemberProperties::isInEventSource).map(MemberProperties::getId).toList();
		if (!ids.isEmpty()) {
			memberPropertiesRepository.removeFromEventSource(ids);
		}
	}

	@Override
	public void deleteMembers(List<MemberProperties> memberProperties) {
		List<Long> ids = memberProperties.stream().map(MemberProperties::getId).toList();
		memberPropertiesRepository.deleteAllByIds(ids);
	}
}
