package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RetentionModelSerializer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewEntityConverter {

	private final RetentionModelSerializer retentionModelSerializer;

    public ViewEntityConverter(RetentionModelSerializer retentionModelSerializer) {
        this.retentionModelSerializer = retentionModelSerializer;
    }

    public ViewEntity fromView(ViewSpecification viewSpecification) {
		List<String> serializedRetentionModels = retentionModelSerializer.serialize(viewSpecification.getRetentionConfigs());
		return new ViewEntity(viewSpecification.getName().asString(), serializedRetentionModels,
				viewSpecification.getFragmentations(), viewSpecification.getPageSize());
	}

	public ViewSpecification toView(ViewEntity viewEntity) {
		List<Model> retentionModels = retentionModelSerializer.deserialize(viewEntity.getRetentionPolicies());
		return new ViewSpecification(ViewName.fromString(viewEntity.getViewName()), retentionModels,
				viewEntity.getFragmentations(), viewEntity.getPageSize());
	}
}
