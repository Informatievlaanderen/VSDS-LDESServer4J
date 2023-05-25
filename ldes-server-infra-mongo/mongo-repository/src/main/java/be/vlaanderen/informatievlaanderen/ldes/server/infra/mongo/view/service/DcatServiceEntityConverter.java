package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.entity.DataServiceEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;

import java.io.StringWriter;

// TODO TVB: 25/05/2023 test
public class DcatServiceEntityConverter {

	public DataServiceEntity fromDcatView(DcatView dcatView) {
		StringWriter outputStream = new StringWriter();
		RDFDataMgr.write(outputStream, dcatView.getDcat(), Lang.NQUADS);
		String dcatAsString = outputStream.toString();
		return new DataServiceEntity(dcatView.getViewName().asString(), dcatAsString);
	}

	public DcatView toDcatView(DataServiceEntity entity) {
		Model dcatModel = RDFParserBuilder.create().fromString(entity.getModel()).lang(Lang.NQUADS).toModel();
		ViewName viewName = ViewName.fromString(entity.getViewName());
		return DcatView.from(viewName, dcatModel);
	}

}
