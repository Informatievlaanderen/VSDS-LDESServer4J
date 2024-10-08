package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view;


import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.mapper.ViewSpecificationMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class ViewPostgresRepository implements ViewRepository {
    private final ViewEntityRepository viewEntityRepository;
    private final EventStreamEntityRepository eventStreamEntityRepository;

    public ViewPostgresRepository(ViewEntityRepository viewEntityRepository, EventStreamEntityRepository eventStreamEntityRepository) {
        this.viewEntityRepository = viewEntityRepository;
        this.eventStreamEntityRepository = eventStreamEntityRepository;
    }

    @Override
    public List<ViewSpecification> retrieveAllViews() {
        return viewEntityRepository.findAll().stream()
                .map(ViewSpecificationMapper::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public void saveView(ViewSpecification viewSpecification) {
        final ViewEntity viewEntity = ViewSpecificationMapper.toEntity(viewSpecification);
        final EventStreamEntity eventStreamEntity = eventStreamEntityRepository
                .findByName(viewSpecification.getName().getCollectionName())
                .orElseThrow(() -> new MissingResourceException("EventStream", viewSpecification.getName().getCollectionName()));
        viewEntity.setEventStream(eventStreamEntity);
        viewEntityRepository.save(viewEntity);
    }

    @Override
    @Transactional
    public void deleteViewByViewName(ViewName viewName) {
        viewEntityRepository.deleteByViewName(viewName.getCollectionName(), viewName.getViewName());
    }

    @Override
    public Optional<ViewSpecification> getViewByViewName(ViewName viewName) {
        return viewEntityRepository.findByViewName(viewName.getCollectionName(), viewName.getViewName()).map(ViewSpecificationMapper::fromEntity);
    }

    @Override
    public List<ViewSpecification> retrieveAllViewsOfCollection(String collectionName) {
        return viewEntityRepository.findAllByCollectionName(collectionName).stream()
                .map(ViewSpecificationMapper::fromEntity)
                .toList();
    }
}
