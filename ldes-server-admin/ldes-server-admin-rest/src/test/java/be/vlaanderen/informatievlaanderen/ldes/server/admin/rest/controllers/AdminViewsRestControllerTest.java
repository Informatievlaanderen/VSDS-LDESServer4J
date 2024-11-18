package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ModelValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.validation.ValidatorsConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.exception.DuplicateRetentionException;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.view.service.ViewService;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ListViewHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters.ViewHttpConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.exceptionhandling.AdminRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.FragmentationConfigExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.RetentionModelExtractor;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.ViewSpecificationConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.HttpModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.PrefixAdderImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.encodig.CharsetEncodingConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.HostNamePrefixConstructorConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.rest.RelativeUriPrefixConstructor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.jena.riot.WebContent.contentTypeNQuads;
import static org.apache.jena.riot.WebContent.contentTypeTurtle;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles({"test", "rest"})
@ContextConfiguration(classes = {AdminViewsRestController.class, CharsetEncodingConfig.class, PrefixAdderImpl.class,
		HttpModelConverter.class, ViewHttpConverter.class, ListViewHttpConverter.class,
		ViewSpecificationConverter.class, ValidatorsConfig.class, RdfModelConverter.class,
		AdminRestResponseEntityExceptionHandler.class, RetentionModelExtractor.class,
		FragmentationConfigExtractor.class, HostNamePrefixConstructorConfig.class, RelativeUriPrefixConstructor.class})
class AdminViewsRestControllerTest {
	private static final String COLLECTION_NAME = "name1";
	private static final String VIEW_NAME = "view1";
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
		Model expectedViewModel1 = RDFDataMgr.loadModel("view/view.ttl");
		ViewSpecification view1 = converter.viewFromModel(expectedViewModel1, COLLECTION_NAME);
		Model expectedViewModel2 = RDFDataMgr.loadModel("view/another-view.ttl");
		ViewSpecification view2 = converter.viewFromModel(expectedViewModel2, COLLECTION_NAME);
		when(viewService.getViewsByCollectionName(COLLECTION_NAME)).thenReturn(List.of(view1, view2));

		mockMvc.perform(get("/admin/api/v1/eventstreams/{collectionName}/views", COLLECTION_NAME)
						.accept(contentTypeNQuads))
				.andExpect(status().isOk())
				.andExpect(content().encoding(StandardCharsets.UTF_8))
				.andExpect(content().contentTypeCompatibleWith(contentTypeNQuads))
				.andExpect(IsIsomorphic.with(expectedViewModel1.add(expectedViewModel2)));
	}

	@Test
	void when_StreamAndViewArePresent_Then_ViewIsReturned() throws Exception {
		Model expectedViewModel = RDFDataMgr.loadModel("view/view.ttl");
		ViewSpecification view = converter.viewFromModel(expectedViewModel, COLLECTION_NAME);
		when(viewService.getViewByViewName(new ViewName(COLLECTION_NAME, VIEW_NAME))).thenReturn(view);

		mockMvc.perform(get("/admin/api/v1/eventstreams/{collectionName}/views/{viewName}", COLLECTION_NAME, VIEW_NAME)
						.accept(contentTypeTurtle))
				.andExpect(status().isOk())
				.andExpect(content().encoding(StandardCharsets.UTF_8))
				.andExpect(content().contentTypeCompatibleWith(contentTypeTurtle))
				.andExpect(IsIsomorphic.with(expectedViewModel));
	}

	@Test
	void when_ViewNotPresent_Then_Returned404() throws Exception {
		when(viewService.getViewByViewName(new ViewName(COLLECTION_NAME, VIEW_NAME)))
				.thenThrow(new MissingResourceException("view", "%s/%s".formatted(COLLECTION_NAME, VIEW_NAME)));

		mockMvc.perform(get("/admin/api/v1/eventstreams/{collectionName}/views/{viewName}", COLLECTION_NAME, VIEW_NAME)
						.accept(contentTypeTurtle))
				.andExpect(status().isNotFound())
				.andExpect(content().encoding(StandardCharsets.UTF_8))
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
				.andExpect(content().string("Resource of type: view with id: %s/%s could not be found.".formatted(COLLECTION_NAME, VIEW_NAME)));

	}

	@Test
	void given_ValidView_when_PostView_then_Return201() throws Exception {
		Model expectedViewModel = RDFDataMgr.loadModel("view/view.ttl");
		ViewSpecification view = converter.viewFromModel(expectedViewModel, COLLECTION_NAME);

		mockMvc.perform(post("/admin/api/v1/eventstreams/{collectionName}/views", COLLECTION_NAME)
						.content(readDataFromFile("view/view.ttl"))
						.contentType(contentTypeTurtle))
				.andExpect(status().isCreated())
				.andExpect(content().bytes(new byte[]{}));
		verify(viewService).addView(view);
	}

	@Test
	void given_ViewWithDuplicateRetentionPolicy_when_PostView_then_Return400() throws Exception {
		Model viewModel = RDFDataMgr.loadModel("view/view-with-duplicate-retention.ttl");
		ViewSpecification view = converter.viewFromModel(viewModel, COLLECTION_NAME);

		doThrow(new DuplicateRetentionException()).when(viewService).addView(view);

		mockMvc.perform(post("/admin/api/v1/eventstreams/{collectionName}/views", COLLECTION_NAME)
						.content(RDFWriter.source(viewModel).lang(Lang.NQUADS).asString())
						.contentType(contentTypeNQuads))
				.andExpect(status().isBadRequest());

		verify(viewService).addView(view);
	}

	@Test
	void when_PostView_then_ViewIsValidated() throws Exception {
		mockMvc.perform(post("/admin/api/v1/eventstreams/{collectionName}/views", COLLECTION_NAME)
				.content(readDataFromFile("view/view.ttl"))
				.contentType(contentTypeTurtle));
		verify(validator).validate(any(), any());
	}

	@Test
	void when_DeleteView_Then_Return200() throws Exception {
		mockMvc.perform(delete("/admin/api/v1/eventstreams/{collectionName}/views/{viewName}", COLLECTION_NAME, VIEW_NAME)).andExpect(status().isOk());
		verify(viewService).deleteViewByViewName(new ViewName(COLLECTION_NAME, VIEW_NAME));
	}

	private String readDataFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		return Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
	}

}
