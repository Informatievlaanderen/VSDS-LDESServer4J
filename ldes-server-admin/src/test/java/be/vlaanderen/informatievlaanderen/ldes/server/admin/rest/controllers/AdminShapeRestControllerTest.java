package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.services.ShaclShapeService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ShaclShapeValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.shacl.exceptions.MissingShaclShapeException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({ "test", "rest" })
@ContextConfiguration(classes = { AdminShapeRestController.class, HttpModelConverter.class,
		PrefixAdderImpl.class, AdminRestResponseEntityExceptionHandler.class })
class AdminShapeRestControllerTest {
	@MockBean
	private ShaclShapeService shaclShapeService;

	@SpyBean
	private ShaclShapeValidator shaclShapeValidator;

	@Autowired
	private MockMvc mockMvc;

	private String readDataFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		return Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
	}

	@Nested
	class GetRequest {
		@Test
		void when_ShapeIsPresentArePresent_Then_ShapeIsReturned() throws Exception {
			String collectionName = "name1";
			Model expectedShapeModel = readModelFromFile("shape-1.ttl");
			when(shaclShapeService.retrieveShaclShape(collectionName))
					.thenReturn(new ShaclShape(collectionName, expectedShapeModel));

			mockMvc.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/shape")
					.accept(contentTypeTurtle))
					.andExpect(status().isOk())
					.andExpect(IsIsomorphic.with(expectedShapeModel));
		}

		@Test
		void when_ViewNotPresent_Then_Returned404() throws Exception {
			String collectionName = "name1";
			when(shaclShapeService.retrieveShaclShape(collectionName))
					.thenThrow(new MissingShaclShapeException(collectionName));

			mockMvc.perform(get("/admin/api/v1/eventstreams/" + collectionName + "/shape")
					.accept(contentTypeTurtle))
					.andExpect(status().isNotFound());
		}
	}

	@Nested
	class PutRequest {
		@Test
		void when_ModelInRequestBody_Then_MethodIsCalled() throws Exception {
			String collectionName = "name1";
			String fileName = "shape-1.ttl";
			Model expectedShapeModel = readModelFromFile(fileName);

			mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/shape")
					.accept(contentTypeTurtle)
					.content(readDataFromFile(fileName))
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isOk());

			InOrder inOrder = inOrder(shaclShapeValidator, shaclShapeService);
			inOrder.verify(shaclShapeValidator, times(1)).validateShape(any());
			inOrder.verify(shaclShapeService, times(1))
					.updateShaclShape(new ShaclShape(collectionName, expectedShapeModel));
			inOrder.verifyNoMoreInteractions();
		}

		@Test
		void when_ModelWithoutType_Then_ReturnedBadRequest() throws Exception {
			String collectionName = "name1";
			mockMvc.perform(put("/admin/api/v1/eventstreams/" + collectionName + "/shape")
					.accept(contentTypeTurtle)
					.content(readDataFromFile("shape-without-type.ttl"))
					.contentType(Lang.TURTLE.getHeaderString()))
					.andExpect(status().isBadRequest());

			verify(shaclShapeValidator).validateShape(any());
		}

	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

	@TestConfiguration
	static class AdminShapeRestControllerTestConfig {
		@Bean
		public HttpModelConverter modelConverter() {
			return new HttpModelConverter(new PrefixAdderImpl());
		}
	}
}
