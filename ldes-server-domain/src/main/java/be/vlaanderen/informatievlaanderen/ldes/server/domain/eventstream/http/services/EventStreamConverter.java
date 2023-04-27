package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamHttpMessage;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import org.apache.jena.rdf.model.Model;

public class EventStreamConverter {
	public EventStream fromHttpMessage(EventStreamHttpMessage eventStreamHttpMessage) {
		return new EventStream(eventStreamHttpMessage.getCollection(), eventStreamHttpMessage.getTimestampPath(), eventStreamHttpMessage.getVersionOfPath(), eventStreamHttpMessage.getViews());
	}

	public EventStreamHttpMessage toHttpMessage(EventStream eventStream, Model shaclShape) {
		return new EventStreamHttpMessage(eventStream.getCollection(), eventStream.getTimestampPath(), eventStream.getVersionOfPath(), eventStream.getViews(), shaclShape);
	}

	public EventStreamHttpMessage toHttpMessage(EventStream eventStream, ShaclShape shaclShape) {
		return toHttpMessage(eventStream, shaclShape.getModel());
	}
}
