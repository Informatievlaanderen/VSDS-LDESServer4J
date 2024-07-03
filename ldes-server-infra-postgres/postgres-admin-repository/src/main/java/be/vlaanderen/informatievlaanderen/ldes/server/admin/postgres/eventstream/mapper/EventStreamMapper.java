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

import java.util.List;

public class EventStreamMapper {
    private EventStreamMapper() {
    }

    public static EventStreamTO fromEntity(EventStreamEntity entity) {
        return new EventStreamTO(
                entity.getName(),
                entity.getTimestampPath(),
                entity.getVersionOfPath(),
                entity.isVersionCreationEnabled(),
                entity.isClosed(),
                entity.getViews().stream().map(ViewSpecificationMapper::fromEntity).toList(),
                entity.getShaclShapeEntity().getModel(),
                entity.getEventSourceEntity().getRetentionPolicies(),
                entity.getDcat().map(dcat -> new DcatDataset(entity.getName(), dcat)).orElseGet(() -> new DcatDataset(entity.getName()))
        );
    }

    public static EventStream fromPropertiesProjection(EventStreamProperties projection) {
        return new EventStream(projection.getName(), projection.getTimestampPath(), projection.getVersionOfPath(), projection.isVersionCreationEnabled(), projection.isClosed());
    }

    public static EventStreamEntity toEntity(EventStreamTO eventStream) {
        final EventStreamEntity entity = new EventStreamEntity(eventStream.getCollection(),
                eventStream.getTimestampPath(),
                eventStream.getVersionOfPath(),
                eventStream.isVersionCreationEnabled(),
                false);
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
