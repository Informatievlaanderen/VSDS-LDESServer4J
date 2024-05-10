package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.dcatdataset.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dcat_dataset")
public class DcatDatasetEntity {
	@Id
	private final String collectionName;
	private final String model;

	public DcatDatasetEntity(String collectionName, String model) {
		this.collectionName = collectionName;
		this.model = model;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getModel() {
		return model;
	}

	public DcatDataset toDcatDataset() {
		return new DcatDataset(collectionName, RDFParser.fromString(model)
				.lang(Lang.TURTLE)
				.toModel());
	}

}
