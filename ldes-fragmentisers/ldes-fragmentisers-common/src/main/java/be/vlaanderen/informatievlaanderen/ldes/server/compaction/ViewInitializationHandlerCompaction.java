package be.vlaanderen.informatievlaanderen.ldes.server.compaction;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import org.apache.jena.ext.com.google.common.collect.Iterables;
import org.springframework.context.event.EventListener;

public class ViewInitializationHandlerCompaction {
    public static final String MEMBER_LIMIT = "memberLimit";
    @EventListener
    public void handleViewInitializationEvent(ViewInitializationEvent event) {
        event.getViewName();
    Iterables.getLast(event.getViewSpecification().getFragmentations()).getProperties().get(MEMBER_LIMIT);
//        prepareFragmentationStrategyExecutor(event.getViewName(), event.getViewSpecification());
    }
}
