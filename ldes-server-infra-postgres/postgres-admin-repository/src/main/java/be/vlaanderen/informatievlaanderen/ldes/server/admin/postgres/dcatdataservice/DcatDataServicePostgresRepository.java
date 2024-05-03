package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice;


import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.repository.DataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.service.DcatServiceEntityConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class DcatDataServicePostgresRepository implements DcatViewRepository {

    private final DataServiceEntityRepository repository;
    private final EntityManager entityManager;
    private final DcatServiceEntityConverter converter = new DcatServiceEntityConverter();

	public DcatDataServicePostgresRepository(DataServiceEntityRepository repository, EntityManager entityManager) {
		this.repository = repository;
		this.entityManager = entityManager;
	}

    @Override
	@Transactional
    public void save(DcatView dcatView) {
        repository.save(converter.fromDcatView(dcatView));
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
