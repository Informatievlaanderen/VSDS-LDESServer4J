package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.FragmentationConfigEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.PostgresAdminConstants.SERIALISATION_LANG;

public class ViewEntityConverter {

	public ViewEntity fromView(ViewSpecification viewSpecification) {
		List<String> serializedRetentionModels = viewSpecification
				.getRetentionConfigs()
				.stream()
				.map(retentionModel -> RDFWriter.source(retentionModel).lang(SERIALISATION_LANG).asString())
				.toList();
		return new ViewEntity(viewSpecification.getName().asString(), serializedRetentionModels,
				viewSpecification.getFragmentations()
						.stream()
						.map(FragmentationConfigEntity::toEntity).toList(), viewSpecification.getPageSize());
	}

	public ViewSpecification toView(ViewEntity viewEntity) {
		List<Model> retentionModels = viewEntity
				.getRetentionPolicies()
				.stream()
				.map(serializedRetentionModel -> RDFParser.fromString(serializedRetentionModel).lang(SERIALISATION_LANG).toModel())
				.toList();
		return new ViewSpecification(ViewName.fromString(viewEntity.getViewName()), retentionModels,
				viewEntity.getFragmentations().stream().map(FragmentationConfigEntity::fromEntity).toList(), viewEntity.getPageSize());
	}
}
