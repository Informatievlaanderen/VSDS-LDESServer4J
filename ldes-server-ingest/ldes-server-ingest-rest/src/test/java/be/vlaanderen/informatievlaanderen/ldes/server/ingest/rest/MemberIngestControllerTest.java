package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.VersionCreationProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.versioning.VersionHeaderControllerAdvice;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.MemberIngester;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.exceptions.MemberSubjectNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters.IngestedModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.IngestionRestResponseEntityExceptionHandler;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.ingestreportvalidator.BlankNodesValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.ingestreportvalidator.PathsValidator;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validators.memberingestvalidator.MemberIngestValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
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
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
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
import java.util.Properties;
import java.util.stream.Stream;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {IngestedModelConverter.class, MemberIngestController.class,
        IngestionRestResponseEntityExceptionHandler.class, RdfModelConverter.class, MemberIngestValidator.class,
        BlankNodesValidator.class, PathsValidator.class, VersionHeaderControllerAdvice.class,
        MemberIngestControllerTest.VersionConfig.class})
class MemberIngestControllerTest {
    private static final String VERSION = "4.0.4-SNAPSHOT";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberIngester memberIngester;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        Stream.of(
                        new EventStream("mobility-hindrances", "http://www.w3.org/ns/prov#generatedAtTime", "http://purl.org/dc/terms/isVersionOf", VersionCreationProperties.disabled()),
                        new EventStream("restaurant", "http://www.w3.org/ns/prov#generatedAtTime", "https://vocabulary.uncefact.org/elementVersionId", VersionCreationProperties.disabled()))
                .map(EventStreamCreatedEvent::new)
                .forEach(eventPublisher::publishEvent);
    }

    @ParameterizedTest(name = "Ingest an LDES member in the REST service usingContentType {0}")
    @ArgumentsSource(ContentTypeRdfFormatLangArgumentsProvider.class)
    void when_POSTRequestIsPerformed_LDesMemberIsSaved(String contentType, Lang rdfFormat) throws Exception {
        byte[] ldesMemberBytes = readLdesMemberDataFromFile("example-ldes-member.nq", rdfFormat);

        mockMvc.perform(post("/mobility-hindrances").contentType(contentType).content(ldesMemberBytes))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-App-Version"));
        verify(memberIngester, times(1)).ingest(anyString(), any(Model.class));
    }

    @Test
    void when_POSTRequestIsPerformedWithMultipleNamedNodes_ThrowException() throws Exception {
        byte[] ldesMemberBytes = readLdesMemberDataFromFile("example-ldes-member-with-multiple-nodes.nq", Lang.NQUADS);

        when(memberIngester.ingest(eq("mobility-hindrances"), any()))
                .thenThrow(new MemberSubjectNotFoundException(ModelFactory.createDefaultModel()));

        mockMvc.perform(post("/mobility-hindrances")
                        .contentType("application/n-quads")
                        .content(ldesMemberBytes))
                .andExpect(status().isBadRequest())
		        .andExpect(header().exists("X-App-Version"))
		        .andExpect(content().string(containsString("Only 1 member is allowed per request on collection with version creation disabled")));
    }

    @Test
    void when_POSTRequestIsPerformedWithoutTimestampPath_ThrowException() throws Exception {
        byte[] ldesMemberBytes = readLdesMemberDataFromFile("example-ldes-member-without-timestamp.nq",
                Lang.NQUADS);

        mockMvc.perform(post("/mobility-hindrances").contentType("application/n-quads").content(ldesMemberBytes))
                .andExpect(status().isBadRequest())
		        .andExpect(header().exists("X-App-Version"))
                .andExpect(content().string(containsString("Member must have exactly 1 statement with timestamp path: http://www.w3.org/ns/prov#generatedAtTime")));
        verifyNoInteractions(memberIngester);
    }

    @Test
    void when_POSTRequestIsPerformed_WithoutVersionOf_ThenTheRequestFails() throws Exception {
        byte[] ldesMemberBytes = readLdesMemberDataFromFile("example-ldes-member-without-version-of.nq",
                Lang.NQUADS);

        when(memberIngester.ingest(eq("mobility-hindrances"), any()))
                .thenThrow(new MemberSubjectNotFoundException(ModelFactory.createDefaultModel()));

        mockMvc.perform(post("/mobility-hindrances").contentType("application/n-quads").content(ldesMemberBytes))
                .andExpect(status().isBadRequest())
		        .andExpect(header().exists("X-App-Version"))
		        .andExpect(content().string(containsString("Member must have exactly 1 statement with versionOf path: http://purl.org/dc/terms/isVersionOf")));
    }

    @Test
    @DisplayName("Requesting using another collection name returns 404")
    void when_POSTRequestIsPerformedUsingAnotherCollectionName_ResponseIs404()
            throws Exception {
        byte[] ldesMemberBytes = readLdesMemberDataFromFile("example-ldes-member.nq", Lang.NQUADS);

        when(memberIngester.ingest(eq("another-collection-name"), any()))
                .thenThrow(MissingResourceException.class);

        mockMvc.perform(post("/another-collection-name")
                        .contentType("application/n-quads")
                        .content(ldesMemberBytes))
                .andExpect(status().isNotFound())
		        .andExpect(header().exists("X-App-Version"));
    }

    @Test
    void when_memberConformToShapeIsIngested_then_status200IsReturned() throws Exception {
        String modelString = readModelStringFromFile("menu-items/example-data-old.ttl");

        mockMvc.perform(post("/restaurant").contentType("text/turtle").content(modelString))
                .andExpect(status().isOk())
		        .andExpect(header().exists("X-App-Version"));

        verify(memberIngester).ingest(anyString(), any(Model.class));
    }

    @Test
    void whenIngestValidationExceptionIsThrown_thenStatus400IsReturned() throws Exception {
        String modelString = readModelStringFromFile("menu-items/example-data-old.ttl");
        doThrow(new ShaclValidationException("", ModelFactory.createDefaultModel())).when(memberIngester).ingest(anyString(), any(Model.class));

        mockMvc.perform(post("/restaurant").contentType("text/turtle").content(modelString))
                .andExpect(status().isBadRequest())
		        .andExpect(header().exists("X-App-Version"));
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
                    Arguments.of("text/rdf+n3", Lang.N3),
                    Arguments.of("application/trix", Lang.TRIX),
                    Arguments.of("text/turtle", Lang.TURTLE),
                    Arguments.of("text/trig", Lang.TRIG),
                    Arguments.of("application/rdf+protobuf", Lang.RDFPROTO),
                    Arguments.of("application/rdf+thrift", Lang.RDFTHRIFT));
        }
    }

    @TestConfiguration
    static class VersionConfig {
        @Bean
        public BuildProperties buildProperties() {
            final Properties properties = new Properties();
            properties.setProperty("version", VERSION);
            return new BuildProperties(properties);
        }
    }
}
