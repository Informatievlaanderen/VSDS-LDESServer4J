package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ModelValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ValidatorsConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.AdminWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.*;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.RequestContextExtracter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@EnableConfigurationProperties(value = ServerConfig.class)
@ContextConfiguration(classes = { AdminViewsRestController.class, PrefixAdderImpl.class,
		HttpModelConverter.class, EventStreamResponseConverterImpl.class,
		ViewSpecificationConverter.class, ValidatorsConfig.class,
		AdminRestResponseEntityExceptionHandler.class, RetentionModelExtractor.class,
		FragmentationConfigExtractor.class, RequestContextExtracter.class, AdminWebConfig.class })
class AdminViewsRestControllerTest {
	@MockBean
	private ViewService viewService;
	@SpyBean(name = "viewShaclValidator")
	private ModelValidator validator;
	@Autowired
	private ViewSpecificationConverter converter;
	@Autowired
	private MockMvc mockMvc;

	@Test
	void when_StreamAndViewsArePresent_Then_ViewsAreReturned() throws Exception {
		String collectionName = "name1";
		Model expectedViewModel1 = readModelFromFile("view-1.ttl");
		ViewSpecification view1 = converter.viewFromModel(expectedViewModel1, collectionName);
		Model expectedViewModel2 = readModelFromFile("view-2.ttl");
		ViewSpecification view2 = converter.viewFromModel(expectedViewModel2, collectionName);
		when(viewService.getViewsByCollectionName(collectionName)).thenReturn(List.of(view1, view2));

		MvcResult result = mockMvc
				.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/views")
						.accept(contentTypeTurtle))
				.andExpect(status().isOk())
				.andReturn();

		Model actualModel = RdfModelConverter.fromString(result.getResponse().getContentAsString(), Lang.TURTLE);
		Assertions.assertTrue(actualModel.isIsomorphicWith(expectedViewModel1.add(expectedViewModel2)));
	}

	@Test
	void when_StreamAndViewArePresent_Then_ViewIsReturned() throws Exception {
		String collectionName = "name1";
		String viewName = "view1";
		Model expectedViewModel = readModelFromFile("view-1.ttl");
		ViewSpecification view = converter.viewFromModel(expectedViewModel, collectionName);
		when(viewService.getViewByViewName(new ViewName(collectionName, viewName))).thenReturn(view);
		ResultActions resultActions = mockMvc
				.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/views/" + viewName)
						.accept(contentTypeTurtle))
				.andExpect(status().isOk());
		MvcResult result = resultActions.andReturn();
		Model actualModel = RdfModelConverter.fromString(result.getResponse().getContentAsString(), Lang.TURTLE);
		Assertions.assertTrue(actualModel.isIsomorphicWith(expectedViewModel));
	}

	@Test
	void when_ViewNotPresent_Then_Returned404() throws Exception {
		String collectionName = "name1";
		String viewName = "view1";
		when(viewService.getViewByViewName(new ViewName(collectionName, viewName)))
				.thenThrow(new MissingResourceException("view", "%s/%s".formatted(collectionName, viewName)));
		MvcResult mvcResult = mockMvc
				.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/views/" + viewName).accept(
						contentTypeTurtle))
				.andExpect(status().isNotFound()).andReturn();

		assertThat(mvcResult.getResponse().getContentAsString())
				.isEqualTo("Resource of type: view with id: %s/%s could not be found.", collectionName, viewName);
	}

	@Test
	void when_ModelInRequestBody_Then_MethodIsCalled() throws Exception {
		String collectionName = "name1";
		Model expectedViewModel = readModelFromFile("view-1.ttl");
		ViewSpecification view = converter.viewFromModel(expectedViewModel, collectionName);

		mockMvc.perform(post("/admin/api/v1/eventstreams/" + collectionName + "/views")
				.content(readDataFromFile("view-1.ttl"))
				.contentType(Lang.TURTLE.getHeaderString()))
				.andExpect(status().isCreated());
		verify(viewService, times(1)).addView(view);
	}

	@Test
	void when_StreamEndpointCalledAndModelInRequestBody_Then_ModelIsValidated() throws Exception {
		String collectionName = "name1";
		mockMvc.perform(post("/admin/api/v1/eventstreams/" + collectionName + "/views")
				.content(readDataFromFile("view-1.ttl"))
				.contentType(Lang.TURTLE.getHeaderString()));
		verify(validator, times(1)).validate(any(), any());
	}

	@Test
	void when_Delete_Then_RemoveMethodCalled() throws Exception {
		String collectionName = "name1";
		String viewName = "view1";
		mockMvc.perform(delete("/admin/api/v1/eventstreams/" + collectionName + "/views/" + viewName));
		verify(viewService).deleteViewByViewName(new ViewName(collectionName, viewName));
	}

	private String readDataFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		return Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}
}
