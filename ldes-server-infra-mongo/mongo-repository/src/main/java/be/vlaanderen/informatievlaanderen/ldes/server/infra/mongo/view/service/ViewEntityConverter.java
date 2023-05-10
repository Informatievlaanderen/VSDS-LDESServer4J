package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.entity.ViewEntity;

public class ViewEntityConverter {
	public ViewEntity fromView(ViewSpecification viewSpecification) {
		return new ViewEntity(viewSpecification.getName().asString(), viewSpecification.getRetentionConfigs(),
				viewSpecification.getFragmentations());
	}

	public ViewSpecification toView(ViewEntity viewEntity) {
		return new ViewSpecification(ViewName.fromString(viewEntity.getViewName()), viewEntity.getRetentionPolicies(),
				viewEntity.getFragmentations());
	}
}
