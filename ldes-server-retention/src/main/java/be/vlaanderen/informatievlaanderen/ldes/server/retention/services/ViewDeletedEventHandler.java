package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ingest.MemberIngestedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import org.springframework.context.event.EventListener;

public class ViewDeletedEventHandler {

    @EventListener
    public void handleViewDeletedEvent(ViewDeletedEvent event) {

    }
}
