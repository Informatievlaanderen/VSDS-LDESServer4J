package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.Optional;

public interface DcatViewRepository {

	void save(DcatView dcatView);

	Optional<DcatView> findByViewName(ViewName viewName);

	void delete(ViewName viewName);

}
