package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.entity.ViewEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

import java.util.List;

public class ViewEntityConverter {
	public ViewEntity fromView(ViewSpecification viewSpecification) {
		List<String> serializedRetentionModels = viewSpecification
				.getRetentionConfigs()
				.stream()
				.map(retentionModel -> RdfModelConverter.toString(retentionModel, Lang.NQUADS))
				.toList();
		return new ViewEntity(viewSpecification.getName().asString(), serializedRetentionModels,
				viewSpecification.getFragmentations(), viewSpecification.getPageSize());
	}

	public ViewSpecification toView(ViewEntity viewEntity) {
		List<Model> retentionModels = viewEntity
				.getRetentionPolicies()
				.stream()
				.map(serializedRetentionModel -> RdfModelConverter.fromString(serializedRetentionModel, Lang.NQUADS))
				.toList();
		return new ViewSpecification(ViewName.fromString(viewEntity.getViewName()), retentionModels,
				viewEntity.getFragmentations(), viewEntity.getPageSize());
	}
}
