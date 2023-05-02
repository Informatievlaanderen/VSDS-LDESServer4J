package be.vlaanderen.informatievlaanderen.ldes.server.domain.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryViewCollection implements ViewCollection {
	private final Map<ViewName, ViewSpecification> views;
	private final ViewRepository viewRepository;

	public InMemoryViewCollection(ViewRepository viewRepository) {
		this.views = new HashMap<>();
		this.viewRepository = viewRepository;
	}

	@Override
	public Optional<ViewSpecification> getViewByViewName(ViewName viewName) {
		return Optional.ofNullable(views.get(viewName));
	}

	@Override
	public void addView(ViewSpecification viewSpecification) {
		viewRepository.saveView(viewSpecification);
		views.put(viewSpecification.getName(), viewSpecification);
	}

	@PostConstruct
	private void initShapeConfig() {
		viewRepository
				.retrieveAllViews()
				.forEach(viewSpecification -> views.put(viewSpecification.getName(), viewSpecification));
	}
}
