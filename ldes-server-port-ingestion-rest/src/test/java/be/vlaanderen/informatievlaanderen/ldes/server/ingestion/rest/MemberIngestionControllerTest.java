package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.validation.LdesShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.config.IngestionWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.exceptions.MalformedMemberIdException;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles("test")
@ContextConfiguration(classes = { LdesMemberIngestionController.class,
		IngestionWebConfig.class, LdesConfig.class, LdesShaclValidator.class })
class MemberIngestionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberIngestService memberIngestService;

	@Autowired
	private LdesConfig ldesConfig;

	@ParameterizedTest(name = "Ingest an LDES member in the REST service usingContentType {0}")

	@ArgumentsSource(ContentTypeRdfFormatLangArgumentsProvider.class)
	void when_POSTRequestIsPerformed_LDesMemberIsSaved(String contentType, Lang rdfFormat) throws Exception {
		String ldesMemberString = readLdesMemberDataFromFile("example-ldes-member.nq", rdfFormat);

		mockMvc.perform(post("/mobility-hindrances").contentType(contentType).content(ldesMemberString))
				.andDo(print()).andExpect(status().isOk());
		verify(memberIngestService, times(1)).addMember(any());
	}

	@Test
	@DisplayName("Requesting using another collection name returns 404")
	void when_POSTRequestIsPerformedUsingAnotherCollectionName_ResponseIs404()
			throws Exception {
		String ldesMemberString = readLdesMemberDataFromFile("example-ldes-member.nq", Lang.NQUADS);

		mockMvc.perform(post("/another-collection-name")
				.contentType("application/n-quads")
				.content(ldesMemberString))
				.andDo(print()).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Post request with malformed RDF_SYNTAX_TYPE throws MalformedMemberException")
	void when_POSTRequestIsPerformedUsingMalformedRDF_SYNTAX_TYPE_ThrowMalformedMemberException() throws Exception {
		String ldesMemberString = readLdesMemberDataFromFile("example-ldes-member.nq", Lang.NQUADS);
		String ldesMemberType = ldesConfig.getMemberType();
		String ldesMemberStringWrongType = ldesMemberString.replace(ldesMemberType,
				ldesMemberType.substring(0, ldesMemberType.length() - 1));

		MockHttpServletRequestBuilder postRequest = post("/mobility-hindrances")
				.contentType("application/n-quads")
				.content(ldesMemberStringWrongType);
		Exception outerException = assertThrows(Exception.class, () -> mockMvc.perform(postRequest));
		Exception actualException = assertThrows(MalformedMemberIdException.class, () -> {
			throw outerException.getCause();
		});
		assertEquals(actualException.getMessage(), new MalformedMemberIdException(ldesMemberType).getMessage());
	}

	private String readLdesMemberDataFromFile(String fileName, Lang rdfFormat)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		String content = Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
		return RdfModelConverter.toString(RdfModelConverter.fromString(content,
				Lang.NQUADS), rdfFormat);
	}

	static class ContentTypeRdfFormatLangArgumentsProvider implements
			ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of("application/n-quads", Lang.NQUADS),
					Arguments.of("application/n-triples", Lang.NTRIPLES),
					Arguments.of("application/ld+json", Lang.JSONLD),
					Arguments.of("text/turtle", Lang.TURTLE),
					Arguments.of("application/rdf+json", Lang.RDFJSON),
					Arguments.of("application/trix+xml", Lang.TRIX),
					Arguments.of("text/n3", Lang.N3),
					Arguments.of("application/trig", Lang.TRIG),
					Arguments.of("application/n3", Lang.N3),
					Arguments.of("text/plain", Lang.NTRIPLES),
					Arguments.of("application/rdf+xml", Lang.RDFXML),
					Arguments.of("x/ld-json-11", Lang.JSONLD11),
					Arguments.of("x/ld-json-10", Lang.JSONLD10),
					Arguments.of("text/rdf+n3", Lang.N3),
					Arguments.of("application/trix", Lang.TRIX),
					Arguments.of("application/turtle", Lang.TURTLE),
					Arguments.of("text/trig", Lang.TRIG));
		}
	}
}
