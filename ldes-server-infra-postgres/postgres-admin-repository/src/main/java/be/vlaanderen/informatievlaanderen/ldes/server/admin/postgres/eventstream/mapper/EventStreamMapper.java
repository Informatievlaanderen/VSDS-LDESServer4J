package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventsource.entity.EventSourceEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.projection.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.mapper.ViewSpecificationMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.VersionCreationProperties;

import java.util.List;

public class EventStreamMapper {
    private EventStreamMapper() {
    }

    public static EventStreamTO fromEntity(EventStreamEntity entity) {
        return new EventStreamTO.Builder()
                .withCollection(entity.getName())
                .withTimestampPath(entity.getTimestampPath())
                .withVersionOfPath(entity.getVersionOfPath())
                .withVersionDelimiter(entity.getVersionDelimiter())
                .withClosed(entity.isClosed())
                .withSkolemizationDomain(entity.getSkolemizationDomain())
                .withViews(entity.getViews().stream().map(ViewSpecificationMapper::fromEntity).toList())
                .withShacl(entity.getShaclShapeEntity().getModel())
                .withEventSourceRetentionPolicies(entity.getEventSourceEntity().getRetentionPolicies())
                .withDcatDataset(entity.getDcat().map(dcat -> new DcatDataset(entity.getName(), dcat)).orElseGet(() -> new DcatDataset(entity.getName())))
                .build();
    }

    public static EventStream fromPropertiesProjection(EventStreamProperties projection) {
        return new EventStream(
                projection.getName(),
                projection.getTimestampPath(),
                projection.getVersionOfPath(),
                VersionCreationProperties.ofNullableDelimeter(projection.getVersionDelimiter()),
                projection.isClosed(),
                projection.getSkolemizationDomain()
        );
    }

    public static EventStreamEntity toEntity(EventStreamTO eventStream) {
        final EventStreamEntity entity = new EventStreamEntity(eventStream.getCollection(),
                eventStream.getTimestampPath(),
                eventStream.getVersionOfPath(),
                eventStream.getVersionDelimiter(),
                eventStream.isClosed(),
                eventStream.getSkolemizationDomain());
        final List<ViewEntity> views = eventStream.getViews().stream()
                .map(viewSpec -> {
                    final var viewEntity = ViewSpecificationMapper.toEntity(viewSpec);
                    viewEntity.setEventStream(entity);
                    return viewEntity;
                })
                .toList();
        entity.setViews(views);
        entity.setEventSourceEntity(new EventSourceEntity(entity, eventStream.getEventSourceRetentionPolicies()));
        entity.setShaclShapeEntity(new ShaclShapeEntity(entity, eventStream.getShacl()));
        return entity;
    }
}
