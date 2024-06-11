package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.entity.ShaclShapeEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.shaclshape.repository.ShaclShapeEntityRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShaclShapePostgresRepositoryTest {
    private static final String COLLECTION = "collection-name";
    @Mock
    private ShaclShapeEntityRepository shaclShapeEntityRepository;
    @Mock
    private EventStreamEntityRepository eventStreamEntityRepository;
    @InjectMocks
    private ShaclShapePostgresRepository repository;


    @Test
    void when_retrieveShacl_then_shaclIsReturned() {
        final ShaclShapeEntity shaclShapeEntity = initShaclShapeEntity(loadShaclModel());
        when(shaclShapeEntityRepository.findByCollectionName(COLLECTION)).thenReturn(Optional.of(shaclShapeEntity));
        final ShaclShape expectedShaclShape = new ShaclShape(COLLECTION, loadShaclModel());

        Optional<ShaclShape> shaclShape = repository.retrieveShaclShape(COLLECTION);

        assertThat(shaclShape).contains(expectedShaclShape);
    }

    @Test
    void when_retrieveNonExistingShacl_then_emptyOptionalIsReturned() {
        when(shaclShapeEntityRepository.findByCollectionName(COLLECTION)).thenReturn(Optional.empty());

        Optional<ShaclShape> shaclShape = repository.retrieveShaclShape(COLLECTION);

        verify(shaclShapeEntityRepository).findByCollectionName(COLLECTION);
        assertTrue(shaclShape.isEmpty());
    }

    @Test
    void when_retrieveAllShacls_then_listIsReturned() {
        final String otherCollectionName = "other-collection";
        final ShaclShapeEntity firstEntity = initShaclShapeEntity(loadShaclModel());
        final ShaclShapeEntity secondEntity = mock();
        when(secondEntity.getCollectionName()).thenReturn(otherCollectionName);
        when(shaclShapeEntityRepository.findAll())
                .thenReturn(List.of(firstEntity, secondEntity));
        final List<ShaclShape> expectedShapes = List.of(
                new ShaclShape(COLLECTION, loadShaclModel()),
                new ShaclShape(otherCollectionName, ModelFactory.createDefaultModel()));

        final List<ShaclShape> shaclShapes = repository.retrieveAllShaclShapes();

        assertThat(shaclShapes).containsExactlyInAnyOrderElementsOf(expectedShapes);
    }

    @Test
    void given_NewShaclShape_when_SaveShacl_then_SaveShaclShapeEntity() {
        when(shaclShapeEntityRepository.findByCollectionName(COLLECTION)).thenReturn(Optional.empty());
        final ShaclShape shaclShape = new ShaclShape(COLLECTION, loadShaclModel());
        when(eventStreamEntityRepository.findByName(COLLECTION)).thenReturn(Optional.of(mock()));

        repository.saveShaclShape(shaclShape);

        InOrder inOrder = inOrder(shaclShapeEntityRepository, eventStreamEntityRepository);
        inOrder.verify(shaclShapeEntityRepository).findByCollectionName(COLLECTION);
        inOrder.verify(eventStreamEntityRepository).findByName(COLLECTION);
        inOrder.verify(shaclShapeEntityRepository).save(any(ShaclShapeEntity.class));
    }

    @Test
    void given_ExistingShaclShape_when_SaveShacl_then_SaveShaclShapeEntity() {
        final ShaclShapeEntity entity = initShaclShapeEntity(loadShaclModel());
        when(shaclShapeEntityRepository.findByCollectionName(COLLECTION)).thenReturn(Optional.of(entity));
        final ShaclShape shaclShape = new ShaclShape(COLLECTION, loadShaclModel());

        repository.saveShaclShape(shaclShape);

        InOrder inOrder = inOrder(shaclShapeEntityRepository, eventStreamEntityRepository);
        inOrder.verify(shaclShapeEntityRepository).findByCollectionName(COLLECTION);
        inOrder.verify(shaclShapeEntityRepository).save(any(ShaclShapeEntity.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void when_ShaclDeleted_then_ReturnEmptyWithRetrieval() {
        repository.deleteShaclShape(COLLECTION);

        verify(shaclShapeEntityRepository).deleteByCollectionName(COLLECTION);
    }

    private ShaclShapeEntity initShaclShapeEntity(Model model) {
        final EventStreamEntity eventStreamEntity = new EventStreamEntity(COLLECTION, "", "", false, false);
        final ShaclShapeEntity shaclShapeEntity = new ShaclShapeEntity(eventStreamEntity);
        shaclShapeEntity.setModel(model);
        return shaclShapeEntity;
    }

    private Model loadShaclModel() {
        return RDFParser.source("shacl/shacl-shape.ttl").toModel();
    }

}