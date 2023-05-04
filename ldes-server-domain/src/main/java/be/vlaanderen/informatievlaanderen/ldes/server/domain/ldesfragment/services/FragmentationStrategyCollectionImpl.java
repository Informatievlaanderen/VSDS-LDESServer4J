package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.services.FragmentationStrategyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FragmentationStrategyCollectionImpl implements FragmentationStrategyCollection {

	private final Map<ViewName, FragmentationStrategy> fragmentationStrategyMap;
	private final RootFragmentCreator rootFragmentCreator;
	private final FragmentationStrategyCreator fragmentationStrategyCreator;

	// TODO when the definition of views in config is going to be deprecated, the
	// fragmentationStrategyMap should no longer be injected.
	// But start from an empty Map and be filled via ViewAddedEvents.
	public FragmentationStrategyCollectionImpl(
			@Qualifier("configured-fragmentation") Map<ViewName, FragmentationStrategy> fragmentationStrategyMap,
			RootFragmentCreator rootFragmentCreator, FragmentationStrategyCreator fragmentationStrategyCreator) {
		this.fragmentationStrategyMap = fragmentationStrategyMap;
		this.rootFragmentCreator = rootFragmentCreator;
		this.fragmentationStrategyCreator = fragmentationStrategyCreator;
	}

	public Map<ViewName, FragmentationStrategy> getFragmentationStrategyMap() {
		return Map.copyOf(fragmentationStrategyMap);
	}

	@EventListener
	public void handleViewAddedEvent(ViewAddedEvent event) {
		rootFragmentCreator.createRootFragmentForView(event.getViewName());
		fragmentationStrategyMap.put(event.getViewName(),
				fragmentationStrategyCreator.createFragmentationStrategyForView(event.getViewSpecification()));
	}
}
