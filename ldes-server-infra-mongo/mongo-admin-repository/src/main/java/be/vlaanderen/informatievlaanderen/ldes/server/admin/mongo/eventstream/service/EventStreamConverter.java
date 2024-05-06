package be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.mongo.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RetentionModelSerializer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventStreamConverter {
	private final RetentionModelSerializer retentionModelSerializer;

    public EventStreamConverter(RetentionModelSerializer retentionModelSerializer) {
        this.retentionModelSerializer = retentionModelSerializer;
    }

    public EventStreamEntity fromEventStream(EventStream eventStream) {
		List<String> serializedRetentionModels = retentionModelSerializer.serialize(eventStream.getEventSourceRetentionPolicies());
		return new EventStreamEntity(eventStream.getCollection(), eventStream.getTimestampPath(),
				eventStream.getVersionOfPath(), eventStream.isVersionCreationEnabled(), serializedRetentionModels);
	}

	public EventStream toEventStream(EventStreamEntity eventStreamEntity) {
		List<Model> retentionModels = retentionModelSerializer.deSerialize(eventStreamEntity.getRetentionPolicies());
		return new EventStream(eventStreamEntity.getId(), eventStreamEntity.getTimestampPath(),
				eventStreamEntity.getVersionOfPath(), eventStreamEntity.isVersionCreationEnabled(), retentionModels);
	}
}
