package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.entity.DcatDatasetEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

public class DcatDatasetEntityConverter {

	public DcatDataset entitytoDataset(DcatDatasetEntity entity) {
		return new DcatDataset(entity.getCollectionName(), stringToModel(entity.getModel()));
	}

	public DcatDatasetEntity datasetToEntity(DcatDataset dataset) {
		return new DcatDatasetEntity(dataset.getCollectionName(), modelToString(dataset.getModel()));
	}

	private String modelToString(Model model) {
		return RDFWriter.source(model).lang(Lang.TURTLE).asString();
	}

	private Model stringToModel(String string) {
		return RDFParserBuilder.create().fromString(string).lang(Lang.TURTLE).toModel();
	}
}
