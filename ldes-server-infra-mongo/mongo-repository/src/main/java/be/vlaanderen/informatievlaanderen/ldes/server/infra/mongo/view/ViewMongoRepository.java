package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.service.ViewEntityConverter;

import java.util.List;

public class ViewMongoRepository implements ViewRepository {

	private final ViewEntityRepository repository;
	private final ViewEntityConverter converter = new ViewEntityConverter();

	public ViewMongoRepository(ViewEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<ViewSpecification> retrieveAllViews() {
		return repository
				.findAll()
				.stream()
				.map(converter::toView)
				.toList();
	}

	@Override
	public void saveView(ViewSpecification viewSpecification) {
		repository.save(converter.fromView(viewSpecification));
	}

	@Override
	public void deleteViewByViewName(ViewName viewName) {
		repository.deleteById(viewName.asString());
	}
}
