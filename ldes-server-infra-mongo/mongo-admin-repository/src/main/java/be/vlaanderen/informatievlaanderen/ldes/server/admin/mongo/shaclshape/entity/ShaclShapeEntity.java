package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.shaclshape.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "shacl_shape")
public class ShaclShapeEntity {

	@Id
	private final String id;

	private final String model;

	public ShaclShapeEntity(String id, String model) {
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
