package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.resultactionsextensions.ResponseToModelConverter;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.apache.jena.vocabulary.RDF;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.awaitility.Awaitility;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Flux;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_REMAINING_ITEMS;
import static be.vlaanderen.informatievlaanderen.ldes.server.maintenance.batch.MaintenanceFlows.MAINTENANCE_JOB;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LdesServerSteps extends LdesServerIntegrationTest {

    public static final String ACTUATOR_PROMETHEUS = "/actuator/prometheus";
    private final Stack<String> interactedStreams = new Stack<>();
    private String responseContentType;
    private String responseBody;
    @Autowired
    private WebApplicationContext wac;
    private WebTestClient client;
    private int lastStatusCode;
    private Model responseModel;

    @Before("@setupStreaming")
    public void setUp() {
        client = MockMvcWebTestClient.bindToApplicationContext(this.wac).build();
    }

    @Before("@clearRegistry")
    public void clearRegistry() {
        Metrics.globalRegistry.getMeters().forEach(meter -> {
            if (meter instanceof Counter) {
                Metrics.globalRegistry.remove(meter);
            }
        });
        lastStatusCode = 0;
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.[SSS]'Z'"));
    }

    private Model getResponseAsModel(String url, String acceptedContentTypes) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(url).accept(acceptedContentTypes))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        this.responseBody = response.getContentAsString();
        this.responseContentType = response.getContentType();
        return RDFParser.create().fromString(this.responseBody).lang(RDFLanguages.contentTypeToLang(this.responseContentType.split(";")[0])).toModel();
    }

    @When("I ingest {int} members to the collection {string}")
    public void iIngestMembersToTheCollection(int numberOfMembers, String collectionName) throws Exception {
        for (int i = 0; i < numberOfMembers; i++) {
            String member = readMemberTemplate("data/input/members/mob-hind.template.ttl")
                    .replace("ID", String.valueOf(i))
                    .replace("DATETIME", "2023-04-06T09:58:15.867Z");
            mockMvc.perform(post("/" + collectionName)
                            .contentType("text/turtle")
                            .content(member))
                    .andExpect(status().is2xxSuccessful());
        }
    }

    @When("I ingest {int} members of template {string} to the collection {string}")
    public void iIngestMembersToTheCollection(int numberOfMembers, String memberTemplate, String collectionName)
            throws Exception {
        final String memberContentTemplate = readMemberTemplate(memberTemplate);
        for (int i = 0; i < numberOfMembers; i++) {
            String memberContent = memberContentTemplate
                    .replace("ID", String.valueOf(i))
                    .replace("DATETIME", getCurrentTimestamp());
            mockMvc.perform(post("/" + collectionName)
                            .contentType("text/turtle")
                            .content(memberContent))
                    .andExpect(status().is2xxSuccessful());
        }
    }

    private String readMemberTemplate(String fileName) throws IOException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.lines(path).collect(Collectors.joining());
    }

    private Model readModelFromFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI().toString();
        return RDFDataMgr.loadModel(uri);
    }

    @Then("I can fetch the TreeNode {string} and it contains {int} members")
    public void iCanFetchTheTreeNodeAndItContainsMembers(String url, int expectedNumberOfMembers) {
        await()
                .atMost(60, SECONDS)
                .untilAsserted(() -> {
                    responseModel = fetchFragment(url);
                    assertNotNull(responseModel);
                    assertEquals(expectedNumberOfMembers, responseModel.listObjectsOfProperty(TREE_MEMBER).toList().size());
                });
    }

    @And("The expected response is equal to {string}")
    public void theExpectedResponseIsEqualTo(String expectedOutputFile) throws URISyntaxException {
        Model expectedModel = stripGeneratedAtTimeOfModel(readModelFromFile(expectedOutputFile));
        Model actualModel = stripGeneratedAtTimeOfModel(responseModel);
        assertTrue(actualModel.isIsomorphicWith(expectedModel));
    }

    private Model stripGeneratedAtTimeOfModel(Model model) {
        return model.remove(
                model.listStatements(null, model.createProperty("http://www.w3.org/ns/prov#generatedAtTime"),
                        (Resource) null));
    }

    @When("I ingest the data described in {string} the collection {string}")
    public void iIngestTheMemberDescribedInTheCollection(String memberFileName, String collectionName)
            throws Exception {
        String member = readBodyFromFile(memberFileName);
        ContentType contentType = RDFLanguages.guessContentType(memberFileName);
        mockMvc.perform(post("/" + collectionName)
                        .contentType(contentType.getContentTypeStr())
                        .content(member))
                .andExpect(status().is2xxSuccessful())
                .andDo(result -> lastStatusCode = result.getResponse().getStatus());
    }

    @Then("The returned status code is {int}")
    public void checkStatusCode(int statusCode) {
        assertThat(lastStatusCode).isEqualTo(statusCode);
    }

    private String readBodyFromFile(String fileName) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URI uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();
        return Files.lines(Paths.get(uri)).collect(Collectors.joining("\n"));
    }

    @Given("^I create the eventstream ([^ ]+)")
    public void iCreateTheEventstreamEventStreamDescription(String eventStreamDescriptionFile) throws Exception {
        String eventStreamDescriptionFileSanitized = eventStreamDescriptionFile.replace("\"", "");
        String eventstream = readBodyFromFile(eventStreamDescriptionFileSanitized)
                .replace("CURRENTTIME", getCurrentTimestamp());

        final String content = mockMvc.perform(post("/admin/api/v1/eventstreams")
                        .contentType(RDFLanguages.guessContentType(eventStreamDescriptionFileSanitized).getContentTypeStr())
                        .content(eventstream))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String eventStreamName = RDFParser.create().fromString(content).lang(Lang.TURTLE)
                .toModel()
                .listStatements(null, RDF.type, createResource("https://w3id.org/ldes#EventStream"))
                .next()
                .getSubject()
                .getLocalName();

        interactedStreams.push(eventStreamName);
    }

    @Then("^I create the view ([^ ]+)")
    public void iCreateTheViewViewDescriptionFile(String viewDescriptionFile) throws Exception {
        String viewDescriptionFileSanitized = viewDescriptionFile.replace("\"", "");
        String view = readBodyFromFile(viewDescriptionFileSanitized)
                .replace("CURRENTTIME", getCurrentTimestamp());

        mockMvc.perform(post("/admin/api/v1/eventstreams/%s/views".formatted(interactedStreams.getFirst()))
                        .contentType(RDFLanguages.guessContentType(viewDescriptionFileSanitized).getContentTypeStr())
                        .content(view))
                .andExpect(status().isCreated());
    }

    @Then("^I delete the eventstream ([^ ]+)")
    public void iDeleteTheEventstreamCollectionName(String eventStreamName) throws Exception {
        String eventStreamNameSanitized = eventStreamName.replace("\"", "");
        mockMvc.perform(delete("/admin/api/v1/eventstreams/" + eventStreamNameSanitized))
                .andExpect(status().isOk());
    }

    @Then("^I can fetch the TreeNode ([^ ]+) using accept content-types (.+)")
    public void iCanFetchTheTreeNodeTreeNodeUrlUsingAcceptContentTypesAcceptContentTypes(String treeNodeUrl, String acceptContentTypes)
            throws Exception {
        await().atMost(Duration.ofSeconds(4));
        assertFalse(getResponseAsModel(treeNodeUrl.replace("\"", ""), acceptContentTypes.replace("\"", "")).listStatements().toList().isEmpty());
    }

    @Then("^The content-type of the response header is (.+)")
    public void theContentTypeOfTheResponseIsContentType(String contentType) throws Exception {
        assertThat(responseContentType).startsWith(contentType.replace("\"", ""));
    }

    @And("^The content-type of the response content is (.+)")
    public void theContentTypeOfTheResponseContentIsContentType(String contentType) throws Exception {
        assertThat(responseContentType).isEqualTo(contentType.replace("\"", ""));
    }

    @Then("The response from requesting the url {string} has access control headers, vary header and an etag")
    public void theResponseFromRequestingTheUrlHasAccessControlHeadersAndAnEtag(String url) {
        await()
                .atMost(Duration.of(20, ChronoUnit.SECONDS))
                .untilAsserted(() -> {
                    mockMvc.perform(get(url).accept("text/turtle")
                                    .header("Access-Control-Request-Method", "GET")
                                    .header("Origin", "http://www.someurl.com"))
                            .andExpect(status().isOk())
                            .andExpect(header().exists("Etag"))
                            .andExpect(header().stringValues("Vary", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Accept"))
                            .andExpect(header().string("Access-Control-Allow-Origin", "*"));
                });
    }

    @Then("the first fragment of the {string} view in collection {string} contains {long} members")
    public void firstFragmentOfViewContainsMembers(String view, String collection, long expectedMemberCount)
            throws Exception {
        // Get only relation from view
        Awaitility.await()
                .until(() -> fetchFragment("/%s/%s".formatted(collection, view))
                        .listObjectsOfProperty(createProperty("https://w3id.org/tree#node")).hasNext());

        String fragmentUrl = fetchFragment("/%s/%s".formatted(collection, view))
                .listObjectsOfProperty(createProperty("https://w3id.org/tree#node")).next().toString();


        await().atMost(60, SECONDS)
                .until(() -> {
                    Model fragmentPage = fetchFragment(fragmentUrl);

                    return fragmentPage.listObjectsOfProperty(createProperty("https://w3id.org/tree#member"))
                            .toList().size() == expectedMemberCount;
                });
    }

    @And("the LDES {string} contains {int} members")
    public void theLDESContainsMembers(String collection, int expectedMemberCount) {
        await().atMost(60, SECONDS)
                .until(() -> jdbcTemplate
                        .queryForObject("SELECT COUNT(*) FROM members m JOIN collections c ON m.collection_id = c.collection_id WHERE c.name = ?",
                                Integer.class, collection)
                        == expectedMemberCount);
    }

    @After
    public void cleanup() {
        interactedStreams.forEach(eventStream -> {
            try {
                mockMvc.perform(delete("/admin/api/v1/eventstreams/" + eventStream));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    @And("The response from requesting the url {string} contains {long} remaining items statements")
    public void theResponseFromRequestingTheUrlDoesContainRemainingitemsStatement(String url, long statementCount) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(url).accept("text/turtle")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://www.someurl.com"))
                .andExpect(status().isOk()).andReturn().getResponse();
        Model contentAsString = RDFParser.create().fromString(response.getContentAsString()).lang(Lang.TTL).toModel();
        long size = contentAsString.listObjectsOfProperty(createProperty(TREE_REMAINING_ITEMS))
                .toList().size();
        assertThat(size).isEqualTo(statementCount);
    }

    @When("I ingest {int} files of state objects from folder {string} to the collection {string}")
    public void iIngestFilesOfStateObjectsFromFolderToTheCollection(int numberOfStateFiles, String folderName, String collectionName) throws Exception {
        for (int i = 0; i < numberOfStateFiles; i++) {
            Model model = RDFParser.source("%s/%d.ttl".formatted(folderName, i + 1))
                    .lang(Lang.TURTLE)
                    .toModel();
            mockMvc.perform(post("/" + collectionName)
                            .contentType("text/turtle")
                            .content(RDFWriter.source(model).lang(Lang.TURTLE).asString()))
                    .andExpect(status().is2xxSuccessful());
        }
    }


    @When("I fetch a fragment from url {string} in a streaming way and is equal to the model of {string}")
    public void iFetchAStreamingFragment(String url, String compareUrl) {
        await().atMost(60, SECONDS)
                .untilAsserted(() -> {
                    FluxExchangeResult<String> response = client.get()
                            .uri(url)
                            .accept(MediaType.TEXT_EVENT_STREAM)
                            .exchange()
                            .expectStatus()
                            .isOk()
                            .returnResult(String.class);

                    Flux<String> eventFlux = response.getResponseBody();
                    responseModel = ModelFactory.createDefaultModel();

                    eventFlux.toStream().forEach(responseText -> {
                        InputStream decoded = new ByteArrayInputStream(Base64.getDecoder().decode(responseText));
                        Model eventModel = RDFParser
                                .source(decoded)
                                .lang(Lang.RDFPROTO)
                                .toModel();
                        responseModel.add(eventModel);
                    });

                    assertTrue(responseModel.isIsomorphicWith(getResponseAsModel(compareUrl, Lang.TURTLE.getHeaderString())));
                });
    }

    @When("I close the collection {string}")
    public void iCloseTheEventstream(String collection) throws Exception {
        mockMvc.perform(post("/admin/api/v1/eventstreams/{collection}/close", collection))
                .andExpect(status().is2xxSuccessful());
    }

    private Model fetchFragment(String path) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(new URI(path))
                        .accept("text/turtle"))
                .andReturn()
                .getResponse();
        if (response.getStatus() == 404) {
            return ModelFactory.createDefaultModel();
        }
        return new ResponseToModelConverter(response).convert();
    }

    @And("The prometheus value for key {string} in my collection {string} is {string}")
    public void theResponseFromRequestingTheUrlDoesContainAJsonFile(String message, String collection, String value) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get(ACTUATOR_PROMETHEUS).accept("application/openmetrics-text"))
                .andReturn().getResponse();
        assertTrue(response.getContentAsString().contains("%s{collection=\"%s\"} %s".formatted(message, collection, value)));
    }

    @And("the background processes did not fail")
    public void theBackgroundProcessesDidNotFail() {
        assertThat(jobExplorer.getLastJobInstance(MAINTENANCE_JOB))
                .isNotNull()
                .extracting(jobExplorer::getLastJobExecution)
                .isNotNull()
                .extracting(JobExecution::getStepExecutions, InstanceOfAssertFactories.list(StepExecution.class))
                .map(StepExecution::getStatus)
                .doesNotContain(BatchStatus.FAILED);
    }

    @And("the batch tables has been cleaned")
    public void theBatchTablesHasBeenCleaned() throws NoSuchJobException {
        final JobInstance lastJobInstance = jobExplorer.getLastJobInstance(MAINTENANCE_JOB);
        assertThat(lastJobInstance).isNotNull();
        await()
                .untilAsserted(() -> assertThat(jobExplorer.getLastJobExecution(lastJobInstance))
                        .isNotNull()
                        .extracting(JobExecution::isRunning, InstanceOfAssertFactories.BOOLEAN)
                        .isFalse());
        assertThat(jobExplorer.getJobInstanceCount(MAINTENANCE_JOB)).isEqualTo(1);
        assertThat(jobExplorer.getJobNames())
                .doesNotContain("fragmentation")
                .hasSize(1);
    }
}
