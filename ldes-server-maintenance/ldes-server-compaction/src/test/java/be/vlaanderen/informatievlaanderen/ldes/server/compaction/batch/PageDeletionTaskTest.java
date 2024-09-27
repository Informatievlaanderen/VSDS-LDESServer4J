package be.vlaanderen.informatievlaanderen.ldes.server.compaction.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.repositories.PageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PageDeletionTaskTest {
	@Mock
	private PageRepository pageRepository;
	@InjectMocks
	private PageDeletionTask pageDeletionTask;

	@Test
	void when_FragmentHasDeleteTimeEarlierThanCurrentTime_then_ItIsDeletedAndEventIsSent() {
		pageDeletionTask.execute(null, null);

		verify(pageRepository).deleteOutdatedFragments(any());
	}
}