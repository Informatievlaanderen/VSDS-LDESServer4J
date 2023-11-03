package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DcatViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DcatViewSavedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.apache.jena.rdf.model.Model;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DcatViewServiceImpl implements DcatViewService {

	private final DcatViewRepository dcatViewRepository;
	private final ApplicationEventPublisher eventPublisher;

	public DcatViewServiceImpl(DcatViewRepository dcatViewRepository, ApplicationEventPublisher eventPublisher) {
		this.dcatViewRepository = dcatViewRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void create(ViewName viewName, Model dcat) {
		DcatView dcatView = DcatView.from(viewName, dcat);
		dcatViewRepository.save(dcatView);
		eventPublisher.publishEvent(new DcatViewSavedEvent(dcatView));
	}

	@Override
	public Optional<DcatView> findByViewName(ViewName viewName) {
		return dcatViewRepository.findByViewName(viewName);
	}

	@Override
	public void update(ViewName viewName, Model dcat) {
		if (dcatViewRepository.findByViewName(viewName).isEmpty()) {
			throw new MissingResourceException("dcat-data-service", viewName.asString());
		}

		DcatView dcatView = DcatView.from(viewName, dcat);
		dcatViewRepository.save(dcatView);
		eventPublisher.publishEvent(new DcatViewSavedEvent(dcatView));
	}

	@Override
	public void delete(ViewName viewName) {
		dcatViewRepository.delete(viewName);
		eventPublisher.publishEvent(new DcatViewDeletedEvent(viewName));
	}

	@Override
	public List<DcatView> findAll() {
		return dcatViewRepository.findAll();
	}

	@EventListener
	public void handleEventStreamInitEvent(ViewDeletedEvent event) {
		delete(event.getViewName());
	}

	/**
	 * Initializes the dcatViews.
	 * The ApplicationReadyEvent is used instead of earlier spring lifecycle events
	 * to give db migrations such as mongock time before this init.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void initViews() {
		findAll().forEach(dcatView -> eventPublisher.publishEvent(new DcatViewSavedEvent(dcatView)));
	}

}
