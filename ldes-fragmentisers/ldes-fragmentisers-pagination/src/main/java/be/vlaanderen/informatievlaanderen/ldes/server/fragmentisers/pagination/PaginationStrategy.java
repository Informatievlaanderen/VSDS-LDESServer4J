package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.fragmentation.MemberAllocatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategyDecorator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.pagination.services.OpenPageProvider;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static java.lang.Boolean.TRUE;

public class PaginationStrategy {
	public static final String PAGINATION_FRAGMENTATION = "PaginationFragmentation";

	private final OpenPageProvider openPageProvider;
	private final FragmentRepository fragmentRepository;
	private final ObservationRegistry observationRegistry;
	private final ApplicationEventPublisher eventPublisher;

	public PaginationStrategy(OpenPageProvider openPageProvider, ObservationRegistry observationRegistry,
                              FragmentRepository fragmentRepository, ApplicationEventPublisher eventPublisher) {
		this.fragmentRepository = fragmentRepository;
		this.openPageProvider = openPageProvider;
		this.observationRegistry = observationRegistry;
        this.eventPublisher = eventPublisher;
    }

	public void addMemberToFragment(LdesFragmentIdentifier parentFragmentId, Member member,
													  Observation parentObservation) {
		Observation paginationObservation = Observation.createNotStarted(PAGINATION_FRAGMENTATION,
				observationRegistry)
				.parentObservation(parentObservation)
				.start();
		Fragment ldesFragment = openPageProvider
				.retrieveOpenFragmentOrCreateNewFragment(parentFragmentId);


		eventPublisher.publishEvent(
				new MemberAllocatedEvent(member.id(), ldesFragment.getViewName().getCollectionName(),
						ldesFragment.getViewName().getViewName(), ldesFragment.getFragmentIdString()));
		fragmentRepository.incrementNrOfMembersAdded(ldesFragment.getFragmentId());


		paginationObservation.stop();
	}
}
