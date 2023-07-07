package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

public interface TreeNodeRemover {

	void removeLdesFragmentsOfView(ViewName viewName);

	void deleteTreeNodesByCollection(String collectionName);
}
