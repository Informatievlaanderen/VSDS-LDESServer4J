package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.AdminWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingShaclShapeException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesConfigException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesConfigException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.ShaclCollection;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
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
import java.util.Optional;
import java.util.stream.Collectors;

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
class AdminShapeRestControllerTest {
	@MockBean
	private ShaclShapeService shaclShapeService;

	@MockBean
	@Qualifier("shapeShaclValidator")
	private LdesConfigShaclValidator ldesConfigShaclValidator;

	@Autowired
	private MockMvc mockMvc;

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
			when(shaclShapeService.retrieveShaclShape(collectionName))
					.thenReturn(new ShaclShape(collectionName, expectedShapeModel));

			ResultActions resultActions = mockMvc
					.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/shape"))
					.andDo(print())
					.andExpect(status().isOk());

			MvcResult result = resultActions.andReturn();
			Model actualModel = RdfModelConverter.fromString(result.getResponse().getContentAsString(), Lang.TURTLE);
			assertTrue(actualModel.isIsomorphicWith(expectedShapeModel));
		}

		@Test
		void when_ViewNotPresent_Then_Returned404() throws Exception {
			String collectionName = "name1";
			when(shaclShapeService.retrieveShaclShape(collectionName))
					.thenThrow(new MissingShaclShapeException(collectionName));

			mockMvc.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/shape"))
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
					.contentType(Lang.TURTLE.getHeaderString()))
					.andDo(print())
					.andExpect(status().isOk());

			InOrder inOrder = inOrder(ldesConfigShaclValidator, shaclShapeService);
			inOrder.verify(ldesConfigShaclValidator, times(1)).validate(any(), any());
			inOrder.verify(shaclShapeService, times(1))
					.updateShaclShape(new ShaclShape(collectionName, expectedShapeModel));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void when_ModelWithoutType_Then_ReturnedBadRequest() throws Exception {
			String collectionName = "name1";
			mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/shape")
					.content(readDataFromFile("shape-without-type.ttl"))
					.contentType(Lang.TURTLE.getHeaderString()))
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
}
