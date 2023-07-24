package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory.RootFragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.EventSourceService;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class RefragmentationServiceImpl implements RefragmentationService {

	private final RootFragmentCreator rootFragmentCreator;
	private final EventSourceService eventSourceService;
	private final ObservationRegistry observationRegistry;

	public RefragmentationServiceImpl(RootFragmentCreator rootFragmentCreator, EventSourceService eventSourceService,
			ObservationRegistry observationRegistry) {
		this.rootFragmentCreator = rootFragmentCreator;
		this.eventSourceService = eventSourceService;
		this.observationRegistry = observationRegistry;
	}

	// TODO TVB: 24/07/23 update test
	@Override
	public void refragmentMembersForView(ViewName viewName,
			FragmentationStrategy fragmentationStrategyForView) {
		Fragment rootFragmentForView = rootFragmentCreator.createRootFragmentForView(viewName);
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
