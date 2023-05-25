package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.DuplicateViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.MissingViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ViewServiceImpl implements ViewService {
	
	public static final String DEFAULT_VIEW_NAME = "by-page";
	public static final String DEFAULT_VIEW_FRAGMENTATION_STRATEGY = "pagination";
	public static final Map<String, String> DEFAULT_VIEW_FRAGMENTATION_PROPERTIES = Map.of("memberLimit", "100",
			"bidirectionalRelations", "false");

	private final DcatViewService dcatViewService;
	private final ViewRepository viewRepository;
	private final ApplicationEventPublisher eventPublisher;

	public ViewServiceImpl(DcatViewService dcatViewService, ViewRepository viewRepository,
						   ApplicationEventPublisher eventPublisher) {
		this.dcatViewService = dcatViewService;
		this.viewRepository = viewRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void addView(ViewSpecification viewSpecification) {
		Optional<ViewSpecification> view = viewRepository.getViewByViewName(viewSpecification.getName());
		if (view.isPresent()) {
			throw new DuplicateViewException(viewSpecification.getName());
		}

		viewRepository.saveView(viewSpecification);
		eventPublisher.publishEvent(new ViewAddedEvent(viewSpecification));
	}

	@Override
	public void addDefaultView(String collectionName) {
		ViewName defaultViewName = new ViewName(collectionName, DEFAULT_VIEW_NAME);
		FragmentationConfig fragmentation = new FragmentationConfig();
		fragmentation.setName(DEFAULT_VIEW_FRAGMENTATION_STRATEGY);
		fragmentation.setConfig(DEFAULT_VIEW_FRAGMENTATION_PROPERTIES);

		ViewSpecification defaultView = new ViewSpecification(defaultViewName, List.of(), List.of(fragmentation));
		addView(defaultView);
	}

	// TODO TVB: 25/05/2023 add dcat
	@Override
	public ViewSpecification getViewByViewName(ViewName viewName) {
		return viewRepository.getViewByViewName(viewName).orElseThrow(() -> new MissingViewException(viewName));
	}

	@Override
	public List<ViewSpecification> getViewsByCollectionName(String collectionName) {
		return viewRepository.retrieveAllViewsOfCollection(collectionName);
	}

	// TODO TVB: 25/05/2023 add test
	@Override
	public void deleteViewByViewName(ViewName viewName) {
		viewRepository.deleteViewByViewName(viewName);
		dcatViewService.delete(viewName);
		eventPublisher.publishEvent(new ViewDeletedEvent(viewName));
	}

	@EventListener(ApplicationStartedEvent.class)
	public void initViews() {
		viewRepository
				.retrieveAllViews()
				.forEach(viewSpecification -> eventPublisher
						.publishEvent(new ViewInitializationEvent(viewSpecification)));
	}

}
