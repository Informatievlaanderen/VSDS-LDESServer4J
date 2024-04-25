package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice;


import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.entity.DataServiceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.repository.DataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.service.DcatServiceEntityConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

import java.util.List;
import java.util.Optional;

public class DcatDataServicePostgresRepository implements DcatViewRepository {

    private final DataServiceEntityRepository repository;
    private final DcatServiceEntityConverter converter = new DcatServiceEntityConverter();

	public DcatDataServicePostgresRepository(DataServiceEntityRepository repository) {
		this.repository = repository;
	}

	@Override
    public void save(DcatView dcatView) {
        DataServiceEntity dataServiceEntity = converter.fromDcatView(dcatView);
        repository.save(dataServiceEntity);
    }

    @Override
    public Optional<DcatView> findByViewName(ViewName viewName) {
        return repository.findById(viewName.asString())
                .map(converter::toDcatView);
    }

    @Override
    public void delete(ViewName viewName) {
        repository.deleteById(viewName.asString());
    }

    @Override
    public void deleteByCollectionName(String collectionName) {
        repository.deleteAllByViewNameStartingWith(collectionName + "/");
    }

    @Override
    public List<DcatView> findAll() {
        return repository.findAll().stream().map(converter::toDcatView).toList();
    }

}
