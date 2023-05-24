package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.Optional;

public interface DcatViewRepository {

	void create(DcatView dcatView);

	Optional<DcatView> findByViewName(ViewName viewName);

	void update(DcatView dcatView);

	void remove(ViewName viewName);

}
