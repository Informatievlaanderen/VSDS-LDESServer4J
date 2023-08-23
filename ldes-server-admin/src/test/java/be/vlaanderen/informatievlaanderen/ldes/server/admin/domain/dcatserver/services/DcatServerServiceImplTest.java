package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcatserver.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.exceptions.DcatAlreadyConfiguredException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.exceptions.MissingDcatServerException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services.DcatServerServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.DcatShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.DcatViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DcatServerServiceImplTest {
	private static final String ID = "id";
	private static final Model DCAT = ModelFactory.createDefaultModel();
	private static final DcatServer SERVER_DCAT = new DcatServer(ID, DCAT);
	private DcatServerService service;
	@Mock
	private DcatServerRepository repository;
	@Mock
	private DcatViewService dcatViewService;
	@Mock
	private DcatShaclValidator dcatShaclValidator;
	@Mock
	private DcatDatasetService dcatDatasetService;

	@BeforeEach
	void setUp() {
		service = new DcatServerServiceImpl(repository, dcatViewService, dcatDatasetService, "http://localhost.dev",
				dcatShaclValidator);
	}

	@Test
	void when_ServerHasNoDcat_and_CreateDcat_then_ReturnIt() {
		when(repository.getServerDcat()).thenReturn(List.of());
		when(repository.saveServerDcat(any())).thenReturn(SERVER_DCAT);

		final DcatServer createdDcat = assertDoesNotThrow(() -> service.createDcatServer(DCAT));

		assertEquals(SERVER_DCAT, createdDcat);
		InOrder inOrder = Mockito.inOrder(repository);
		inOrder.verify(repository).getServerDcat();
		inOrder.verify(repository).saveServerDcat(any());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_ServerAlreadyHasDcatConfigured_then_ThrowException() {
		when(repository.getServerDcat()).thenReturn(List.of(SERVER_DCAT));
		final String expectedMessage = "The server can contain only one dcat configuration and there already has been configured one with id " + ID;

		Exception e = assertThrows(DcatAlreadyConfiguredException.class, () -> service.createDcatServer(DCAT));

		assertEquals(expectedMessage, e.getMessage());
		verify(repository).getServerDcat();
		verifyNoMoreInteractions(repository);
	}

	@Test
	void when_UpdateExistingDcat_then_ReturnDcat() {
		when(repository.getServerDcatById(ID)).thenReturn(Optional.of(SERVER_DCAT));
		when(repository.saveServerDcat(SERVER_DCAT)).thenReturn(SERVER_DCAT);

		DcatServer updatedDcatServer = assertDoesNotThrow(() -> service.updateDcatServer(ID, DCAT));

		assertEquals(SERVER_DCAT, updatedDcatServer);
		InOrder inOrder = inOrder(repository);
		inOrder.verify(repository).getServerDcatById(ID);
		inOrder.verify(repository).saveServerDcat(SERVER_DCAT);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_UpdateNonExistingDcat_then_ThrowException() {
		when(repository.getServerDcatById(ID)).thenReturn(Optional.empty());
		String expectedMessage = String.format("No dcat is configured on the server with id %s", ID);

		Exception e = assertThrows(MissingDcatServerException.class, () -> service.updateDcatServer(ID, DCAT));

		assertEquals(expectedMessage, e.getMessage());
		verify(repository).getServerDcatById(ID);
		verifyNoMoreInteractions(repository);
	}

	@Test
	void when_DeleteExistingDcat_then_ReturnVoid() {
		assertDoesNotThrow(() -> service.deleteDcatServer(ID));
		verify(repository).deleteServerDcat(ID);
		verifyNoMoreInteractions(repository);
	}

	@Nested
	class GetComposedDcat {

		private final static String COLLECTION_PARCELS = "parcels";
		private final static String COLLECTION_BUILDINGS = "buildings";
		private final static String VIEW_BY_PAGE = "by-page";
		private final static String VIEW_BY_TIME = "by-time";

		@Test
		void should_ReturnEmptyModel_when_NoServerDcatPresent() {
			when(repository.findSingleDcatServer()).thenReturn(Optional.empty());

			Model result = service.getComposedDcat();

			assertTrue(result.isEmpty());
			verify(dcatShaclValidator).validate(any(), any());
		}

		@Test
		void should_CombineServerAndDataset_when_TheseArePresent_and_ViewsAreNot() {
			when(repository.findSingleDcatServer()).thenReturn(createServer());
			when(dcatDatasetService.findAll()).thenReturn(createDatasets());
			when(dcatViewService.findAll()).thenReturn(List.of());

			Model result = service.getComposedDcat();

			String path = "dcat/dcat-combined-without-views.ttl";
			Model expectedResult = RDFParser.source(path).lang(Lang.TURTLE).build().toModel();
			assertTrue(expectedResult.isIsomorphicWith(result));
		}

		@Test
		void should_CombineServerDatasetAndViews_when_AllArePresent() {
			when(repository.findSingleDcatServer()).thenReturn(createServer());
			when(dcatDatasetService.findAll()).thenReturn(createDatasets());
			when(dcatViewService.findAll()).thenReturn(createViews());

			Model result = service.getComposedDcat();

			String path = "dcat/dcat-combined-all.ttl";
			Model expectedResult = RDFParser.source(path).lang(Lang.TURTLE).build().toModel();
			assertTrue(expectedResult.isIsomorphicWith(result));
		}

		private Optional<DcatServer> createServer() {
			Model server = RDFParser.source("dcat/server.ttl").lang(Lang.TURTLE).build().toModel();
			return Optional.of(new DcatServer("id1", server));
		}

		private List<DcatView> createViews() {
			final List<DcatView> views = new ArrayList<>();
			views.addAll(createDcatBuildingViews());
			views.addAll(createDcatParcelViews());
			return views;
		}

		private List<DcatDataset> createDatasets() {
			Model parcels = RDFParser.source("dcat/parcels.ttl").lang(Lang.TURTLE).build().toModel();
			DcatDataset parcelDataset = new DcatDataset(COLLECTION_PARCELS, parcels);

			Model buildings = RDFParser.source("dcat/buildings.ttl").lang(Lang.TURTLE).build().toModel();
			DcatDataset buildingDataset = new DcatDataset(COLLECTION_BUILDINGS, buildings);
			return List.of(parcelDataset, buildingDataset);
		}

		private List<DcatView> createDcatParcelViews() {
			Model byPage = RDFParser.source("dcat/view-by-page.ttl").lang(Lang.TURTLE).build().toModel();
			Model byTime = RDFParser.source("dcat/view-by-geospatial.ttl").lang(Lang.TURTLE).build().toModel();
			return List.of(
					DcatView.from(new ViewName(COLLECTION_PARCELS, VIEW_BY_PAGE), byPage),
					DcatView.from(new ViewName(COLLECTION_PARCELS, VIEW_BY_TIME), byTime));
		}

		private List<DcatView> createDcatBuildingViews() {
			Model byPage = RDFParser.source("dcat/view-by-page.ttl").lang(Lang.TURTLE).build().toModel();
			return List.of(DcatView.from(new ViewName(COLLECTION_BUILDINGS, VIEW_BY_PAGE), byPage));
		}

	}

}
