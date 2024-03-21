package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.ViewCollection;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IngestedMemberHandler {

	private final MemberPropertiesRepository memberPropertiesRepository;
	private final ViewCollection viewCollection;

	public IngestedMemberHandler(MemberPropertiesRepository memberPropertiesRepository,
								 ViewCollection viewCollection) {
		this.memberPropertiesRepository = memberPropertiesRepository;
        this.viewCollection = viewCollection;
    }

	@EventListener
	public void handleMemberIngestedEvent(MemberIngestedEvent event) {
		final LocalDateTime timestamp = event.timestamp();
		final String versionOf = event.versionOf();
		final MemberProperties member = new MemberProperties(event.id(), event.collectionName(), versionOf, timestamp);
		addViewsToMember(member);

		memberPropertiesRepository.insert(member);
	}

	private void addViewsToMember(MemberProperties member) {
		viewCollection
				.getViews()
				.stream()
				.map(ViewSpecification::getName)
				.map(ViewName::asString)
				.forEach(member::addViewReference);
	}

}
