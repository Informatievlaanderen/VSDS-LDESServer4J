package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.dcatdataset.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import org.apache.jena.rdf.model.Model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dcat_dataset")
public class DcatDatasetEntity {
	@Id
	private final String id;
	private final Model model;

	public DcatDatasetEntity(String id, Model model) {
		this.id = id;
		this.model = model;
	}

	public String getId() {
		return id;
	}

	public Model getModel() {
		return model;
	}

	public DcatDataset toDataset() {
		return new DcatDataset(getId(), getModel());
	}

	public static DcatDatasetEntity fromDataset(DcatDataset dataset) {
		return new DcatDatasetEntity(dataset.id(), dataset.model());
	}
}
