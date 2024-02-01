package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewEntityConverter {

	private final RdfModelConverter rdfModelConverter;

    public ViewEntityConverter(RdfModelConverter rdfModelConverter) {
        this.rdfModelConverter = rdfModelConverter;
    }

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
				.map(serializedRetentionModel -> rdfModelConverter.fromString(serializedRetentionModel, Lang.NQUADS))
				.toList();
		return new ViewSpecification(ViewName.fromString(viewEntity.getViewName()), retentionModels,
				viewEntity.getFragmentations(), viewEntity.getPageSize());
	}
}
