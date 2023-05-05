package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.DuplicateViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewServiceImpl implements ViewService {

	private final ViewCollection viewCollection;

	public ViewServiceImpl(ViewCollection viewCollection) {
		this.viewCollection = viewCollection;
	}

	@Override
	public void addView(ViewSpecification viewSpecification) {
		if (viewCollection.getViewByViewName(viewSpecification.getName()).isEmpty()) {
			viewCollection.addView(viewSpecification);
		} else {
			throw new DuplicateViewException(viewSpecification.getName());
		}
	}

	@Override
	public ViewSpecification getViewByViewName(ViewName viewName) {
		throw new NotImplementedException();
	}

	@Override
	public List<ViewSpecification> getViewsByCollectionName(String collectionName) {
		throw new NotImplementedException();
	}

	@Override
	public void deleteViewByViewName(ViewName viewName) {
		viewCollection.deleteViewByViewName(viewName);
	}
}
