package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset7.shaclshape;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(ShaclShapeEntityV1.COLLECTION_NAME)
public class ShaclShapeEntityV1 {

	public static final String COLLECTION_NAME = "shacl_shape";

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
