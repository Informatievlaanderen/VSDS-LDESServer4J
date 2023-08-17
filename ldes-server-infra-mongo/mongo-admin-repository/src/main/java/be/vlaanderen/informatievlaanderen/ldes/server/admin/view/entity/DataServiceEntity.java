package be.vlaanderen.informatievlaanderen.ldes.server.admin.view.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dcat_dataservice")
public class DataServiceEntity {

	@Id
	private final String viewName;

	private final String model;

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
