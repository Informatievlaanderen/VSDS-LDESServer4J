package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;

public class EventStreamConverter {
	public EventStream fromHttpMessage(EventStreamResponse eventStreamResponse) {
		return new EventStream(eventStreamResponse.getCollection(), eventStreamResponse.getTimestampPath(),
				eventStreamResponse.getVersionOfPath(), eventStreamResponse.getViews());
	}

	public EventStreamResponse toHttpMessage(EventStream eventStream, ShaclShape shaclShape) {
		return new EventStreamResponse(eventStream.getCollection(), eventStream.getTimestampPath(),
				eventStream.getVersionOfPath(), eventStream.getViews(), shaclShape.getModel());
	}
}
