package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.MemberIngester;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters.MemberConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.IngestionRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.validators.CollectionNameValidator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles("test")
@ContextConfiguration(classes = { MemberConverter.class, MemberIngestController.class,
		IngestionRestResponseEntityExceptionHandler.class, RdfModelConverter.class, CollectionNameValidator.class})
class MemberIngestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberIngester memberIngester;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private CollectionNameValidator collectionNameValidator;

	@BeforeEach
	void setUp() {
		Stream.of(
						new EventStream("mobility-hindrances", "timestampPath", "versionOfPath"),
				new EventStream("restaurant", "timestampPath", "versionOfPath")
				).map(EventStreamCreatedEvent::new)
				.forEach(eventPublisher::publishEvent);
	}

	@ParameterizedTest(name = "Ingest an LDES member in the REST service usingContentType {0}")
	@ArgumentsSource(ContentTypeRdfFormatLangArgumentsProvider.class)
	void when_POSTRequestIsPerformed_LDesMemberIsSaved(String contentType, Lang rdfFormat) throws Exception {
		byte[] ldesMemberBytes = readLdesMemberDataFromFile("example-ldes-member.nq", rdfFormat);

		mockMvc.perform(post("/mobility-hindrances").contentType(contentType).content(ldesMemberBytes))
				.andExpect(status().isOk());
		verify(memberIngester, times(1)).ingest(any(Member.class));
	}

	@Test
	void when_POSTRequestIsPerformedWithoutMultipleNamedNodes_ThrowException() throws Exception {
		byte[] ldesMemberBytes = readLdesMemberDataFromFile("example-ldes-member-with-multiple-nodes.nq", Lang.NQUADS);

		mockMvc.perform(post("/mobility-hindrances")
						.contentType("application/n-quads")
						.content(ldesMemberBytes))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Member id could not be extracted from model")));
	}

	@Test
	void when_POSTRequestIsPerformed_LDesMemberIsSavedWithoutVersionOfAndTimestamp() throws Exception {
		byte[] ldesMemberBytes = readLdesMemberDataFromFile("example-ldes-member-without-version-of-timestamp.nq",
				Lang.NQUADS);

		mockMvc.perform(post("/mobility-hindrances").contentType("application/n-quads").content(ldesMemberBytes))
				.andExpect(status().isOk());
		verify(memberIngester, times(1)).ingest(any(Member.class));
	}

	@Test
	@DisplayName("Requesting using another collection name returns 404")
	void when_POSTRequestIsPerformedUsingAnotherCollectionName_ResponseIs404()
			throws Exception {
		byte[] ldesMemberBytes = readLdesMemberDataFromFile("example-ldes-member.nq", Lang.NQUADS);

		mockMvc.perform(post("/another-collection-name")
						.contentType("application/n-quads")
						.content(ldesMemberBytes))
				.andExpect(status().isNotFound());
	}

	@Test
	void when_memberConformToShapeIsIngested_then_status200IsReturned() throws Exception {
		String modelString = readModelStringFromFile("menu-items/example-data-old.ttl");

		mockMvc.perform(post("/restaurant").contentType("text/turtle").content(modelString))
				.andExpect(status().isOk());

		verify(memberIngester).ingest(any(Member.class));
	}

	@Test
	void whenIngestValidationExceptionIsThrown_thenStatus400IsReturned() throws Exception {
		String modelString = readModelStringFromFile("menu-items/example-data-old.ttl");
		doThrow(ShaclValidationException.class).when(memberIngester).ingest(any(Member.class));

		mockMvc.perform(post("/restaurant").contentType("text/turtle").content(modelString))
				.andExpect(status().isBadRequest());
	}

	private byte[] readLdesMemberDataFromFile(String fileName, Lang rdfFormat) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		RDFWriter.source(RDFParser.source(fileName).lang(Lang.NQUADS).toModel()).lang(rdfFormat).output(outputStream);
		return outputStream.toByteArray();
	}

	private String readModelStringFromFile(String fileName) throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();

		return Files.readString(Paths.get(uri));
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
					Arguments.of("text/trig", Lang.TRIG),
					Arguments.of("application/rdf+protobuf", Lang.RDFPROTO),
					Arguments.of("application/rdf+thrift", Lang.RDFTHRIFT));
		}
	}

}
