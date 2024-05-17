package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dcat_dataservice")
public class DataServiceEntity {

	@Id
	private String viewName;

	@Column(columnDefinition = "text")
	private String model;

	protected DataServiceEntity() {}

	public DataServiceEntity(String viewName, String model) {
		this.viewName = viewName;
		this.model = model;
	}

	public String getViewName() {
		return viewName;
	}

	public String getModel() {
		return model;
	}

}
