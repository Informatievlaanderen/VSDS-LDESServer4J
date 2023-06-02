package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.entity.DataServiceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.repository.DataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.view.service.DcatServiceEntityConverter;

import java.util.List;
import java.util.Optional;

public class DcatViewMongoRepository implements DcatViewRepository {

	private final DataServiceEntityRepository dataServiceEntityRepository;
	private final DcatServiceEntityConverter dcatServiceEntityConverter;

	public DcatViewMongoRepository(DataServiceEntityRepository dataServiceEntityRepository,
			DcatServiceEntityConverter dcatServiceEntityConverter) {
		this.dataServiceEntityRepository = dataServiceEntityRepository;
		this.dcatServiceEntityConverter = dcatServiceEntityConverter;
	}

	@Override
	public void save(DcatView dcatView) {
		DataServiceEntity dataServiceEntity = dcatServiceEntityConverter.fromDcatView(dcatView);
		dataServiceEntityRepository.save(dataServiceEntity);
	}

	@Override
	public Optional<DcatView> findByViewName(ViewName viewName) {
		return dataServiceEntityRepository.findById(viewName.asString())
				.map(dcatServiceEntityConverter::toDcatView);
	}

	@Override
	public void delete(ViewName viewName) {
		dataServiceEntityRepository.deleteById(viewName.asString());
	}

	@Override
	public List<DcatView> findAll() {
		return dataServiceEntityRepository.findAll().stream().map(dcatServiceEntityConverter::toDcatView).toList();
	}

}
