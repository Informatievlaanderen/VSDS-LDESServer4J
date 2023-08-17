package be.vlaanderen.informatievlaanderen.ldes.server.admin.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.view.entity.DataServiceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;

import java.io.StringWriter;

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
