package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.DuplicateViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.MissingViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingEventStreamException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ViewServiceImplTest {

	private final DcatViewService dcatViewService = Mockito.mock(DcatViewService.class);
	private final ViewRepository viewRepository = mock(ViewRepository.class);
	private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
	private static final String COLLECTION = "collection";
	private static final String NOT_EXISTING_COLLECTION = "not_existing_collection";
	private final ViewServiceImpl viewService = new ViewServiceImpl(dcatViewService, viewRepository, eventPublisher);

	@BeforeEach
	void setUp() {
		viewService.handleEventStreamInitEvent(
				new EventStreamCreatedEvent(new EventStream(COLLECTION, null, null, null)));
	}

	@Nested
	class AddView {
		private final ViewSpecification view = new ViewSpecification(new ViewName(COLLECTION, "view"), List.of(),
				List.of(), 100);
		private final ViewSpecification viewOfNotExistingCollection = new ViewSpecification(
				new ViewName(NOT_EXISTING_COLLECTION, "view"), List.of(),
				List.of(), 100);

		@Test
		void when_ViewDoesNotExist_then_ViewIsAdded() {
			when(viewRepository.getViewByViewName(view.getName())).thenReturn(Optional.empty());

			viewService.addView(view);

			InOrder inOrder = inOrder(viewRepository, eventPublisher);
			inOrder.verify(viewRepository).getViewByViewName(view.getName());
			inOrder.verify(eventPublisher).publishEvent(any(ViewAddedEvent.class));
			inOrder.verify(viewRepository).saveView(view);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void when_ViewDoesExist_then_DuplicateViewExceptionIsThrown() {
			when(viewRepository.getViewByViewName(view.getName())).thenReturn(Optional.of(view));

			DuplicateViewException duplicateViewException = assertThrows(DuplicateViewException.class,
					() -> viewService.addView(view));

			assertEquals("Collection collection already has a view: view", duplicateViewException.getMessage());
			InOrder inOrder = inOrder(viewRepository, eventPublisher);
			inOrder.verify(viewRepository).getViewByViewName(view.getName());
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void when_EventStreamDoesNotExist_then_MissingEventStreamExceptionIsThrown() {
			MissingEventStreamException missingEventStreamException = assertThrows(MissingEventStreamException.class,
					() -> viewService.addView(viewOfNotExistingCollection));

			assertEquals("No event stream found for collection not_existing_collection",
					missingEventStreamException.getMessage());
			InOrder inOrder = inOrder(viewRepository, eventPublisher);
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Nested
	class DeleteView {
		private final ViewName viewName = new ViewName(COLLECTION, "view");
		private final ViewName viewNameOfNotExistingCollection = new ViewName(NOT_EXISTING_COLLECTION, "view");
		private final ViewName notExistingViewName = new ViewName(COLLECTION, "not_existing_view");

		@Test
		void when_DeleteViewAndEventStreamDoesNotExist_then_MissingEventStreamExceptionIsThrown() {
			MissingEventStreamException missingEventStreamException = assertThrows(MissingEventStreamException.class,
					() -> viewService.deleteViewByViewName(viewNameOfNotExistingCollection));

			assertEquals("No event stream found for collection not_existing_collection",
					missingEventStreamException.getMessage());
			InOrder inOrder = inOrder(viewRepository, eventPublisher);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void when_DeleteViewAndViewExists_then_ViewIsDeleted() {
			ViewSpecification viewSpecification = new ViewSpecification(viewName, List.of(), List.of(), 100);
			when(viewRepository.getViewByViewName(viewName)).thenReturn(Optional.of(viewSpecification));

			viewService.deleteViewByViewName(viewName);

			InOrder inOrder = inOrder(viewRepository, eventPublisher, dcatViewService);
			inOrder.verify(eventPublisher).publishEvent(any(ViewDeletedEvent.class));
			inOrder.verify(viewRepository).deleteViewByViewName(viewName);
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Nested
	class GetView {
		private final ViewName viewName = new ViewName(COLLECTION, "view");

		@Test
		void when_GetViewAndViewIsPresent_then_ViewIsReturned() {
			ViewSpecification expectedViewSpecification = new ViewSpecification(viewName, List.of(), List.of(),
					100);
			when(viewRepository.getViewByViewName(viewName)).thenReturn(Optional.of(expectedViewSpecification));

			ViewSpecification actualViewSpecification = viewService.getViewByViewName(viewName);

			assertEquals(expectedViewSpecification, actualViewSpecification);
			InOrder inOrder = inOrder(viewRepository, eventPublisher);
			inOrder.verify(viewRepository).getViewByViewName(viewName);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void when_GetViewAndViewIsNotPresent_then_MissingViewExceptionIsThrown() {
			when(viewRepository.getViewByViewName(viewName)).thenReturn(Optional.empty());

			MissingViewException missingViewException = assertThrows(MissingViewException.class,
					() -> viewService.getViewByViewName(viewName));

			assertEquals("Collection collection does not have a view: view", missingViewException.getMessage());
			InOrder inOrder = inOrder(viewRepository, eventPublisher);
			inOrder.verify(viewRepository).getViewByViewName(viewName);
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Nested
	class GetViewsOfCollection {
		private final ViewName viewName = new ViewName(COLLECTION, "view");
		private final ViewSpecification expectedViewSpecification = new ViewSpecification(viewName, List.of(),
				List.of(), 100);

		@Test
		void when_GetViewsByCollectionNameAndEventStreamDoesNotExist_then_MissingEventStreamExceptionIsThrown() {
			MissingEventStreamException missingEventStreamException = assertThrows(MissingEventStreamException.class,
					() -> viewService.getViewsByCollectionName(NOT_EXISTING_COLLECTION));

			assertEquals("No event stream found for collection not_existing_collection",
					missingEventStreamException.getMessage());
			InOrder inOrder = inOrder(viewRepository, eventPublisher);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void when_GetViewsByCollectionName_then_ViewsAreReturned() {
			when(viewRepository.retrieveAllViewsOfCollection(viewName.getCollectionName()))
					.thenReturn(List.of(expectedViewSpecification));

			List<ViewSpecification> actualViewSpecifications = viewService
					.getViewsByCollectionName(viewName.getCollectionName());

			assertEquals(List.of(expectedViewSpecification), actualViewSpecifications);
			InOrder inOrder = inOrder(viewRepository, eventPublisher);
			inOrder.verify(viewRepository).retrieveAllViewsOfCollection(viewName.getCollectionName());
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Nested
	class InitViews {
		@Test
		void when_ApplicationIsStartedUp_then_ViewAddedEventsAreSent() {
			ViewSpecification firstViewSpecification = new ViewSpecification(new ViewName(COLLECTION, "view"),
					List.of(), List.of(), 100);
			ViewSpecification secondViewSpecification = new ViewSpecification(new ViewName(COLLECTION, "view2"),
					List.of(), List.of(), 100);
			when(viewRepository.retrieveAllViews())
					.thenReturn(List.of(firstViewSpecification, secondViewSpecification));

			viewService.initViews();

			InOrder inOrder = inOrder(viewRepository, eventPublisher);
			inOrder.verify(viewRepository).retrieveAllViews();
			inOrder.verify(eventPublisher, times(2)).publishEvent(any(ViewInitializationEvent.class));
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Test
	void should_CallRepositoryWithDcatView_when_ViewDeletedEventIsPublished() {
		ViewName view = new ViewName(COLLECTION, "view");
		ViewSpecification firstViewSpecification = new ViewSpecification(view, List.of(), List.of(), 100);
		ViewName view2 = new ViewName(COLLECTION, "view2");
		ViewSpecification secondViewSpecification = new ViewSpecification(view2, List.of(), List.of(), 100);
		when(viewRepository.retrieveAllViewsOfCollection(COLLECTION))
				.thenReturn(List.of(firstViewSpecification, secondViewSpecification));

		viewService.handleEventStreamDeletedEvent(new EventStreamDeletedEvent(COLLECTION));

		verify(eventPublisher, times(2)).publishEvent(any(ViewDeletedEvent.class));
		verify(viewRepository).deleteViewByViewName(view);
		verify(viewRepository).deleteViewByViewName(view2);
		assertThrows(MissingEventStreamException.class, () -> viewService.getViewsByCollectionName(COLLECTION));
	}
}
