package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.ViewCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.DuplicateViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ViewServiceImplTest {

	private final ViewCollection viewCollection = mock(ViewCollection.class);

	private final ViewService viewService = new ViewServiceImpl(viewCollection);

	@Nested
	class AddView {
		private final ViewSpecification view = new ViewSpecification(new ViewName("collection", "view"), List.of(),
				List.of());

		@Test
        void when_ViewDoesNotExist_ViewIsAdded() {
            when(viewCollection.getViewByViewName(view.getName())).thenReturn(Optional.empty());

            viewService.addView(view);

            InOrder inOrder = inOrder(viewCollection);
            inOrder.verify(viewCollection).getViewByViewName(view.getName());
            inOrder.verify(viewCollection).addView(view);
            inOrder.verifyNoMoreInteractions();
        }

		@Test
        void when_ViewDoesExist_DuplicateViewExceptionIsThrown() {
            when(viewCollection.getViewByViewName(view.getName())).thenReturn(Optional.of(view));

            DuplicateViewException duplicateViewException = assertThrows(DuplicateViewException.class, () -> viewService.addView(view));

            assertEquals("Collection collection already has a view: collection/view", duplicateViewException.getMessage());
            verify(viewCollection).getViewByViewName(view.getName());
            verifyNoMoreInteractions(viewCollection);
        }
	}
}