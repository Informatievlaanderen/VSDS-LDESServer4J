package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.List;

public interface ViewService {

	void addView(ViewSpecification viewSpecification);

	void addDefaultView(String collectionName);

	ViewSpecification getViewByViewName(ViewName viewName);

	List<ViewSpecification> getViewsByCollectionName(String collectionName);

	void deleteViewByViewName(ViewName viewName);

}
