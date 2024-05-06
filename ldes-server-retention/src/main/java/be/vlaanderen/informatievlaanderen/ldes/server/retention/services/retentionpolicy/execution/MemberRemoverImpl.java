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
	private final ApplicationEventPublisher applicationEventPublisher;

	public MemberRemoverImpl(MemberPropertiesRepository memberPropertiesRepository,
			ApplicationEventPublisher applicationEventPublisher) {
		this.memberPropertiesRepository = memberPropertiesRepository;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public void removeMemberFromView(MemberProperties memberProperties, String viewName) {
		memberProperties.deleteViewReference(viewName);
		memberPropertiesRepository.removeViewReference(memberProperties.getId(), viewName);
		applicationEventPublisher
				.publishEvent(new MemberUnallocatedEvent(memberProperties.getId(), ViewName.fromString(viewName)));
	}

	@Override
	public void removeMembersFromEventSource(List<MemberProperties> memberProperties) {
		List<String> ids = memberProperties.stream().filter(MemberProperties::isInEventSource).map(MemberProperties::getId).toList();
		memberPropertiesRepository.removeFromEventSource(ids);
		applicationEventPublisher.publishEvent(
				new MembersRemovedFromEventSourceEvent(ids));
	}

	@Override
	public void deleteMembers(List<MemberProperties> memberProperties) {
		List<String> ids = memberProperties.stream().map(MemberProperties::getId).toList();
		memberPropertiesRepository.deleteAllByIds(ids);
		applicationEventPublisher.publishEvent(
				new MembersDeletedEvent(ids));
	}
}
