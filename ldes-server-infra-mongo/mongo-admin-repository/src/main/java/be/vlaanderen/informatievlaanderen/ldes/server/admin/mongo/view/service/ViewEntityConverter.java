package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewEntityConverter {

    public ViewEntity fromView(ViewSpecification viewSpecification) {
	    List<String> serializedRetentionModels = viewSpecification
			    .getRetentionConfigs()
			    .stream()
			    .map(retentionModel -> RDFWriter.source(retentionModel).lang(Lang.NQUADS).asString())
			    .toList();
	    return new ViewEntity(viewSpecification.getName().asString(), serializedRetentionModels,
			    viewSpecification.getFragmentations(), viewSpecification.getPageSize());
	}

	public ViewSpecification toView(ViewEntity viewEntity) {
		List<Model> retentionModels = viewEntity
				.getRetentionPolicies()
				.stream()
				.map(serializedRetentionModel -> RDFParser.fromString(serializedRetentionModel).lang(Lang.NQUADS).toModel())
				.toList();
		return new ViewSpecification(ViewName.fromString(viewEntity.getViewName()), retentionModels,
				viewEntity.getFragmentations(), viewEntity.getPageSize());
	}
}
