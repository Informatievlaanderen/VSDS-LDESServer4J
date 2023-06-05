package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.ModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.DcatViewValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.exception.MissingViewDcatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.service.DcatViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { AppConfig.class, DcatViewsRestController.class, PrefixAdderImpl.class,
		ModelConverter.class, AdminRestResponseEntityExceptionHandler.class })
class DcatViewsRestControllerTest {

	private final static String COLLECTION_NAME = "collectionName";
	private final static String VIEW_NAME = "viewName";

	@MockBean
	private DcatViewService dcatViewService;

	@MockBean
	private DcatViewValidator validator;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
    void setUp() {
        when(validator.supports(any())).thenReturn(true);
    }

	@Nested
	class CreateDcat {

		@Test
		void should_Return400_when_ValidatorThrowsIllegalArgumentException() throws Exception {
			doThrow(IllegalArgumentException.class).when(validator).validate(any(), any());

			mockMvc.perform(post(DcatViewsRestController.BASE_URL, COLLECTION_NAME, VIEW_NAME)
					.content(writeToTurtle(readTurtleFromFile("dcat-view-valid.ttl")))
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(dcatViewService);
		}

		@Test
		void should_Return201_when_CreatedSuccessfully() throws Exception {
			Model dcat = readTurtleFromFile("dcat-view-valid.ttl");
			mockMvc.perform(post(DcatViewsRestController.BASE_URL, COLLECTION_NAME, VIEW_NAME)
					.content(writeToTurtle(dcat))
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isCreated());

			verify(dcatViewService)
					.create(eq(new ViewName(COLLECTION_NAME, VIEW_NAME)), argThat(IsIsomorphic.with(dcat)));
		}

	}

	@Nested
	class UpdateDcat {

		@Test
		void should_Return400_when_ValidatorThrowsIllegalArgumentException() throws Exception {
			doThrow(IllegalArgumentException.class).when(validator).validate(any(), any());

			mockMvc.perform(put(DcatViewsRestController.BASE_URL, COLLECTION_NAME, VIEW_NAME)
					.content(writeToTurtle(readTurtleFromFile("dcat-view-valid.ttl")))
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isBadRequest());

			verifyNoInteractions(dcatViewService);
		}

		@Test
		void should_Return200_when_UpdatedSuccessfully() throws Exception {
			Model dcat = readTurtleFromFile("dcat-view-valid.ttl");
			mockMvc.perform(put(DcatViewsRestController.BASE_URL, COLLECTION_NAME, VIEW_NAME)
					.content(writeToTurtle(dcat))
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isOk());

			verify(dcatViewService)
					.update(eq(new ViewName(COLLECTION_NAME, VIEW_NAME)), argThat(IsIsomorphic.with(dcat)));
		}

		@Test
		void should_Return404_when_ResourceNotFound() throws Exception {
			doThrow(MissingViewDcatException.class).when(dcatViewService).update(any(), any());

			Model dcat = readTurtleFromFile("dcat-view-valid.ttl");
			mockMvc.perform(put(DcatViewsRestController.BASE_URL, COLLECTION_NAME, VIEW_NAME)
					.content(writeToTurtle(dcat))
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isNotFound());

			verify(dcatViewService)
					.update(eq(new ViewName(COLLECTION_NAME, VIEW_NAME)), argThat(IsIsomorphic.with(dcat)));
		}
	}

	@Test
	void should_Return200_when_DeletedSuccessfully() throws Exception {
		mockMvc.perform(delete(DcatViewsRestController.BASE_URL, COLLECTION_NAME, VIEW_NAME))
				.andExpect(status().isOk());

		verify(dcatViewService).delete(new ViewName(COLLECTION_NAME, VIEW_NAME));
	}

	private Model readTurtleFromFile(String path) {
		return RDFParser.source(path).lang(Lang.TURTLE).build().toModel();
	}

	private String writeToTurtle(Model model) {
		return RDFWriter.source(model).lang(Lang.TURTLE).asString();
	}

}