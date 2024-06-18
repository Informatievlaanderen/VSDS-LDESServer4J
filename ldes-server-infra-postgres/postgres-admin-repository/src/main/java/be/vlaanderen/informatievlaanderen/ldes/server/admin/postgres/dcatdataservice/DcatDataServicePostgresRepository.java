package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice;


import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.entity.DcatDataServiceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.mapper.DcatViewMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.dcatdataservice.repository.DcatDataServiceEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DcatDataServicePostgresRepository implements DcatViewRepository {

    private final DcatDataServiceEntityRepository dcatDataServiceEntityRepository;
    private final ViewEntityRepository viewEntityRepository;

	public DcatDataServicePostgresRepository(DcatDataServiceEntityRepository dcatDataServiceEntityRepository, ViewEntityRepository viewEntityRepository) {
		this.dcatDataServiceEntityRepository = dcatDataServiceEntityRepository;
        this.viewEntityRepository = viewEntityRepository;
    }

    @Override
	@Transactional
    public void save(DcatView dcatView) {
        final String collectionName = dcatView.getViewName().getCollectionName();
        final String viewName = dcatView.getViewName().getViewName();
        dcatDataServiceEntityRepository.findByViewName(collectionName, viewName)
                .or(() -> viewEntityRepository.findByViewName(collectionName, viewName).map(DcatDataServiceEntity::new))
                .ifPresent(dcatDataServiceEntity -> {
                    dcatDataServiceEntity.setModel(dcatView.getDcat());
                    dcatDataServiceEntityRepository.save(dcatDataServiceEntity);
                });
    }

    @Override
    public Optional<DcatView> findByViewName(ViewName viewName) {
        return dcatDataServiceEntityRepository
                .findByViewName(viewName.getCollectionName(), viewName.getViewName())
                .map(DcatViewMapper::fromEntity);
    }

    @Override
    @Transactional
    public void delete(ViewName viewName) {
        dcatDataServiceEntityRepository.deleteByViewName(viewName.getCollectionName(), viewName.getViewName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DcatView> findAll() {
        return dcatDataServiceEntityRepository.findAll().stream().map(DcatViewMapper::fromEntity).toList();
    }

}
