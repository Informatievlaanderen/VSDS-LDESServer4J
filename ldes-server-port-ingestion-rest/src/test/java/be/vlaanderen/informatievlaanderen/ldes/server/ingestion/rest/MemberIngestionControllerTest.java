package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesconfig.services.LdesConfigModelService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services.MemberIngestService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.config.IngestionWebConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.exceptionhandling.IngestionRestResponseEntityExceptionHandler;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ActiveProfiles("test")
@ContextConfiguration(classes = { LdesMemberIngestionController.class,
		IngestionWebConfig.class, AppConfig.class, IngestionRestResponseEntityExceptionHandler.class })
class MemberIngestionControllerTest {
	private final static String RESTAURANT = "restaurant";

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MemberIngestService memberIngestService;

	@MockBean
	private LdesConfigModelService ldesConfigModelService;

	@Autowired
	private AppConfig appConfig;

	@ParameterizedTest(name = "Ingest an LDES member in the REST service usingContentType {0}")
	@ArgumentsSource(ContentTypeRdfFormatLangArgumentsProvider.class)
	void when_POSTRequestIsPerformed_LDesMemberIsSaved(String contentType, Lang rdfFormat) throws Exception {
		String ldesMemberString = readLdesMemberDataFromFile("example-ldes-member.nq", rdfFormat);

		mockMvc.perform(post("/mobility-hindrances").contentType(contentType).content(ldesMemberString))
				.andDo(print()).andExpect(status().isOk());
		verify(memberIngestService, times(1)).addMember(any(Member.class));
	}

	@Test
	void when_POSTRequestIsPerformedWithoutMemberId_ThrowMalformedMemberException() throws Exception {
		String ldesMemberString = readLdesMemberDataFromFile("example-ldes-member-without-id.nq", Lang.NQUADS);

		mockMvc.perform(post("/mobility-hindrances")
				.contentType("application/n-quads")
				.content(ldesMemberString))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(
						"Member id could not be extracted. MemberType https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder could not be found in listStatements."));
	}

	@Test
	void when_POSTRequestIsPerformed_LDesMemberIsSavedWithoutVersionOfAndTimestamp() throws Exception {
		String ldesMemberString = readLdesMemberDataFromFile("example-ldes-member-without-version-of-timestamp.nq",
				Lang.NQUADS);

		mockMvc.perform(post("/mobility-hindrances").contentType("application/n-quads").content(ldesMemberString))
				.andDo(print()).andExpect(status().isOk());
		verify(memberIngestService, times(1)).addMember(any(Member.class));
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
		String ldesMemberType = appConfig.getCollections().get(0).getMemberType();
		String ldesMemberStringWrongType = ldesMemberString.replace(ldesMemberType,
				ldesMemberType.substring(0, ldesMemberType.length() - 1));

		mockMvc.perform(post("/mobility-hindrances")
				.contentType("application/n-quads")
				.content(ldesMemberStringWrongType))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(
						"Member id could not be extracted. MemberType https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder could not be found in listStatements."));
	}

	@Test
	void when_memberConformToShapeIsIngested_then_status200IsReturned() throws Exception {
		Model oldShape = readModelFromFile("alternatives/example-shape-old.ttl", Lang.TURTLE);

		publisher.publishEvent(new ShaclChangedEvent(RESTAURANT, oldShape));

		String modelString = readModelStringFromFile("alternatives/example-data-old.ttl");

		mockMvc.perform(post("/restaurant").contentType("text/turtle").content(modelString))
				.andDo(print())
				.andExpect(status().isOk());

		verify(memberIngestService).addMember(any(Member.class));
	}

	@Test
	void when_memberNotConformToShapeIsIngested_then_status400IsReturned() throws Exception {
		Model oldShape = readModelFromFile("alternatives/example-shape-old.ttl", Lang.TURTLE);

		publisher.publishEvent(new ShaclChangedEvent(RESTAURANT, oldShape));

		String modelString = readModelStringFromFile("alternatives/example-data-new.ttl");

		mockMvc.perform(post("/restaurant").contentType("text/turtle").content(modelString))
				.andDo(print())
				.andExpect(status().isBadRequest());

		verifyNoInteractions(memberIngestService);
	}

	@Test
	void when_shapeIsUpdated_and_membersConformToShapeIsIngested_then_status200IsReturned() throws Exception {
		Model oldShape = readModelFromFile("alternatives/example-shape-old.ttl", Lang.TURTLE);
		Model newShape = readModelFromFile("alternatives/example-shape-new.ttl", Lang.TURTLE);

		publisher.publishEvent(new ShaclChangedEvent(RESTAURANT, oldShape)); // init server with old shape
		publisher.publishEvent(new ShaclChangedEvent(RESTAURANT, newShape)); // update the server with new shape

		String modelString = readModelStringFromFile("alternatives/example-data-new.ttl");

		mockMvc.perform(post("/restaurant").contentType("text/turtle").content(modelString))
				.andDo(print())
				.andExpect(status().isOk());

		verify(memberIngestService).addMember(any(Member.class));
	}

	private String readLdesMemberDataFromFile(String fileName, Lang rdfFormat)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		String content = Files.lines(Paths.get(file.toURI())).collect(Collectors.joining("\n"));
		return RdfModelConverter.toString(RdfModelConverter.fromString(content,
				Lang.NQUADS), rdfFormat);
	}

	private Model readModelFromFile(String fileName, Lang lang) throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();

		return RDFParserBuilder.create()
				.fromString(Files.lines(Paths.get(uri)).collect(Collectors.joining())).lang(lang)
				.toModel();
	}

	private String readModelStringFromFile(String fileName) throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();

		return Files.lines(Paths.get(uri)).collect(Collectors.joining());
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
