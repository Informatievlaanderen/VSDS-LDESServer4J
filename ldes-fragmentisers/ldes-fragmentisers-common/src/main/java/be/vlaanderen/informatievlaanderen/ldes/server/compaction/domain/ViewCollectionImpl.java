package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ViewCollectionImpl implements ViewCollection {
    private final HashMap<ViewName, ViewCapacity> viewCapacities = new HashMap<>();

    @Override
    public void saveViewCapacity(ViewCapacity viewCapacity) {
        viewCapacities.put(viewCapacity.getViewName(), viewCapacity);
    }

    @Override
    public ViewCapacity getViewCapacityByViewName(ViewName viewName) {
        return viewCapacities.get(viewName);
    }

    @Override
    public void deleteViewCapacityByViewName(ViewName viewName) {
        viewCapacities.remove(viewName);
    }
}
