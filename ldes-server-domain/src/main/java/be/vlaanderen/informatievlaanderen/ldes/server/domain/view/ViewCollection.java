package be.vlaanderen.informatievlaanderen.ldes.server.domain.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;

import java.util.Optional;

public interface ViewCollection {

	Optional<ViewSpecification> getViewByViewName(ViewName viewName);

	void addView(ViewSpecification viewSpecification);

	void deleteViewByViewName(ViewName viewName);
}
