package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.entities.ViewCapacity;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ViewCollectionImplTest {
    private static final String COLLECTION = "collection";
    private ViewCollection viewCollection;

    @BeforeEach
    void setUp() {
        viewCollection = new ViewCollectionImpl();
    }

    @Test
    void test_deleteByViewName() {
        final String viewNameToDelete = "view1";
        final String viewNameToKeep = "view2";
        Stream.of(viewNameToDelete, viewNameToKeep)
                .map(viewName -> new ViewName(COLLECTION, viewName))
                .map(viewName -> new ViewCapacity(viewName, 100))
                .forEach(viewCollection::saveViewCapacity);

        viewCollection.deleteViewCapacityByViewName(new ViewName(COLLECTION, viewNameToDelete));

        assertThat(viewCollection.getAllViewCapacities())
                .map(ViewCapacity::getViewName)
                .containsExactlyInAnyOrder(new ViewName(COLLECTION, viewNameToKeep));
    }
    @Test
    void test_deleteByCollectionName() {
        final ViewName viewNameToKeep = new ViewName("collection-to-keep", "view1");
        Stream.of("view1", "view2", "view3")
                .map(viewName -> new ViewName(COLLECTION, viewName))
                .map(viewName -> new ViewCapacity(viewName, 100))
                .forEach(viewCollection::saveViewCapacity);
        viewCollection.saveViewCapacity(new ViewCapacity(viewNameToKeep, 100));

        viewCollection.deleteViewCapacitiesByCollectionName(COLLECTION);

        assertThat(viewCollection.getAllViewCapacities())
                .map(ViewCapacity::getViewName)
                .containsExactlyInAnyOrder(viewNameToKeep);
    }
}