package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DcatAlreadyConfiguredException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingServerDcatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.entities.ServerDcat;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.serverdcat.services.ServerDcatService;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = { AdminServerDcatControllerImpl.class, ModelConverter.class, PrefixAdderImpl.class,
		AdminRestResponseEntityExceptionHandler.class })
@ExtendWith(MockitoExtension.class)
class AdminServerDcatControllerTest {
	private static final String ID = "id";
	@MockBean
	private ServerDcatService service;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void when_PostValidDcatModel_then_ReturnStatus200() throws Exception {
		final Model model = readModelFromFile("dcat/valid-server-dcat.ttl");
		when(service.createServerDcat(any())).thenReturn(new ServerDcat(ID, model));

		mockMvc.perform(post("/admin/api/v1/dcat")
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile("dcat/valid-server-dcat.ttl")))
				.andExpect(status().isCreated())
				.andExpect(content().string(ID));

		verify(service).createServerDcat(argThat(model::isIsomorphicWith));
	}

	@Test
	void when_ServerAlreadyHasDcatConfigured_and_PostDcat_then_Return400() throws Exception {
		when(service.createServerDcat(any())).thenThrow(DcatAlreadyConfiguredException.class);

		mockMvc.perform(post("/admin/api/v1/dcat")
						.contentType(Lang.TURTLE.getHeaderString())
						.content(readDataFromFile("dcat/valid-server-dcat.ttl")))
				.andExpect(status().isBadRequest());

		verify(service).createServerDcat(any());
	}

	@Test
	void when_PutValidDcat_then_ReturnStatus200() throws Exception {
		final Model model = readModelFromFile("dcat/valid-server-dcat.ttl");
		when(service.updateServerDcat(eq(ID), any())).thenReturn(new ServerDcat(ID, model));

		mockMvc.perform(put("/admin/api/v1/dcat/{id}", ID)
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile("dcat/valid-server-dcat.ttl")))
				.andExpect(status().isOk());

		verify(service).updateServerDcat(eq(ID), argThat(model::isIsomorphicWith));
	}

	@Test
	void when_ServerHasNoDcatYet_and_PutServerDcat_then_ReturnStatus404() throws Exception {
		final String id = "id";
		final Model model = readModelFromFile("dcat/valid-server-dcat.ttl");
		when(service.updateServerDcat(eq(id), any())).thenThrow(MissingServerDcatException.class);

		mockMvc.perform(put("/admin/api/v1/dcat/{id}", id)
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile("dcat/valid-server-dcat.ttl")))
				.andExpect(status().isNotFound());

		verify(service).updateServerDcat(eq(id), argThat(model::isIsomorphicWith));
	}

	@Test
	void when_DeleteExistingDcat_then_ReturnStatus200() throws Exception {
		final String id = "id";

		mockMvc.perform(delete("/admin/api/v1/dcat/{id}", id)).andExpect(status().isOk());

		verify(service).deleteServerDcat(id);
	}

	@Test
	void when_DeleteNonExistingDcat_then_ReturnStatus404() throws Exception {
		final String id = "id";
		doThrow(MissingServerDcatException.class).when(service).deleteServerDcat(id);

		mockMvc.perform(delete("/admin/api/v1/dcat/{id}", id)).andExpect(status().isNotFound());

		verify(service).deleteServerDcat(id);
	}

	private Model readModelFromFile(String fileName) throws IOException {
		final Path filePath = ResourceUtils.getFile("classpath:" + fileName).toPath();
		return RDFParser.source(filePath).lang(Lang.TURTLE).toModel();
	}

	private String readDataFromFile(String fileName) throws IOException {
		final File file = ResourceUtils.getFile("classpath:" + fileName);
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}
}