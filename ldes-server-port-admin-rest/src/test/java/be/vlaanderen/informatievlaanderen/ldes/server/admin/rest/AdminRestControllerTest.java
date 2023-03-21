package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.AdminWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services.LdesConfigModelService;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { AdminRestController.class,
		AdminWebConfig.class, AdminRestResponseEntityExceptionHandler.class })
class AdminRestControllerTest {

	@MockBean
	private LdesConfigModelService ldesConfigModelService;
	@Autowired
	private MockMvc mockMvc;

	@Test
	void when_ModelInRequestBody_Then_MethodIsCalled() throws Exception {
		ResultActions resultActions = mockMvc.perform(put("/admin/api/v1/eventstreams")
				.content(readDataFromFile("ldes-1.ttl", Lang.TURTLE))
				.contentType(MediaType.TEXT_PLAIN))
				.andDo(print())
				.andExpect(status().isOk());
		MvcResult result = resultActions.andReturn();
		verify(ldesConfigModelService, times(1)).updateEventStream(any());
	}

	@Test
	void when_MalformedModelInRequestBody_Then_ReturnedBadRequest() throws Exception {
		ResultActions resultActions = mockMvc.perform(put("/admin/api/v1/eventstreams")
				.content(readDataFromFile("ldes-without-type.ttl", Lang.TURTLE))
				.contentType(MediaType.TEXT_PLAIN))
				.andDo(print())
				.andExpect(status().isBadRequest());
		MvcResult result = resultActions.andReturn();
	}

	@Test
	void retrieveAllLdesStreams() {
	}

	@Test
	void putLdesStream() {
	}

	@Test
	void getLdesStream() {
	}

	@Test
	void getShape() {
	}

	@Test
	void putShape() {
	}

	@Test
	void getViews() {
	}

	@Test
	void putViews() {
	}

	@Test
	void getView() {
	}

	private String readDataFromFile(String fileName, Lang rdfFormat)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		String content = Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
		return RdfModelConverter.toString(RdfModelConverter.fromString(content,
				Lang.TURTLE), rdfFormat);
	}
}
