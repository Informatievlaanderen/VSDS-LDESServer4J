package be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.entity.EventStreamEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.eventstream.repository.EventStreamEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.entity.ViewEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.postgres.view.repository.ViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewPostgresRepositoryTest {

    public static final String VIEW_NAME = "view1";
    public static final String COLLECTION_NAME = "collection1";
    @Mock
    private ViewEntityRepository viewEntityRepository;
    @Mock
    private EventStreamEntityRepository eventStreamEntityRepository;
    @InjectMocks
    private ViewPostgresRepository repository;

    @Test
    void test_retrievingAllViews_AND_retrievingAllViewsOfCollection() {
        when(viewEntityRepository.findAll())
                .thenReturn(List.of(
                        new ViewEntity(VIEW_NAME, List.of(), List.of(), 100) {{
                            setEventStream(new EventStreamEntity(COLLECTION_NAME, null, null, null, false, null));
                        }},
                        new ViewEntity("view2", List.of(), List.of(), 100) {{
                            setEventStream(new EventStreamEntity(COLLECTION_NAME, null, null, null, false, null));
                        }},
                        new ViewEntity("view1", List.of(), List.of(), 100) {{
                            setEventStream(new EventStreamEntity("collection2", null, null, null, false, null));
                        }}));
        final List<ViewSpecification> expectedViews = List.of(
                new ViewSpecification(new ViewName(COLLECTION_NAME, VIEW_NAME), List.of(), List.of(), 100),
                new ViewSpecification(new ViewName(COLLECTION_NAME, "view2"), List.of(), List.of(), 100),
                new ViewSpecification(new ViewName("collection2", VIEW_NAME), List.of(), List.of(), 100));

        List<ViewSpecification> viewSpecifications = repository.retrieveAllViews();

        assertThat(viewSpecifications).containsExactlyInAnyOrderElementsOf(expectedViews);
    }

    @Test
    void test_RetrieveViewsOfCollection() {
        when(viewEntityRepository.findAllByCollectionName(COLLECTION_NAME))
                .thenReturn(List.of(
                        new ViewEntity(VIEW_NAME, List.of(), List.of(), 100) {{
                            setEventStream(new EventStreamEntity(COLLECTION_NAME, null, null, null, false, null));
                        }},
                        new ViewEntity("view2", List.of(), List.of(), 100) {{
                            setEventStream(new EventStreamEntity(COLLECTION_NAME, null, null, null, false, null));
                        }}));
        final List<ViewSpecification> expectedViews = List.of(
                new ViewSpecification(new ViewName(COLLECTION_NAME, VIEW_NAME), List.of(), List.of(), 100),
                new ViewSpecification(new ViewName(COLLECTION_NAME, "view2"), List.of(), List.of(), 100));

        final List<ViewSpecification> actualViews = repository.retrieveAllViewsOfCollection(COLLECTION_NAME);

        assertThat(actualViews).containsExactlyInAnyOrderElementsOf(expectedViews);
    }

    @Test
    void given_EventStreamExists_test_savingOfView() {
        when(eventStreamEntityRepository.findByName(COLLECTION_NAME)).thenReturn(Optional.of(mock()));
        final ViewSpecification view = new ViewSpecification(new ViewName(COLLECTION_NAME, VIEW_NAME), List.of(),
                List.of(), 100);

        repository.saveView(view);

        verify(viewEntityRepository).save(any(ViewEntity.class));
    }

    @Test
    void given_EventStreamDoesNotExist_test_savingOfView() {
        when(eventStreamEntityRepository.findByName(COLLECTION_NAME)).thenReturn(Optional.empty());
        final ViewSpecification view = new ViewSpecification(new ViewName(COLLECTION_NAME, VIEW_NAME), List.of(),
                List.of(), 100);

        assertThatThrownBy(() -> repository.saveView(view))
                .isInstanceOf(MissingResourceException.class)
                .hasMessage("Resource of type: EventStream with id: %s could not be found.", COLLECTION_NAME);

        verifyNoInteractions(viewEntityRepository);
    }

    @Test
    void test_deletingOfView() {
        final ViewName viewName = new ViewName(COLLECTION_NAME, VIEW_NAME);

        repository.deleteViewByViewName(viewName);

        verify(viewEntityRepository).deleteByViewName(viewName.getCollectionName(), viewName.getViewName());
    }

    @Test
    void test_getViewByViewName() {
        ViewEntity viewEntity = new ViewEntity(VIEW_NAME, List.of(), List.of(), 100);
        viewEntity.setEventStream(new EventStreamEntity(COLLECTION_NAME, null, null, null, false, null));

        ViewSpecification expectedViewSpecification = new ViewSpecification(new ViewName(COLLECTION_NAME, VIEW_NAME),
                List.of(), List.of(), 100);
        when(viewEntityRepository.findByViewName(expectedViewSpecification.getName().getCollectionName(), expectedViewSpecification.getName().getViewName()))
                .thenReturn(Optional.of(viewEntity));

        Optional<ViewSpecification> actualViewSpecification = repository
                .getViewByViewName(expectedViewSpecification.getName());

        assertThat(actualViewSpecification)
                .contains(expectedViewSpecification);
    }

}
