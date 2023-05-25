package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.Model;

public interface DcatViewService {

	void create(ViewName viewName, Model dcat);

	void update(ViewName viewName, Model dcat);

	void delete(ViewName viewName);

}
