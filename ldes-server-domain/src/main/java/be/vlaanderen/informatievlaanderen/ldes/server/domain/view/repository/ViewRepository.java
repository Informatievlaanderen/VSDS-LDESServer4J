package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;

import java.util.List;

public interface ViewRepository {
	List<ViewSpecification> retrieveAllViews();

	void saveView(ViewSpecification viewSpecification);

	void deleteViewByViewName(ViewName viewName);
}
