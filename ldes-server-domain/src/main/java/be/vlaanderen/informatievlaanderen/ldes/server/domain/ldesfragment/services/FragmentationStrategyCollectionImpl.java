package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FragmentationStrategyCollectionImpl implements FragmentationStrategyCollection {

	private final Map<ViewName, FragmentationStrategy> fragmentationStrategyMap;
	private final RootFragmentCreator rootFragmentCreator;
	private final FragmentationStrategyCreator fragmentationStrategyCreator;
	private final RefragmentationService refragmentationService;
	private final LdesFragmentRemover ldesFragmentRemover;

	// TODO when the definition of views in config is going to be deprecated, the
	// fragmentationStrategyMap should no longer be injected.
	// But start from an empty Map and be filled via ViewAddedEvents.
	public FragmentationStrategyCollectionImpl(
			RootFragmentCreator rootFragmentCreator, FragmentationStrategyCreator fragmentationStrategyCreator,
			RefragmentationService refragmentationService, LdesFragmentRemover ldesFragmentRemover) {
		this.fragmentationStrategyMap = new HashMap<>();
		this.rootFragmentCreator = rootFragmentCreator;
		this.fragmentationStrategyCreator = fragmentationStrategyCreator;
		this.refragmentationService = refragmentationService;
		this.ldesFragmentRemover = ldesFragmentRemover;
	}

	public Map<ViewName, FragmentationStrategy> getFragmentationStrategyMap() {
		return Map.copyOf(fragmentationStrategyMap);
	}

	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		LdesFragment rootFragmentForView = rootFragmentCreator.createRootFragmentForView(event.getViewName());
		FragmentationStrategy fragmentationStrategyForView = fragmentationStrategyCreator
				.createFragmentationStrategyForView(event.getViewSpecification());
		refragmentationService.refragmentMembersForView(rootFragmentForView, fragmentationStrategyForView);
		fragmentationStrategyMap.put(event.getViewName(),
				fragmentationStrategyForView);
	}

	@EventListener
	public void handleViewInitializationEvent(ViewInitializationEvent event) {
		FragmentationStrategy fragmentationStrategyForView = fragmentationStrategyCreator
				.createFragmentationStrategyForView(event.getViewSpecification());
		fragmentationStrategyMap.put(event.getViewName(),
				fragmentationStrategyForView);
	}

	@EventListener
	public void handleViewDeletedEvent(ViewDeletedEvent event) {
		ldesFragmentRemover.removeLdesFragmentsOfView(event.getViewName());
		fragmentationStrategyMap.remove(event.getViewName());
	}
}
