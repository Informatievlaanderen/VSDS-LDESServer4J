package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.factory;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

public interface RootBucketCreator {
	void createRootBucketForView(ViewName viewName);
}
