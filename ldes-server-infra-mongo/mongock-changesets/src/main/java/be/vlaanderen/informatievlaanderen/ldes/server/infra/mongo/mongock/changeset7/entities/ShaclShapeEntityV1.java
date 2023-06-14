package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shacl_shape")
public class ShaclShapeEntityV1 {

	@Id
	private final String id;

	private final String model;

	public ShaclShapeEntityV1(String id, String model) {
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
