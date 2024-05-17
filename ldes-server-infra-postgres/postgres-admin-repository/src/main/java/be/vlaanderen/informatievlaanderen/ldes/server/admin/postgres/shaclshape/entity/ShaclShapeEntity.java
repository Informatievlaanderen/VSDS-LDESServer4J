package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "shacl_shape")
public class ShaclShapeEntity {

	@Id
	private String id;

	@Column(columnDefinition = "text")
	private String model;

	protected ShaclShapeEntity() {}

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
