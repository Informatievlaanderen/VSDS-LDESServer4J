package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.Model;

import java.util.Optional;

public interface DcatViewService {

	void create(ViewName viewName, Model dcat);

	Optional<DcatView> findByViewName(ViewName viewName);

	void update(ViewName viewName, Model dcat);

	void delete(ViewName viewName);

}
