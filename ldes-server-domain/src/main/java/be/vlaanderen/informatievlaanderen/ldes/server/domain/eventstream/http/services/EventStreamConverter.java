package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;

import java.util.List;

public class EventStreamConverter {
	public EventStream fromHttpMessage(EventStreamResponse eventStreamResponse) {
		return new EventStream(eventStreamResponse.getCollection(), eventStreamResponse.getTimestampPath(),
				eventStreamResponse.getVersionOfPath());
	}

	public EventStreamResponse toHttpMessage(EventStream eventStream, List<ViewSpecification> views,
			ShaclShape shaclShape) {
		return new EventStreamResponse(eventStream.getCollection(), eventStream.getTimestampPath(),
				eventStream.getVersionOfPath(), views, shaclShape.getModel());
	}
}
