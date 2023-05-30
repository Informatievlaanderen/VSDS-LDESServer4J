package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dcat_dataset")
public class DcatDatasetEntity {
	@Id
	private final String id;
	private final String model;

	public DcatDatasetEntity(String id, String model) {
		this.id = id;
		this.model = model;
	}

	public String getId() {
		return id;
	}

	public String getModel() {
		return model;
	}

}
