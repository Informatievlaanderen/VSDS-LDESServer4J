package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.services.DcatDatasetService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.dcat.DcatDatasetValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ExistingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.HostNamePrefixConstructorConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.RelativeUriPrefixConstructor;
import org.apache.jena.riot.Lang;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { HttpModelConverter.class, PrefixAdderImpl.class, DcatDatasetRestController.class,
		AdminRestResponseEntityExceptionHandler.class, HostNamePrefixConstructorConfig.class, RelativeUriPrefixConstructor.class,
		RdfModelConverter.class, RdfModelConverter.class })
class DcatDatasetRestControllerTest {
	private static final String COLLECTION_NAME = "collection";
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
			final String dataset = readDataFromFile("dcat/dataset/valid.ttl");

			mockMvc.perform(post("/admin/api/v1/eventstreams/" + COLLECTION_NAME + "/dcat")
					.accept(contentTypeTurtle)
					.content(dataset)
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isCreated());

			verify(dcatDatasetService).saveDataset(any(DcatDataset.class));
		}

		@Test
		void when_DatasetExists_Then_StatusIs400() throws Exception {
			final String dataset = readDataFromFile("dcat/dataset/valid.ttl");
			doThrow(new ExistingResourceException("dcat-dataset", COLLECTION_NAME)).when(dcatDatasetService)
					.saveDataset(any(DcatDataset.class));

			mockMvc.perform(post("/admin/api/v1/eventstreams/" + COLLECTION_NAME + "/dcat")
					.accept(contentTypeTurtle)
					.content(dataset)
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isBadRequest());

			verify(dcatDatasetService).saveDataset(any(DcatDataset.class));
		}
	}

	@Nested
	class PutDataset {
		@Test
		void when_DatasetExists_Then_DatasetIsSaved_And_StatusIs200() throws Exception {
			final String dataset = readDataFromFile("dcat/dataset/valid.ttl");

			mockMvc.perform(put("/admin/api/v1/eventstreams/" + COLLECTION_NAME + "/dcat")
					.accept(contentTypeTurtle)
					.content(dataset)
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isOk());

			verify(dcatDatasetService).updateDataset(any(DcatDataset.class));
		}

		@Test
		void when_DatasetDoesNotExist_Then_StatusIs404() throws Exception {
			final String dataset = readDataFromFile("dcat/dataset/valid.ttl");
			doThrow(new MissingResourceException("dcat-dataset", COLLECTION_NAME)).when(dcatDatasetService)
					.updateDataset(any(DcatDataset.class));

			mockMvc.perform(put("/admin/api/v1/eventstreams/" + COLLECTION_NAME + "/dcat")
					.accept(contentTypeTurtle)
					.content(dataset)
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isNotFound());

			verify(dcatDatasetService).updateDataset(any(DcatDataset.class));
		}
	}

	@Nested
	class DeleteDataset {
		@Test
		void when_Delete_Then_StatusIs200() throws Exception {
			mockMvc.perform(delete("/admin/api/v1/eventstreams/" + COLLECTION_NAME + "/dcat"))
					.andExpect(status().isOk());

			verify(dcatDatasetService).deleteDataset(COLLECTION_NAME);
		}
	}

	private String readDataFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		return Files.lines(path).collect(Collectors.joining());
	}
}
