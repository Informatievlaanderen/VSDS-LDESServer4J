package be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingShaclShapeException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.repository.ShaclShapeRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShaclShapeServiceImplTest {
	private static final String COLLECTION_NAME_1 = "collectionName1";
	private ShaclShapeServiceImpl service;
	@Mock
	private ShaclShapeRepository shaclShapeRepository;
	@Mock
	private ApplicationEventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		service = new ShaclShapeServiceImpl(shaclShapeRepository, eventPublisher);
	}

	@Nested
	class RetrieveShaclShape {
		@Test
		void when_collectionExists_and_hasShapeConfigured_then_retrieveShape() throws URISyntaxException {
			final Model shape = readModelFromFile("eventstream/streams/example-shape.ttl");
			when(shaclShapeRepository.retrieveShaclShape(COLLECTION_NAME_1))
					.thenReturn(Optional.of(new ShaclShape(COLLECTION_NAME_1, shape)));

			assertEquals(new ShaclShape(COLLECTION_NAME_1, shape), service.retrieveShaclShape(COLLECTION_NAME_1));

			InOrder inOrder = inOrder(shaclShapeRepository, eventPublisher);
			inOrder.verify(shaclShapeRepository).retrieveShaclShape(COLLECTION_NAME_1);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
        void when_collectionExists_and_hasNoShapeConfigured_then_retrieveRelatedShape() {
            when(shaclShapeRepository.retrieveShaclShape(COLLECTION_NAME_1)).thenReturn(Optional.of(new ShaclShape(COLLECTION_NAME_1, null)));

            assertEquals(new ShaclShape(COLLECTION_NAME_1, null), service.retrieveShaclShape(COLLECTION_NAME_1));
            InOrder inOrder = inOrder(shaclShapeRepository, eventPublisher);
            inOrder.verify(shaclShapeRepository).retrieveShaclShape(COLLECTION_NAME_1);
            inOrder.verifyNoMoreInteractions();
        }

		@Test
        void when_collectionDoesNotExists_then_throwException() {
            when(shaclShapeRepository.retrieveShaclShape(COLLECTION_NAME_1)).thenReturn(Optional.empty());

            Exception e = assertThrows(MissingShaclShapeException.class, () -> service.retrieveShaclShape(COLLECTION_NAME_1));

            assertEquals("No shacl shape configured for collection " + COLLECTION_NAME_1, e.getMessage());
            InOrder inOrder = inOrder(shaclShapeRepository, eventPublisher);
            inOrder.verify(shaclShapeRepository).retrieveShaclShape(COLLECTION_NAME_1);
            inOrder.verifyNoMoreInteractions();
        }
	}

	@Nested
	class UpdateShaclShape {
		@Test
		void when_collectionExists_and_updateShape_then_expectUpdatedShacl() throws URISyntaxException {
			final Model newShape = readModelFromFile("eventstream/streams/example-shape.ttl");
			final ShaclShape shaclShape = new ShaclShape(COLLECTION_NAME_1, newShape);

			ShaclShape updateShaclShape = service.updateShaclShape(shaclShape);

			assertEquals(shaclShape, updateShaclShape);
			InOrder inOrder = inOrder(shaclShapeRepository, eventPublisher);
			inOrder.verify(shaclShapeRepository).saveShaclShape(shaclShape);
			inOrder.verify(eventPublisher).publishEvent(any(ShaclChangedEvent.class));
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Nested
	class DeleteShaclShape {
		@Test
		void when_collectionExists_and_deleteShape_then_throwExceptionWithRetrieval() {
			service.deleteShaclShape(COLLECTION_NAME_1);

			InOrder inOrder = inOrder(shaclShapeRepository, eventPublisher);
			inOrder.verify(shaclShapeRepository).deleteShaclShape(COLLECTION_NAME_1);
			inOrder.verify(eventPublisher).publishEvent(any(ShaclDeletedEvent.class));
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Nested
	class InitShapes {
		@Test
		void when_ApplicationIsStarted_ShaclChangedEventsAreSentOut() {
			Model model = ModelFactory.createDefaultModel();
			ShaclShape shaclShape = new ShaclShape(COLLECTION_NAME_1, model);
			ShaclShape shaclShape2 = new ShaclShape("otherCollection", model);
			when(shaclShapeRepository.retrieveAllShaclShapes()).thenReturn(List.of(shaclShape, shaclShape2));

			service.initShapes();

			InOrder inOrder = inOrder(shaclShapeRepository, eventPublisher);
			inOrder.verify(shaclShapeRepository).retrieveAllShaclShapes();
			inOrder.verify(eventPublisher, times(2)).publishEvent((ShaclChangedEvent) any());
			inOrder.verifyNoMoreInteractions();
		}
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}
}
