package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatdataset.services.DcatDatasetService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatDatasetValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { ModelConverter.class, PrefixAdderImpl.class, DcatDatasetRestController.class,
		AdminRestResponseEntityExceptionHandler.class })
class DcatDatasetRestControllerTest {
	private final static String COLLECTION_NAME = "collection";
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private DcatDatasetService dcatDatasetService;
	@MockBean
	private DcatDatasetValidator validator;

	@BeforeEach
	void setUp() {
		when(validator.supports(any())).thenReturn(true);
	}

	@Nested
	class PostDataset {
		@Test
		void when_DatasetIsPosted_Then_DatasetIsSaved_And_StatusIs201() throws Exception {
			final String dataset = readDataFromFile("dataset/valid.ttl");

			mockMvc.perform(post("/admin/api/v1/eventstreams/" + COLLECTION_NAME + "/dcat")
					.accept(contentTypeTurtle)
					.content(dataset)
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isCreated());

			verify(dcatDatasetService).saveDataset(any(DcatDataset.class));
		}
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

	private String readDataFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		return Files.lines(path).collect(Collectors.joining());
	}
}
