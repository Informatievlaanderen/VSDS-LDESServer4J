package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ViewCollectionTest {

    private ViewCollection viewCollection;

    @BeforeEach
    void setUp() {
        viewCollection = new ViewCollection();
    }

    @ParameterizedTest
    @ArgumentsSource(ViewSupplierProvider.class)
    void testHandleViewAddedEvent(ViewSupplier viewSupplier) {
        viewCollection.handle(viewSupplier);

        assertThat(viewCollection.getViews()).contains(viewSupplier.getViewSpecification());
    }

    static class ViewSupplierProvider implements ArgumentsProvider {

        ViewName viewName = ViewName.fromString("col/view");
        ViewSpecification viewSpecification = new ViewSpecification(viewName, List.of(), List.of(), 10);

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new ViewInitializationEvent(viewSpecification)),
                    Arguments.of(new ViewAddedEvent(viewSpecification)));
        }

    }

    @Test
    void testHandleViewDeletedEvent() {
        final ViewName viewNameA = ViewName.fromString("col/viewA");
        final ViewSpecification viewSpecificationA = new ViewSpecification(viewNameA, List.of(), List.of(), 10);
        final ViewName viewNameB = ViewName.fromString("col/viewB");
        final ViewSpecification viewSpecificationB = new ViewSpecification(viewNameB, List.of(), List.of(), 10);

        viewCollection.handle(new ViewAddedEvent(viewSpecificationA));
        viewCollection.handle(new ViewAddedEvent(viewSpecificationB));

        assertThat(viewCollection.getViews()).hasSize(2);

        viewCollection.handle(new ViewDeletedEvent(viewNameA));

        assertThat(viewCollection.getViews()).hasSize(1);
        assertThat(viewCollection.getViews()).contains(viewSpecificationB);
    }

    @Test
    void testHandleEventStreamDeletedEvent() {
        final String collectionNameA = "colA";
        final ViewName viewNameAA = new ViewName(collectionNameA, "viewA");
        final ViewSpecification viewSpecificationA = new ViewSpecification(viewNameAA, List.of(), List.of(), 10);
        final ViewName viewNameAB = new ViewName(collectionNameA, "viewB");
        final ViewSpecification viewSpecificationB = new ViewSpecification(viewNameAB, List.of(), List.of(), 10);
        final ViewName viewNameBC = ViewName.fromString("colB/viewC");
        final ViewSpecification viewSpecificationC = new ViewSpecification(viewNameBC, List.of(), List.of(), 10);

        viewCollection.handle(new ViewAddedEvent(viewSpecificationA));
        viewCollection.handle(new ViewAddedEvent(viewSpecificationB));
        viewCollection.handle(new ViewAddedEvent(viewSpecificationC));

        assertThat(viewCollection.getViews()).hasSize(3);

        viewCollection.handle(new EventStreamDeletedEvent(collectionNameA));

        assertThat(viewCollection.getViews()).hasSize(1);
        assertThat(viewCollection.getViews()).contains(viewSpecificationC);
    }

}