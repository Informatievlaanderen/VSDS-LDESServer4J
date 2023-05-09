package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.DuplicateViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.MissingViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.ViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewInitializationEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViewServiceImplTest {

	private final ViewRepository viewRepository = mock(ViewRepository.class);
	private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
	private final ViewServiceImpl viewService = new ViewServiceImpl(viewRepository, eventPublisher);

	@Nested
	class AddView {
		private final ViewSpecification view = new ViewSpecification(new ViewName("collection", "view"), List.of(),
				List.of());

		@Test
        void when_ViewDoesNotExist_ViewIsAdded() {
            when(viewRepository.getViewByViewName(view.getName())).thenReturn(Optional.empty());

            viewService.addView(view);

            InOrder inOrder = inOrder(viewRepository, eventPublisher);
            inOrder.verify(viewRepository).getViewByViewName(view.getName());
            inOrder.verify(viewRepository).saveView(view);
            inOrder.verify(eventPublisher).publishEvent(any(ViewAddedEvent.class));
            inOrder.verifyNoMoreInteractions();
        }

		@Test
        void when_ViewDoesExist_DuplicateViewExceptionIsThrown() {
            when(viewRepository.getViewByViewName(view.getName())).thenReturn(Optional.of(view));

            DuplicateViewException duplicateViewException = assertThrows(DuplicateViewException.class, () -> viewService.addView(view));

            assertEquals("Collection collection already has a view: view", duplicateViewException.getMessage());
            InOrder inOrder = inOrder(viewRepository, eventPublisher);
            inOrder.verify(viewRepository).getViewByViewName(view.getName());
            inOrder.verifyNoMoreInteractions();
        }
	}

	@Nested
	class DeleteView {
		private final ViewName viewName = new ViewName("collection", "view");

		@Test
		void when_ViewDoesNotExist_ViewIsAdded() {
			viewService.deleteViewByViewName(viewName);

			InOrder inOrder = inOrder(viewRepository, eventPublisher);
			inOrder.verify(viewRepository).deleteViewByViewName(viewName);
			inOrder.verify(eventPublisher).publishEvent(any(ViewDeletedEvent.class));
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Nested
	class GetView {
		private final ViewName viewName = new ViewName("collection", "view");

		@Test
		void when_GetViewAndViewIsPresent_ViewIsReturned() {
			ViewSpecification expectedViewSpecification = new ViewSpecification(viewName, List.of(), List.of());
			when(viewRepository.getViewByViewName(viewName)).thenReturn(Optional.of(expectedViewSpecification));

			ViewSpecification actualViewSpecification = viewService.getViewByViewName(viewName);

			assertEquals(expectedViewSpecification, actualViewSpecification);
			InOrder inOrder = inOrder(viewRepository);
			inOrder.verify(viewRepository).getViewByViewName(viewName);
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void when_GetViewAndViewIsNotPresent_ViewIsReturned() {
			when(viewRepository.getViewByViewName(viewName)).thenReturn(Optional.empty());

			MissingViewException missingViewException = assertThrows(MissingViewException.class, () -> viewService.getViewByViewName(viewName));

			assertEquals("Collection collection does not have a view: view", missingViewException.getMessage());
			InOrder inOrder = inOrder(viewRepository);
			inOrder.verify(viewRepository).getViewByViewName(viewName);
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Nested
	class InitViews {
		@Test
		void when_ApplicationIsStartedUp_ViewAddedEventsAreSent() {
			ViewSpecification firstViewSpecification = new ViewSpecification(new ViewName("collection", "view"),
					List.of(), List.of());
			ViewSpecification secondViewSpecification = new ViewSpecification(new ViewName("collection", "view2"),
					List.of(), List.of());
			when(viewRepository.retrieveAllViews())
					.thenReturn(List.of(firstViewSpecification, secondViewSpecification));

			viewService.initViews();

			InOrder inOrder = inOrder(viewRepository, eventPublisher);
			inOrder.verify(viewRepository).retrieveAllViews();
			inOrder.verify(eventPublisher, times(2)).publishEvent(any(ViewInitializationEvent.class));
			inOrder.verifyNoMoreInteractions();
		}
	}
}
