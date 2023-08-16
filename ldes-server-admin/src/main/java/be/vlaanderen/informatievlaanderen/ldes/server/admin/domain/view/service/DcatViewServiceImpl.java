package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.MissingViewDcatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.Model;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DcatViewServiceImpl implements DcatViewService {

	private final DcatViewRepository dcatViewRepository;

	public DcatViewServiceImpl(DcatViewRepository dcatViewRepository) {
		this.dcatViewRepository = dcatViewRepository;
	}

	@Override
	public void create(ViewName viewName, Model dcat) {
		dcatViewRepository.save(DcatView.from(viewName, dcat));
	}

	@Override
	public Optional<DcatView> findByViewName(ViewName viewName) {
		return dcatViewRepository.findByViewName(viewName);
	}

	@Override
	public void update(ViewName viewName, Model dcat) {
		if (dcatViewRepository.findByViewName(viewName).isEmpty()) {
			throw new MissingViewDcatException();
		}

		dcatViewRepository.save(DcatView.from(viewName, dcat));
	}

	@Override
	public void delete(ViewName viewName) {
		dcatViewRepository.delete(viewName);
	}

	@Override
	public List<DcatView> findAll() {
		return dcatViewRepository.findAll();
	}

	@EventListener
	public void handleEventStreamInitEvent(ViewDeletedEvent event) {
		delete(event.getViewName());
	}

}
