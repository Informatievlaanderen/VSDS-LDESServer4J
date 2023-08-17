package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

import java.util.List;

public interface ViewService {

	void addView(ViewSpecification viewSpecification);

	void addDefaultView(String collectionName);

	ViewSpecification getViewByViewName(ViewName viewName);

	List<ViewSpecification> getViewsByCollectionName(String collectionName);

	void deleteViewByViewName(ViewName viewName);

}
