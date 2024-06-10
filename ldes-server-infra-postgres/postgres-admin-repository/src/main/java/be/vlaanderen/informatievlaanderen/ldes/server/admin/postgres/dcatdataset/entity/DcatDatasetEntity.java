package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataset.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "old-dcat")
@Table(name = "dcat_dataset")
public class DcatDatasetEntity {
	@Id
	private String collectionName;
	@Column(columnDefinition = "text")
	private String model;

	protected DcatDatasetEntity() {}

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

}
