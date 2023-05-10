package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.AdminWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.ViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.MissingViewException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ViewSpecificationConverter.viewFromModel;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { AdminViewsRestController.class,
		AdminWebConfig.class, AdminRestResponseEntityExceptionHandler.class })
class AdminViewsRestControllerTest {
	@MockBean
	private ViewService viewService;
	@MockBean
	private ViewValidator validator;
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
    void setUp() {
        when(validator.supports(any())).thenReturn(true);
    }

	@Test
	void when_StreamAndViewsArePresent_Then_ViewsAreReturned() throws Exception {
		String collectionName = "name1";
		Model expectedViewModel1 = readModelFromFile("view-1.ttl");
		ViewSpecification view1 = ViewSpecificationConverter.viewFromModel(expectedViewModel1, collectionName);
		Model expectedViewModel2 = readModelFromFile("view-2.ttl");
		ViewSpecification view2 = ViewSpecificationConverter.viewFromModel(expectedViewModel2, collectionName);
		when(viewService.getViewsByCollectionName(collectionName)).thenReturn(List.of(view1, view2));

		ResultActions resultActions = mockMvc
				.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/views"))
				.andDo(print())
				.andExpect(status().isOk());
		MvcResult result = resultActions.andReturn();
		Model actualModel = RdfModelConverter.fromString(result.getResponse().getContentAsString(), Lang.TURTLE);
		Assertions.assertTrue(actualModel.isIsomorphicWith(expectedViewModel1.add(expectedViewModel2)));
	}

	@Test
	void when_StreamAndViewArePresent_Then_ViewIsReturned() throws Exception {
		String collectionName = "name1";
		String viewName = "view1";
		Model expectedViewModel = readModelFromFile("view-1.ttl");
		ViewSpecification view = ViewSpecificationConverter.viewFromModel(expectedViewModel, collectionName);
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
				.thenThrow(new MissingViewException(new ViewName(collectionName, viewName)));
		MvcResult mvcResult = mockMvc
				.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/views/" + viewName).accept(contentTypeTurtle))
				.andExpect(status().isNotFound()).andReturn();

		assertEquals("Collection name1 does not have a view: view1", mvcResult.getResponse().getContentAsString());
	}

	@Test
	void when_ModelInRequestBody_Then_MethodIsCalled() throws Exception {
		String collectionName = "name1";
		Model expectedViewModel = readModelFromFile("view-1.ttl");
		ViewSpecification view = ViewSpecificationConverter.viewFromModel(expectedViewModel, collectionName);
		mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/views")
				.content(readDataFromFile("view-1.ttl"))
				.contentType(Lang.TURTLE.getHeaderString()))
				.andDo(print())
				.andExpect(status().isOk());
		verify(viewService, times(1)).addView(view);
	}

	@Test
	void when_StreamEndpointCalledAndModelInRequestBody_Then_ModelIsValidated() throws Exception {
		String collectionName = "name1";
		mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/views")
				.content(readDataFromFile("view-1.ttl"))
				.contentType(Lang.TURTLE.getHeaderString()));
		verify(validator, times(1)).validate(any(), any());
	}

	@Test
	void when_Delete_Then_RemoveMethodCalled() throws Exception {
		String collectionName = "name1";
		String viewName = "view1";
		mockMvc.perform(delete("/admin/api/v1/eventstreams/" + collectionName + "/views/" + viewName))
				.andDo(print());
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
