package be.vlaanderen.informatievlaanderen.ldes.server.admin.spi;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Optional;

public interface DcatViewService {

	void create(ViewName viewName, Model dcat);

	Optional<DcatView> findByViewName(ViewName viewName);

	void update(ViewName viewName, Model dcat);

	void delete(ViewName viewName);

	List<DcatView> findAll();
}
