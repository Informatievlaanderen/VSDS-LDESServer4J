package be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewSupplier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.function.BiConsumer;
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
        ViewName viewNameA = ViewName.fromString("col/viewA");
        ViewSpecification viewSpecificationA = new ViewSpecification(viewNameA, List.of(), List.of(), 10);
        ViewName viewNameB = ViewName.fromString("col/viewB");
        ViewSpecification viewSpecificationB = new ViewSpecification(viewNameB, List.of(), List.of(), 10);

        viewCollection.handle(new ViewAddedEvent(viewSpecificationA));
        viewCollection.handle(new ViewAddedEvent(viewSpecificationB));

        assertThat(viewCollection.getViews()).hasSize(2);

        viewCollection.handle(new ViewDeletedEvent(viewNameA));

        assertThat(viewCollection.getViews()).hasSize(1);
        assertThat(viewCollection.getViews()).contains(viewSpecificationB);
    }

}