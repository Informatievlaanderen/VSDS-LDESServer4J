package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DcatViewDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.DcatViewSavedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DcatViewServiceImplTest {

	private final static String COLLECTION_NAME = "collectionName";
	private final static String VIEW = "view";
	private final static ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);
	private final static Model MODEL = ModelFactory.createDefaultModel();

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private DcatViewRepository dcatViewRepository;

	@InjectMocks
	private DcatViewServiceImpl dcatViewService;

	@Test
	void should_CallRepositoryWithDcatView_when_CreateIsCalled() {
		dcatViewService.create(VIEW_NAME, MODEL);

		verify(eventPublisher).publishEvent(any(DcatViewSavedEvent.class));
		verify(dcatViewRepository).save(DcatView.from(VIEW_NAME, MODEL));
		verifyNoMoreInteractions(dcatViewRepository);
	}

	@Test
	void should_GetDcatViewFromRepository_when_FindByViewIsCalled() {
		DcatView dcatView = DcatView.from(VIEW_NAME, MODEL);
		when(dcatViewRepository.findByViewName(VIEW_NAME)).thenReturn(Optional.of(dcatView));

		Optional<DcatView> result = dcatViewService.findByViewName(VIEW_NAME);

		assertThat(result).contains(dcatView);
	}

	@Test
	void should_CallRepositoryWithDcatView_when_DeleteIsCalled() {
		dcatViewService.delete(VIEW_NAME);

		verify(eventPublisher).publishEvent(new DcatViewDeletedEvent(VIEW_NAME));
		verify(dcatViewRepository).delete(VIEW_NAME);
		verifyNoMoreInteractions(dcatViewRepository);
	}

	@Test
	void initViews() {
		DcatView dcatViewA = DcatView.from(ViewName.fromString("coll/A"), MODEL);
		DcatView dcatViewB = DcatView.from(ViewName.fromString("coll/B"), MODEL);
		DcatView dcatViewC = DcatView.from(ViewName.fromString("coll/C"), MODEL);
		when(dcatViewRepository.findAll()).thenReturn(List.of(dcatViewA, dcatViewB, dcatViewC));

		dcatViewService.initViews();

		verify(eventPublisher).publishEvent(new DcatViewSavedEvent(dcatViewA));
		verify(eventPublisher).publishEvent(new DcatViewSavedEvent(dcatViewB));
		verify(eventPublisher).publishEvent(new DcatViewSavedEvent(dcatViewC));
	}

	@Nested
	class Update {

		@Test
		void should_CallRepositoryWithDcatView_when_EntityExists() {
			when(dcatViewRepository.findByViewName(VIEW_NAME)).thenReturn(Optional.of(DcatView.from(VIEW_NAME, MODEL)));

			dcatViewService.update(VIEW_NAME, MODEL);

			verify(eventPublisher).publishEvent(any(DcatViewSavedEvent.class));
			verify(dcatViewRepository).save(DcatView.from(VIEW_NAME, MODEL));
			verifyNoMoreInteractions(dcatViewRepository);
		}

		@Test
		void should_ThrowException_when_EntityDoesNotExist() {
			when(dcatViewRepository.findByViewName(VIEW_NAME)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> dcatViewService.update(VIEW_NAME, MODEL))
					.isInstanceOf(MissingResourceException.class)
					.hasMessage("Resource of type: dcat-data-service with id: %s could not be found.", VIEW_NAME.asString());
		}

	}

	@Test
	void should_CallRepositoryToFindAllViews_when_FindAllIsCalled() {
		dcatViewService.findAll();

		verify(dcatViewRepository).findAll();
		verifyNoMoreInteractions(dcatViewRepository);
	}

}