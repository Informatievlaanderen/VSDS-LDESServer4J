package be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.repository.DcatViewRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class DcatViewServiceImplTest {

	private final static String COLLECTION_NAME = "collectionName";
	private final static String VIEW = "view";
	private final static ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);

	@Mock
	private DcatViewRepository dcatViewRepository;

	@InjectMocks
	private DcatViewServiceImpl dcatViewService;

	@Test
	void should_CallServiceWithDcatView_when_CreateIsCalled() {
		Model defaultModel = ModelFactory.createDefaultModel();

		dcatViewService.create(VIEW_NAME, defaultModel);

		verify(dcatViewRepository).save(DcatView.from(VIEW_NAME, defaultModel));
		verifyNoMoreInteractions(dcatViewRepository);
	}

	@Test
	void should_CallServiceWithDcatView_when_DeleteIsCalled() {
		dcatViewService.delete(VIEW_NAME);

		verify(dcatViewRepository).delete(VIEW_NAME);
		verifyNoMoreInteractions(dcatViewRepository);
	}

}