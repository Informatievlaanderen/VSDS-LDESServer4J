package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.Collection;

public interface ViewCollection {
    void saveViewCapacity(ViewCapacity viewCapacity) ;

    ViewCapacity getViewCapacityByViewName(ViewName viewName);

    void deleteViewCapacityByViewName(ViewName viewName) ;

    Collection<ViewCapacity> getAllViewCapacities();
}
