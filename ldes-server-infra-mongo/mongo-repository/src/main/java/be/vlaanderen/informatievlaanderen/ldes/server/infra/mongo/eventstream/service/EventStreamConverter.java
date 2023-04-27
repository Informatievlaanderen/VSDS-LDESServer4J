package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.eventstream.entity.EventStreamEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.RDFWriter;

public class EventStreamConverter {
	public EventStreamEntity fromEventStream(EventStream eventStream) {
		String viewsString = RDFWriter.source(eventStream.getViews()).lang(Lang.TURTLE).asString();
		return new EventStreamEntity(eventStream.getCollection(), eventStream.getTimestampPath(),
				eventStream.getVersionOfPath(), viewsString);
	}

	public EventStream toEventStream(EventStreamEntity eventStreamEntity) {
		Model views = RDFParserBuilder.create().fromString(eventStreamEntity.getViews()).lang(Lang.TURTLE).toModel();
		return new EventStream(eventStreamEntity.getId(), eventStreamEntity.getTimestampPath(),
				eventStreamEntity.getVersionOfPath(), views);
	}
}
