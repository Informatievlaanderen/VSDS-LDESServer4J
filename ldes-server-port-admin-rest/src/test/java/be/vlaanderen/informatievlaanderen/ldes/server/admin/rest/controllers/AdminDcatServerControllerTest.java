package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.entities.DcatServer;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.exceptions.MissingDcatServerException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.dcatserver.services.DcatServerService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.DcatAlreadyConfiguredException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.dcat.DcatCatalogValidator;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = { AdminServerDcatController.class, ModelConverter.class, PrefixAdderImpl.class,
		AdminRestResponseEntityExceptionHandler.class })
@ExtendWith(MockitoExtension.class)
class AdminDcatServerControllerTest {
	private static final String ID = "id";
	@MockBean
	private DcatServerService service;
	@MockBean
	private DcatCatalogValidator validator;
	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		when(validator.supports(argThat(Model.class::isAssignableFrom))).thenReturn(true);
	}

	@Nested
	class PostRequest {
		@Test
		void when_PostValidDcatModel_then_ReturnStatus200() throws Exception {
			final Model model = readModelFromFile();

			when(service.createDcatServer(any())).thenReturn(new DcatServer(ID, model));

			mockMvc.perform(post("/admin/api/v1/dcat")
					.contentType(Lang.TURTLE.getHeaderString())
					.content(readDataFromFile()))
					.andExpect(status().isCreated())
					.andExpect(content().string(ID));

			InOrder inOrder = inOrder(service, validator);
			inOrder.verify(validator).validate(argThat(IsIsomorphic.with(model)), any());
			inOrder.verify(service).createDcatServer(argThat(model::isIsomorphicWith));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void when_PostInvalidDcatModel_then_ReturnStatus400() throws Exception {
			final String modelString = readDataFromFile().replace("[]",
					"<http://example.org/svc/1>");

			doThrow(IllegalArgumentException.class).when(validator).validate(any(), any());

			mockMvc.perform(post("/admin/api/v1/dcat")
					.contentType(Lang.TURTLE.getHeaderString())
					.content(modelString))
					.andExpect(status().isBadRequest());

			verify(validator).validate(any(), any());
			verifyNoInteractions(service);
		}

		@Test
		void when_ServerAlreadyHasDcatConfigured_and_PostDcat_then_Return400() throws Exception {
			when(service.createDcatServer(any())).thenThrow(DcatAlreadyConfiguredException.class);

			mockMvc.perform(post("/admin/api/v1/dcat")
							.contentType(Lang.TURTLE.getHeaderString())
							.content(readDataFromFile()))
					.andExpect(status().isBadRequest());

			InOrder inOrder = inOrder(service, validator);
			inOrder.verify(validator).validate(any(), any());
			inOrder.verify(service).createDcatServer(any());
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Nested
	class PutRequest {
		@Test
		void when_PutValidDcat_then_ReturnStatus200() throws Exception {
			final Model model = readModelFromFile();

			when(service.createDcatServer(any())).thenReturn(new DcatServer(ID, model));

			mockMvc.perform(put("/admin/api/v1/dcat/{id}", ID)
					.contentType(Lang.TURTLE.getHeaderString())
					.content(readDataFromFile()))
					.andExpect(status().isOk());

			InOrder inOrder = inOrder(service, validator);
			inOrder.verify(validator).validate(argThat(IsIsomorphic.with(model)), any());
			inOrder.verify(service).updateDcatServer(eq(ID), argThat(model::isIsomorphicWith));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void when_PutInvalidDcatModel_then_ReturnStatus400() throws Exception {
			final String modelString = readDataFromFile().replace("[]",
					"<http://example.org/svc/1>");

			doThrow(IllegalArgumentException.class).when(validator).validate(any(), any());

			mockMvc.perform(put("/admin/api/v1/dcat/{id}", ID)
					.contentType(Lang.TURTLE.getHeaderString())
					.content(modelString))
					.andExpect(status().isBadRequest());

			verify(validator).validate(any(), any());
			verifyNoInteractions(service);
		}

		@Test
		void when_ServerHasNoDcatYet_and_PutServerDcat_then_ReturnStatus404() throws Exception {
			final Model model = readModelFromFile();
			when(service.updateDcatServer(eq(ID), any())).thenThrow(MissingDcatServerException.class);

			mockMvc.perform(put("/admin/api/v1/dcat/{id}", ID)
					.contentType(Lang.TURTLE.getHeaderString())
					.content(readDataFromFile()))
					.andExpect(status().isNotFound());

			InOrder inOrder = inOrder(service, validator);
			inOrder.verify(validator).validate(argThat(IsIsomorphic.with(model)), any());
			inOrder.verify(service).updateDcatServer(eq(ID), argThat(IsIsomorphic.with(model)));
			inOrder.verifyNoMoreInteractions();
		}
	}

	@Nested
	class DeleteRequest {
		@Test
		void when_DeleteNonExistingDcat_then_ReturnStatus404() throws Exception {
			doThrow(MissingDcatServerException.class).when(service).deleteDcatServer(ID);

			mockMvc.perform(delete("/admin/api/v1/dcat/{id}", ID)).andExpect(status().isNotFound());

			verify(service).deleteDcatServer(ID);
			verifyNoInteractions(validator);
		}

		@Test
		void when_DeleteExistingDcat_then_ReturnStatus200() throws Exception {
			final String id = "id";

			mockMvc.perform(delete("/admin/api/v1/dcat/{id}", id)).andExpect(status().isOk());

			verify(service).deleteDcatServer(id);
			verifyNoInteractions(validator);
		}
	}

	private Model readModelFromFile() {
		return RDFParser.source("dcat/valid-server-dcat.ttl").lang(Lang.TURTLE).toModel();
	}

	private String readDataFromFile() throws IOException {
		final File file = ResourceUtils.getFile("classpath:dcat/valid-server-dcat.ttl");
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}
}
