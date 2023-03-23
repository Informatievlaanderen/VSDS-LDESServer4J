package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.AdminWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingLdesConfigException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.valueobjects.LdesConfigModel;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesConfigShaclValidator;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { AdminStreamRestController.class,
		AdminWebConfig.class, AdminRestResponseEntityExceptionHandler.class })
class AdminStreamRestControllerTest {
	@MockBean
	private LdesConfigModelService ldesConfigModelService;
	@MockBean
	@Qualifier("streamShaclValidator")
	private LdesConfigShaclValidator ldesConfigShaclValidator;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		when(ldesConfigShaclValidator.supports(any())).thenReturn(true);
	}

	@Test
	void when_StreamPresent_Then_StreamIsReturned() throws Exception {
		String collectionName = "name1";
		Model model = readModelFromFile("ldes-1.ttl");
		LdesConfigModel configModel = new LdesConfigModel(collectionName, model);
		when(ldesConfigModelService.retrieveEventStream(collectionName)).thenReturn(configModel);
		ResultActions resultActions = mockMvc.perform(get("/admin/api/v1/eventstreams/" + collectionName))
				.andDo(print())
				.andExpect(status().isOk());
		MvcResult result = resultActions.andReturn();
		Model actualModel = RdfModelConverter.fromString(result.getResponse().getContentAsString(), Lang.TURTLE);
		Assertions.assertTrue(actualModel.isIsomorphicWith(model));
	}

	@Test
	void when_StreamNotPresent_Then_Returned404() throws Exception {
		String collectionName = "name1";
		when(ldesConfigModelService.retrieveEventStream(collectionName))
				.thenThrow(new MissingLdesConfigException(collectionName));
		mockMvc.perform(get("/admin/api/v1/eventstreams/" + collectionName))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void when_ModelInRequestBody_Then_MethodIsCalled() throws Exception {
		mockMvc.perform(put("/admin/api/v1/eventstreams")
				.content(readDataFromFile("ldes-1.ttl", Lang.TURTLE))
				.contentType(MediaType.TEXT_PLAIN))
				.andDo(print())
				.andExpect(status().isOk());
		verify(ldesConfigModelService, times(1)).updateEventStream(any());
	}

	@Test
	void when_ModelWithoutType_Then_ReturnedBadRequest() throws Exception {
		mockMvc.perform(put("/admin/api/v1/eventstreams")
				.content(readDataFromFile("ldes-without-type.ttl", Lang.TURTLE))
				.contentType(MediaType.TEXT_PLAIN))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void when_MalformedModelInRequestBody_Then_ReturnedBadRequest() throws Exception {
		var request = put("/admin/api/v1/eventstreams")
				.content(readDataFromFile("malformed-ldes.ttl", Lang.TURTLE))
				.contentType(MediaType.TEXT_PLAIN);
		mockMvc.perform(request).andDo(print());
		mockMvc.perform(request)
				.andExpect(status().isBadRequest());
	}

	@Test
	void when_StreamEndpointCalledAndModelInRequestBody_Then_ModelIsValidated() throws Exception {
		final Model model = readModelFromFile("ldes-1.ttl");
		final LdesConfigModel ldesConfigModel = new LdesConfigModel("collectionName1", model);
		when(ldesConfigModelService.updateEventStream(ldesConfigModel)).thenReturn(ldesConfigModel);
		mockMvc.perform(put("/admin/api/v1/eventstreams")
				.content(readDataFromFile("ldes-1.ttl", Lang.TURTLE))
				.contentType(MediaType.TEXT_PLAIN))
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
