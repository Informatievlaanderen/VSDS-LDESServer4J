package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamServiceSpi;

public interface EventStreamService extends EventStreamServiceSpi {

    void deleteEventStream(String collectionName);

    EventStreamResponse createEventStream(EventStreamResponse eventStream);

}
