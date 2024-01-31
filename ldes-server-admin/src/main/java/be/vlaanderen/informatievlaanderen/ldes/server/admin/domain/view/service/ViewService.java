package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;

import java.util.List;

public interface ViewService {

	void addView(ViewSpecification viewSpecification);

	ViewSpecification getViewByViewName(ViewName viewName);

	List<ViewSpecification> getViewsByCollectionName(String collectionName);

	void deleteViewByViewName(ViewName viewName);

	void deleteAllViewsByViewName(List<ViewName> viewNames);
}
