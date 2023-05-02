package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.AdminWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesConfigException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { AdminViewsRestController.class,
		AdminWebConfig.class, AdminRestResponseEntityExceptionHandler.class })
class AdminViewsRestControllerTest {

	@MockBean
	private LdesConfigModelService ldesConfigModelService;
	@MockBean
	private ViewService viewService;
	@MockBean
	@Qualifier("viewShaclValidator")
	private LdesConfigShaclValidator ldesConfigShaclValidator;
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
    void setUp() {
        when(ldesConfigShaclValidator.supports(any())).thenReturn(true);
    }

	@Test
	void when_StreamAndViewArePresent_Then_ViewIsReturned() throws Exception {
		String collectionName = "name1";
		String viewName = "view1";
		Model expectedViewModel = readModelFromFile("view-1.ttl");
		LdesConfigModel configModel = new LdesConfigModel(viewName, expectedViewModel);
		when(ldesConfigModelService.retrieveView(collectionName, viewName)).thenReturn(configModel);
		ResultActions resultActions = mockMvc
				.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/views/" + viewName))
				.andDo(print())
				.andExpect(status().isOk());
		MvcResult result = resultActions.andReturn();
		Model actualModel = RdfModelConverter.fromString(result.getResponse().getContentAsString(), Lang.TURTLE);
		Assertions.assertTrue(actualModel.isIsomorphicWith(expectedViewModel));
	}

	@Test
	void when_ViewNotPresent_Then_Returned404() throws Exception {
		String collectionName = "name1";
		String viewName = "view1";
		Model expectedViewModel = readModelFromFile("view-1.ttl");
		LdesConfigModel configModel = new LdesConfigModel(viewName, expectedViewModel);
		when(ldesConfigModelService.retrieveView(collectionName, viewName))
				.thenThrow(new MissingLdesConfigException(collectionName + "/" + viewName));
		ResultActions resultActions = mockMvc
				.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/views/" + viewName))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void when_ModelInRequestBody_Then_MethodIsCalled() throws Exception {
		String collectionName = "name1";
		String viewName = "view1";
		Model expectedViewModel = readModelFromFile("view-1.ttl");
		LdesConfigModel configModel = new LdesConfigModel(viewName, expectedViewModel);
		when(ldesConfigModelService.addView(anyString(), any())).thenReturn(configModel);
		ResultActions resultActions = mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/views")
				.content(readDataFromFile("view-1.ttl", Lang.TURTLE))
				.contentType(Lang.TURTLE.getHeaderString()))
				.andDo(print())
				.andExpect(status().isOk());
		verify(ldesConfigModelService, times(1)).addView(anyString(), any());
	}

	@Test
	void when_ModelWithoutType_Then_ReturnedBadRequest() throws Exception {
		String collectionName = "name1";
		ResultActions resultActions = mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/views")
				.content(readDataFromFile("view-without-type.ttl", Lang.TURTLE))
				.contentType(Lang.TURTLE.getHeaderString()))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void when_StreamEndpointCalledAndModelInRequestBody_Then_ModelIsValidated() throws Exception {
		String collectionName = "name1";
		String viewName = "view1";
		final Model model = readModelFromFile("view-1.ttl");
		final LdesConfigModel ldesConfigModel = new LdesConfigModel(viewName, model);
		when(ldesConfigModelService.addView(collectionName, ldesConfigModel)).thenReturn(ldesConfigModel);
		ResultActions resultActions = mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/views")
				.content(readDataFromFile("ldes-1.ttl", Lang.TURTLE))
				.contentType(Lang.TURTLE.getHeaderString()))
				.andDo(print());
		verify(ldesConfigShaclValidator, times(1)).validate(any(), any());
	}

	private String readDataFromFile(String fileName, Lang rdfFormat)
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
