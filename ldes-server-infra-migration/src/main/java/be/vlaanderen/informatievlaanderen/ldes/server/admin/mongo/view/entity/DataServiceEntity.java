package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.view.entity;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
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

	public DcatView toDcatView() {
		return DcatView.from(ViewName.fromString(viewName), RDFParser.fromString(model)
				.lang(Lang.NQUADS)
				.toModel());
	}

}
