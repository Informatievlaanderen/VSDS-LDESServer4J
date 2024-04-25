package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.entity.DataServiceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.PostgresAdminAutoConfiguration.SERIALISATION_LANG;

public class DcatServiceEntityConverter {

	public DataServiceEntity fromDcatView(DcatView dcatView) {
		String dcatAsString = RDFWriter.source(dcatView.getDcat())
				.lang(SERIALISATION_LANG)
				.asString();
		return new DataServiceEntity(dcatView.getViewName().asString(), dcatAsString);
	}

	public DcatView toDcatView(DataServiceEntity entity) {
		Model dcatModel = RDFParser.fromString(entity.getModel())
				.lang(SERIALISATION_LANG)
				.toModel();
		ViewName viewName = ViewName.fromString(entity.getViewName());
		return DcatView.from(viewName, dcatModel);
	}

}
