package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity.DcatDatasetEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.PostgresAdminConstants.SERIALISATION_LANG;

@Component
public class DcatDatasetEntityConverter {

	public DcatDataset entitytoDataset(DcatDatasetEntity entity) {
		return new DcatDataset(entity.getCollectionName(), stringToModel(entity.getModel()));
	}

	public DcatDatasetEntity datasetToEntity(DcatDataset dataset) {
		return new DcatDatasetEntity(dataset.getCollectionName(), modelToString(dataset.getModel()));
	}

	private String modelToString(Model model) {
		return RDFWriter.source(model)
				.lang(SERIALISATION_LANG)
				.asString();
	}

	private Model stringToModel(String string) {
		return RDFParser.fromString(string)
				.lang(SERIALISATION_LANG)
				.toModel();
	}
}
