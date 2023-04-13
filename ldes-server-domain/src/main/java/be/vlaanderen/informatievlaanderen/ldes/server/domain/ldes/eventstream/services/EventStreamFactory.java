package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesSpecification;

public interface EventStreamFactory {
	EventStream getEventStream(LdesSpecification ldesSpecification);

}
