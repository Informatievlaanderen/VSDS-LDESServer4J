package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.entity.DcatDatasetEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

public class DcatDatasetEntityConverter {

	public DcatDataset entitytoDataset(DcatDatasetEntity entity) {
		return new DcatDataset(entity.getCollectionName(), stringToModel(entity.getModel()));
	}

	public DcatDatasetEntity datasetToEntity(DcatDataset dataset) {
		return new DcatDatasetEntity(dataset.collectionName(), modelToString(dataset.model()));
	}

	private String modelToString(Model model) {
		return RDFWriter.source(model).lang(Lang.TURTLE).asString();
	}

	private Model stringToModel(String string) {
		return RDFParserBuilder.create().fromString(string).lang(Lang.TURTLE).toModel();
	}
}
