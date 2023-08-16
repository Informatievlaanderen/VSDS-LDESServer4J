package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.entities.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.List;
import java.util.Optional;

public interface DcatViewRepository {

	void save(DcatView dcatView);

	Optional<DcatView> findByViewName(ViewName viewName);

	void delete(ViewName viewName);

	List<DcatView> findAll();

}
