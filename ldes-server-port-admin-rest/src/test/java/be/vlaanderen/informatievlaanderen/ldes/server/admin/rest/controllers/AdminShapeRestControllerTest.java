package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.AdminWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesConfigException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.ShaclCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { AdminShapeRestController.class,
		AdminWebConfig.class, AdminRestResponseEntityExceptionHandler.class })
@Import(AdminShapeRestControllerTest.MockitoPublisherConfiguration.class)
class AdminShapeRestControllerTest {
	@MockBean
	private LdesConfigModelService ldesConfigModelService;
	@MockBean
	@Qualifier("shapeShaclValidator")
	private LdesConfigShaclValidator ldesConfigShaclValidator;

	@Captor
	ArgumentCaptor<ShaclChangedEvent> shaclChangedEventArgumentCaptor;
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ShaclCollection shaclCollection;

	@BeforeEach
    void setUp() {
        when(ldesConfigShaclValidator.supports(any())).thenReturn(true);
    }

	@Nested
	class GetRequest {
		@Test
		void when_ShapeIsPresentArePresent_Then_ShapeIsReturned() throws Exception {
			String collectionName = "name1";
			Model expectedShapeModel = readModelFromFile("shape-1.ttl");
			when(shaclCollection.retrieveShape(collectionName))
					.thenReturn(new LdesConfigModel(collectionName, expectedShapeModel));

			ResultActions resultActions = mockMvc
					.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/shape"))
					.andDo(print())
					.andExpect(status().isOk());

			MvcResult result = resultActions.andReturn();
			Model actualModel = RdfModelConverter.fromString(result.getResponse().getContentAsString(), Lang.TURTLE);
			Assertions.assertTrue(actualModel.isIsomorphicWith(expectedShapeModel));
		}

		@Test
		void when_ViewNotPresent_Then_Returned404() throws Exception {
			String collectionName = "name1";
			String viewName = "view1";
			when(ldesConfigModelService.retrieveView(collectionName, viewName))
					.thenThrow(new MissingLdesConfigException(collectionName + "/" + viewName));

			mockMvc.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/views/" + viewName))
					.andDo(print())
					.andExpect(status().isNotFound());
		}
	}

	@Nested
	class PutRequest {
		@Test
		void when_ModelInRequestBody_Then_MethodIsCalled() throws Exception {
			String collectionName = "name1";
			String fileName = "shape-1.ttl";
			Model expectedShapeModel = readModelFromFile(fileName);

			mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/shape")
					.content(readDataFromFile(fileName))
					.contentType(MediaType.TEXT_PLAIN))
					.andDo(print())
					.andExpect(status().isOk());

			InOrder inOrder = inOrder(ldesConfigShaclValidator, applicationEventPublisher);
			inOrder.verify(ldesConfigShaclValidator, times(1)).validate(any(), any());
			inOrder.verify(applicationEventPublisher, times(1)).publishEvent(shaclChangedEventArgumentCaptor.capture());
			inOrder.verifyNoMoreInteractions();
			ShaclChangedEvent shaclChangedEvent = shaclChangedEventArgumentCaptor.getValue();
			assertEquals(collectionName, shaclChangedEvent.getCollectionName());
			assertTrue(shaclChangedEvent.getShacl().isIsomorphicWith(expectedShapeModel));
		}

		@Test
		void when_ModelWithoutType_Then_ReturnedBadRequest() throws Exception {
			String collectionName = "name1";
			mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/shape")
					.content(readDataFromFile("shape-without-type.ttl"))
					.contentType(MediaType.TEXT_PLAIN))
					.andDo(print())
					.andExpect(status().isBadRequest());
		}
	}

	private String readDataFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		String content = Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
		return content;
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

	@TestConfiguration
	static class MockitoPublisherConfiguration {

		@Bean
		@Primary
		ApplicationEventPublisher publisher() {
			return mock(ApplicationEventPublisher.class);
		}

		@Bean
		ShaclCollection shaclCollection() {
			return mock(ShaclCollection.class);
		}
	}
}
