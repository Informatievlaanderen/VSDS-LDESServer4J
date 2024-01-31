package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.service.ViewEntityConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;

import java.util.List;
import java.util.Optional;

public class ViewMongoRepository implements ViewRepository {

	private final ViewEntityRepository repository;
	private final ViewEntityConverter converter;

	public ViewMongoRepository(ViewEntityRepository repository, ViewEntityConverter converter) {
		this.repository = repository;
        this.converter = converter;
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

	@Override
	public Optional<ViewSpecification> getViewByViewName(ViewName viewName) {
		return repository
				.findById(viewName.asString())
				.map(converter::toView);
	}

	@Override
	public List<ViewSpecification> retrieveAllViewsOfCollection(String collectionName) {
		return retrieveAllViews()
				.stream()
				.filter(viewSpecification -> viewSpecification.getName().getCollectionName().equals(collectionName))
				.toList();
	}
}
