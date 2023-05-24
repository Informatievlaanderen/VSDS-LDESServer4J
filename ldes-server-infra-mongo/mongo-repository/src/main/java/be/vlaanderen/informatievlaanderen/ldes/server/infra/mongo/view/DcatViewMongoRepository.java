package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.repository.DataServiceEntityRepository;

import java.util.Optional;

// TODO TVB: 24/05/2023 test
// TODO TVB: 24/05/2023 impl
public class DcatViewMongoRepository implements DcatViewRepository {

	private final DataServiceEntityRepository dataServiceEntityRepository;

	public DcatViewMongoRepository(DataServiceEntityRepository dataServiceEntityRepository) {
		this.dataServiceEntityRepository = dataServiceEntityRepository;
	}

	@Override
	public void create(DcatView dcatView) {

	}

	@Override
	public Optional<DcatView> findByViewName(ViewName viewName) {
		return Optional.empty();
	}

	@Override
	public void update(DcatView dcatView) {

	}

	@Override
	public void remove(ViewName viewName) {

	}

}
