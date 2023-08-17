package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.MissingViewDcatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DcatViewServiceImplTest {

	private final static String COLLECTION_NAME = "collectionName";
	private final static String VIEW = "view";
	private final static ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);
	private final static Model MODEL = ModelFactory.createDefaultModel();

	@Mock
	private DcatViewRepository dcatViewRepository;

	@InjectMocks
	private DcatViewServiceImpl dcatViewService;

	@Test
	void should_CallRepositoryWithDcatView_when_CreateIsCalled() {
		dcatViewService.create(VIEW_NAME, MODEL);

		verify(dcatViewRepository).save(DcatView.from(VIEW_NAME, MODEL));
		verifyNoMoreInteractions(dcatViewRepository);
	}

	@Test
	void should_GetDcatViewFromRepository_when_FindByViewIsCalled() {
		DcatView dcatView = DcatView.from(VIEW_NAME, MODEL);
		when(dcatViewRepository.findByViewName(VIEW_NAME)).thenReturn(Optional.of(dcatView));

		Optional<DcatView> result = dcatViewService.findByViewName(VIEW_NAME);

		assertTrue(result.isPresent());
		assertEquals(dcatView, result.get());
	}

	@Test
	void should_CallRepositoryWithDcatView_when_DeleteIsCalled() {
		dcatViewService.delete(VIEW_NAME);

		verify(dcatViewRepository).delete(VIEW_NAME);
		verifyNoMoreInteractions(dcatViewRepository);
	}

	@Test
	void should_CallRepositoryWithDcatView_when_ViewDeletedEventIsPublished() {
		dcatViewService.handleEventStreamInitEvent(new ViewDeletedEvent(VIEW_NAME));

		verify(dcatViewRepository).delete(VIEW_NAME);
		verifyNoMoreInteractions(dcatViewRepository);
	}

	@Nested
	class Update {

		@Test
		void should_CallRepositoryWithDcatView_when_EntityExists() {
			when(dcatViewRepository.findByViewName(VIEW_NAME)).thenReturn(Optional.of(DcatView.from(VIEW_NAME, MODEL)));

			dcatViewService.update(VIEW_NAME, MODEL);

			verify(dcatViewRepository).save(DcatView.from(VIEW_NAME, MODEL));
			verifyNoMoreInteractions(dcatViewRepository);
		}

		@Test
		void should_ThrowException_when_EntityDoesNotExist() {
			when(dcatViewRepository.findByViewName(VIEW_NAME)).thenReturn(Optional.empty());

			assertThrows(MissingViewDcatException.class, () -> dcatViewService.update(VIEW_NAME, MODEL));
		}

	}

	@Test
	void should_CallRepositoryToFindAllViews_when_FindAllIsCalled() {
		dcatViewService.findAll();

		verify(dcatViewRepository).findAll();
		verifyNoMoreInteractions(dcatViewRepository);
	}

}