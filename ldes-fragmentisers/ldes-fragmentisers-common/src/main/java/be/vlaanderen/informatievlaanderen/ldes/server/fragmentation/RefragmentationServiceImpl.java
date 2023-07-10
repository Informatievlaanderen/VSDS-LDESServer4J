package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.EventSourceService;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class RefragmentationServiceImpl implements RefragmentationService {
	private final EventSourceService eventSourceService;
	private final ObservationRegistry observationRegistry;

	public RefragmentationServiceImpl(EventSourceService eventSourceService, ObservationRegistry observationRegistry) {
		this.eventSourceService = eventSourceService;
		this.observationRegistry = observationRegistry;
	}

	@Override
	public void refragmentMembersForView(Fragment rootFragmentForView,
			FragmentationStrategy fragmentationStrategyForView) {
		Stream<Member> memberStreamOfCollection = eventSourceService
				.getMemberStreamOfCollection(rootFragmentForView.getViewName().getCollectionName());
		memberStreamOfCollection
				.forEach(member -> fragmentMember(rootFragmentForView, fragmentationStrategyForView, member));
	}

	private void fragmentMember(Fragment rootFragmentForView, FragmentationStrategy fragmentationStrategyForView,
			Member member) {
		Observation parentObservation = Observation.createNotStarted("execute refragmentation",
				observationRegistry).start();
		fragmentationStrategyForView.addMemberToFragment(rootFragmentForView, member.getId(),
				member.getModel(), parentObservation);
		parentObservation.stop();
	}
}
