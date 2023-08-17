package be.vlaanderen.informatievlaanderen.ldes.server.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import org.springframework.context.event.EventListener;

public class ViewAddedHandlerCompaction {

    @EventListener
    public void handleViewAddedEvent(ViewAddedEvent event) {
//        prepareFragmentationStrategyExecutor(event.getViewName(), event.getViewSpecification());
    }


}
