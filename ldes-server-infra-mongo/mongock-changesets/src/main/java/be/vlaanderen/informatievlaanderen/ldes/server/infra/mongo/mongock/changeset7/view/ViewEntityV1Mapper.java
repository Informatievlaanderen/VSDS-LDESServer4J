package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.riot.Lang;

import java.util.List;

public class ViewEntityV1Mapper {

	public static ViewEntityV1 mapToEntity(ViewSpecification viewSpecification) {
		List<String> serializedRetentionModels = viewSpecification
				.getRetentionConfigs()
				.stream()
				.map(retentionModel -> RdfModelConverter.toString(retentionModel, Lang.NQUADS))
				.toList();
		return new ViewEntityV1(viewSpecification.getName().asString(), serializedRetentionModels,
				viewSpecification.getFragmentations().stream().map(FragmentationRenamer::rename).toList());
	}

}
