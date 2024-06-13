package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class ViewCollection {

    private final Map<ViewName, ViewSpecification> views = new HashMap<>();

    @EventListener({ViewAddedEvent.class, ViewInitializationEvent.class})
    public void handle(ViewSupplier event) {
        views.put(event.viewSpecification().getName(), event.viewSpecification());
    }

    @EventListener
    public void handle(ViewDeletedEvent event) {
        views.remove(event.getViewName());
    }

    @EventListener
    public void handle(EventStreamDeletedEvent event) {
        views.keySet().stream()
                .filter(viewName -> viewName.getCollectionName().equals(event.collectionName()))
                .toList()
                .forEach(views::remove);

    }

    public Collection<ViewSpecification> getViews() {
        return views.values();
    }

}
