package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

import java.util.List;
import java.util.Optional;

public interface ViewRepository {
	List<ViewSpecification> retrieveAllViews();

	void saveView(ViewSpecification viewSpecification);

	void deleteViewByViewName(ViewName viewName);

	Optional<ViewSpecification> getViewByViewName(ViewName viewName);

	List<ViewSpecification> retrieveAllViewsOfCollection(String collectionName);
}
