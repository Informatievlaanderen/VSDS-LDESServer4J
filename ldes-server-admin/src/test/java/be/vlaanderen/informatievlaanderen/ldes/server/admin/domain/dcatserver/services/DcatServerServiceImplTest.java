package be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcatserver.services;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.repositories.DcatServerRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatserver.services.DcatServerServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.DcatShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.DcatViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ExistingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DcatServerServiceImplTest {
	private static final String ID = "2a896d35-8c72-4723-83b3-add9b1be96aa";
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

		final DcatServer createdDcat = service.createDcatServer(DCAT);

		assertThat(createdDcat).isEqualTo(SERVER_DCAT);
		InOrder inOrder = Mockito.inOrder(repository);
		inOrder.verify(repository).getServerDcat();
		inOrder.verify(repository).saveServerDcat(any());
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_ServerAlreadyHasDcatConfigured_then_ThrowException() {
		when(repository.getServerDcat()).thenReturn(List.of(SERVER_DCAT));

		assertThatThrownBy(() -> service.createDcatServer(DCAT))
				.isInstanceOf(ExistingResourceException.class)
				.hasMessage("Resource of type: dcat-catalog with id: %s already exists.", ID);
		verify(repository).getServerDcat();
		verifyNoMoreInteractions(repository);
	}

	@Test
	void when_UpdateExistingDcat_then_ReturnDcat() {
		when(repository.getServerDcatById(ID)).thenReturn(Optional.of(SERVER_DCAT));
		when(repository.saveServerDcat(SERVER_DCAT)).thenReturn(SERVER_DCAT);

		DcatServer updatedDcatServer = service.updateDcatServer(ID, DCAT);

		assertThat(updatedDcatServer).isEqualTo(SERVER_DCAT);
		InOrder inOrder = inOrder(repository);
		inOrder.verify(repository).getServerDcatById(ID);
		inOrder.verify(repository).saveServerDcat(SERVER_DCAT);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void when_UpdateNonExistingDcat_then_ThrowException() {
		when(repository.getServerDcatById(ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.updateDcatServer(ID, DCAT))
				.isInstanceOf(MissingResourceException.class)
				.hasMessage("Resource of type: dcat-catalog with id: %s could not be found.", ID);

		verify(repository).getServerDcatById(ID);
		verifyNoMoreInteractions(repository);
	}

	@Test
	void when_DeleteExistingDcat_then_ReturnVoid() {
		assertThatNoException().isThrownBy(() -> service.deleteDcatServer(ID));
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

			assertThat(result).matches(Model::isEmpty);
			verify(dcatShaclValidator).validate(any());
		}

		@Test
		void should_CombineServerAndDataset_when_TheseArePresent_and_ViewsAreNot() {
			when(repository.findSingleDcatServer()).thenReturn(createServer());
			when(dcatDatasetService.findAll()).thenReturn(createDatasets());
			when(dcatViewService.findAll()).thenReturn(List.of());

			Model result = service.getComposedDcat();

			String path = "dcat/dcat-combined-without-views.ttl";
			Model expectedResult = RDFParser.source(path).lang(Lang.TURTLE).build().toModel();
			assertThat(result).matches(expectedResult::isIsomorphicWith);
		}

		@Test
		void should_CombineServerDatasetAndViews_when_AllArePresent() {
			when(repository.findSingleDcatServer()).thenReturn(createServer());
			when(dcatDatasetService.findAll()).thenReturn(createDatasets());
			when(dcatViewService.findAll()).thenReturn(createViews());

			Model result = service.getComposedDcat();

			String path = "dcat/dcat-combined-all.ttl";
			Model expectedResult = RDFParser.source(path).lang(Lang.TURTLE).build().toModel();
			assertThat(result).matches(expectedResult::isIsomorphicWith);
		}

		private Optional<DcatServer> createServer() {
			Model server = RDFParser.source("dcat/server.ttl").lang(Lang.TURTLE).build().toModel();
			return Optional.of(new DcatServer(ID, server));
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
