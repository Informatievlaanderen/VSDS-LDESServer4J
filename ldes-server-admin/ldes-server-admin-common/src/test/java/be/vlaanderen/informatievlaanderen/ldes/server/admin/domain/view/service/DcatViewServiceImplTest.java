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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class DcatViewServiceImplTest {

	private static final String COLLECTION_NAME = "collectionName";
	private static final String VIEW = "view";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);
	private static final Model MODEL = ModelFactory.createDefaultModel();

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private DcatViewRepository dcatViewRepository;

	@InjectMocks
	private DcatViewServiceImpl dcatViewService;

	@Test
	void should_CallRepositoryWithDcatView_when_CreateIsCalled() {
		dcatViewService.create(VIEW_NAME, MODEL);

		Mockito.verify(eventPublisher).publishEvent(ArgumentMatchers.any(DcatViewSavedEvent.class));
		Mockito.verify(dcatViewRepository).save(DcatView.from(VIEW_NAME, MODEL));
		Mockito.verifyNoMoreInteractions(dcatViewRepository);
	}

	@Test
	void should_GetDcatViewFromRepository_when_FindByViewIsCalled() {
		DcatView dcatView = DcatView.from(VIEW_NAME, MODEL);
		Mockito.when(dcatViewRepository.findByViewName(VIEW_NAME)).thenReturn(Optional.of(dcatView));

		Optional<DcatView> result = dcatViewService.findByViewName(VIEW_NAME);

		assertThat(result).contains(dcatView);
	}

	@Test
	void should_CallRepositoryWithDcatView_when_DeleteIsCalled() {
		dcatViewService.delete(VIEW_NAME);

		Mockito.verify(eventPublisher).publishEvent(new DcatViewDeletedEvent(VIEW_NAME));
		Mockito.verify(dcatViewRepository).delete(VIEW_NAME);
		Mockito.verifyNoMoreInteractions(dcatViewRepository);
	}

	@Test
	void initViews() {
		DcatView dcatViewA = DcatView.from(ViewName.fromString("coll/A"), MODEL);
		DcatView dcatViewB = DcatView.from(ViewName.fromString("coll/B"), MODEL);
		DcatView dcatViewC = DcatView.from(ViewName.fromString("coll/C"), MODEL);
		Mockito.when(dcatViewRepository.findAll()).thenReturn(List.of(dcatViewA, dcatViewB, dcatViewC));

		dcatViewService.initViews();

		Mockito.verify(eventPublisher).publishEvent(new DcatViewSavedEvent(dcatViewA));
		Mockito.verify(eventPublisher).publishEvent(new DcatViewSavedEvent(dcatViewB));
		Mockito.verify(eventPublisher).publishEvent(new DcatViewSavedEvent(dcatViewC));
	}

	@Nested
	class Update {

		@Test
		void should_CallRepositoryWithDcatView_when_EntityExists() {
			Mockito.when(dcatViewRepository.findByViewName(VIEW_NAME)).thenReturn(Optional.of(DcatView.from(VIEW_NAME, MODEL)));

			dcatViewService.update(VIEW_NAME, MODEL);

			Mockito.verify(eventPublisher).publishEvent(ArgumentMatchers.any(DcatViewSavedEvent.class));
			Mockito.verify(dcatViewRepository).save(DcatView.from(VIEW_NAME, MODEL));
			Mockito.verifyNoMoreInteractions(dcatViewRepository);
		}

		@Test
		void should_ThrowException_when_EntityDoesNotExist() {
			Mockito.when(dcatViewRepository.findByViewName(VIEW_NAME)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> dcatViewService.update(VIEW_NAME, MODEL))
					.isInstanceOf(MissingResourceException.class)
					.hasMessage("Resource of type: dcat-data-service with id: %s could not be found.", VIEW_NAME.asString());
		}

	}

	@Test
	void should_CallRepositoryToFindAllViews_when_FindAllIsCalled() {
		dcatViewService.findAll();

		Mockito.verify(dcatViewRepository).findAll();
		Mockito.verifyNoMoreInteractions(dcatViewRepository);
	}

}