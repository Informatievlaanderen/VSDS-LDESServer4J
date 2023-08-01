package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.valueobject.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.FragmentSequenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FragmentationSequenceListenerTest {

	@Mock
	private FragmentSequenceRepository fragmentSequenceRepository;

	@InjectMocks
	private FragmentationSequenceListener fragmentationSequenceListener;

	@Test
	void should_DeleteSequence_when_ViewIsDeleted() {
		ViewName viewName = ViewName.fromString("col/view");
		ViewDeletedEvent event = new ViewDeletedEvent(viewName);

		fragmentationSequenceListener.handleViewDeleted(event);

		verify(fragmentSequenceRepository).deleteByViewName(viewName);
	}

}