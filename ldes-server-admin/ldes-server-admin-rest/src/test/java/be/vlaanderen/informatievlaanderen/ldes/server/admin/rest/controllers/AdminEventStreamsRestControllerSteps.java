package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.SpringIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.spi.EventStreamTO;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.*;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.mockito.InOrder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminEventStreamsRestControllerSteps extends SpringIntegrationTest {
	private static final String COLLECTION = "name1";
	private static final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
	private static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
	private static final VersionCreationProperties VERSION_DELIMITER = VersionCreationProperties.disabled();
	private ResultActions resultActions;

	@After
	public void cleanup() {
		resourceRemover.removeUsedResources();
	}

	@Given("a db containing multiple eventstreams")
	public void aDbContainingMultipleEventstreams() throws URISyntaxException {
		final String collection2 = "name2";
		final EventStream eventStream = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_DELIMITER);
		final EventStream eventStream2 = new EventStream(collection2, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_DELIMITER);
		eventPublisher.publishEvent(new EventStreamCreatedEvent(eventStream));
		eventPublisher.publishEvent(new EventStreamCreatedEvent(eventStream2));
		Model shape1 = readModelFromFile("shacl/shape-name1.ttl");
		Model shape2 = readModelFromFile("shacl/shape-name2.ttl");
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName("ExampleFragmentation");
		fragmentationConfig.setConfig(Map.of("property", "ldes:propertyPath"));
		ViewSpecification singleView = new ViewSpecification(
				new ViewName(collection2, "view1"),
				List.of(),
				List.of(fragmentationConfig), 100);
		List<ViewSpecification> views = List.of(
				new ViewSpecification(
						new ViewName(COLLECTION, "view2"),
						List.of(),
						List.of(fragmentationConfig), 100),
				new ViewSpecification(
						new ViewName(COLLECTION, "view3"),
						List.of(),
						List.of(fragmentationConfig), 100));

		when(eventStreamRepository.retrieveAllEventStreamTOs()).thenReturn(List.of(
				new EventStreamTO.Builder().withEventStream(eventStream).withViews(views).withShacl(shape1).build(),
				new EventStreamTO.Builder().withEventStream(eventStream2).withViews(List.of(singleView)).withShacl(shape2).build()
		));
	}

	@When("the client calls {string}")
	public void theClientCallsAdminApiVEventStreams(String url) throws Exception {
		resultActions = mockMvc.perform(get(url).accept(MediaType.valueOf("text/turtle")));
	}

	@Then("the client receives HTTP status {int}")
	public void theClientReceivesHTTPStatus(int status) throws Exception {
		resultActions.andExpect(status().is(status));
	}

	@And("the client receives a valid list of event streams")
	public void theClientReceivesAValidListOfEventStreams() throws Exception {
		Model expectedModel = readModelFromFile("eventstream/streams/multiple-ldes.ttl");
		resultActions.andExpect(IsIsomorphic.with(expectedModel));
	}

	@Given("a db containing one event stream")
	public void aDbContainingOneEventStream() throws URISyntaxException {
		final EventStream eventStream = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, VERSION_DELIMITER);
		Model shape = readModelFromFile("shacl/server-shape.ttl");
		final EventStreamTO eventStreamTO = new EventStreamTO.Builder().withEventStream(eventStream).withShacl(shape).build();
		when(eventStreamRepository.retrieveEventStreamTO(COLLECTION)).thenReturn(Optional.of(eventStreamTO));
		when(eventSourceRepository.getEventSource(COLLECTION)).thenReturn(Optional.of(new EventSource(COLLECTION, List.of())));
		eventPublisher.publishEvent(new EventStreamCreatedEvent(eventStream));
	}

	@And("the client receives a single event stream")
	public void theClientReceivesASingleEventStream() throws Exception {
		Model expectedModel = readModelFromFile("eventstream/streams-with-dcat/ldes-with-dcat.ttl");
		resultActions.andExpect(IsIsomorphic.with(expectedModel));
		verify(eventStreamRepository).retrieveEventStreamTO(COLLECTION);
	}

	@Given("an empty db")
	public void anEmptyDb() {
		when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.empty());
	}

	@And("I verify the db interaction")
	public void iVerifyTheDbInteraction() {
		InOrder inOrder = inOrder(eventStreamRepository, shaclShapeRepository, viewRepository);
		inOrder.verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		inOrder.verifyNoMoreInteractions();
	}

	@And("I verify the event stream retrieval interaction")
	public void iVerifyTheEventStreamRetrievalInteraction() {
		verify(eventStreamRepository).retrieveEventStreamTO(COLLECTION);
	}

	@Given("a db which does not contain specified event stream")
	public void aDbWhichDoesNotContainSpecifiedEventStream() {
		assertEquals(Optional.empty(), eventStreamRepository.retrieveEventStream(COLLECTION));
	}

	@And("I verify the event stream in the response body to file (.*)$")
	public void iVerifyTheEventStreamInTheResponseBody(String filename) throws Exception {
		final Model expectedModel = readModelFromFile(filename);
		resultActions.andExpect(IsIsomorphic.with(expectedModel));
	}

	@And("I verify the event stream is saved to the db")
	public void iVerifyTheEventStreamIsSavedToTheDb() {
		verify(eventStreamRepository).saveEventStream(any(EventStreamTO.class));

	}

	@When("^the client posts model from file (.*)$")
	public void theClientPostsModelFromFileFileName(String fileName) throws Exception {
		resultActions = mockMvc.perform(post("/admin/api/v1/eventstreams")
				.accept(Lang.TURTLE.getHeaderString())
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile(fileName)));
	}

	@And("I verify the absence of interactions")
	public void iVerifyTheAbsenceOfInteractions() {
		/*
		 * If the test with this step is ran as very first test, there will always be one interaction with the db,
		 * due to the initialization of the event streams in the service. Therefor, when no interactions are expected,
		 * there could be at most one interaction to retrieve all the event streams.
		 * Otherwise, no interactions are expected
		 */
		verify(eventStreamRepository, atMostOnce()).retrieveAllEventStreams();
		verifyNoMoreInteractions(eventStreamRepository);
	}

	@When("the client deletes the event stream")
	public void theClientDeletesTheEventStream() throws Exception {
		resultActions = mockMvc.perform(delete("/admin/api/v1/eventstreams/{collection}", COLLECTION));
	}

	@And("I verify the db interactions")
	public void iVerifyTheDbInteractions() {
		verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		verify(eventStreamRepository).deleteEventStream(COLLECTION);
		verify(shaclShapeRepository).deleteShaclShape(COLLECTION);
	}

	private Model readModelFromFile(String fileName) throws URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		String uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI()
				.toString();
		return RDFDataMgr.loadModel(uri);
	}

	private String readDataFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		return Files.lines(path).collect(Collectors.joining());
	}

	@And("I verify the absence of the event stream")
	public void iVerifyTheAbsenceOfTheEventStream() throws Exception {
		mockMvc.perform(get("/admin/api/v1/eventstreams/{collection}", COLLECTION).accept(Lang.NQUADS.getHeaderString()))
				.andExpect(content().string("Resource of type: eventstream with id: %s could not be found.".formatted(COLLECTION)))
				.andExpect(status().isNotFound());

	}

	@And("I verify the event stream deletion interaction")
	public void iVerifyTheEventStreamDeletionInteraction() {
		verify(eventStreamRepository).deleteEventStream(COLLECTION);
	}

	@Given("a db containing one deletable event stream")
	public void aDbContainingOneDeletableEventStream() {
		when(eventStreamRepository.deleteEventStream(COLLECTION)).thenReturn(1);
	}

	@And("I verify the saved event stream has a skolemization domain")
	public void iVerifyTheSavedEventStreamHasASkolemizationDomain() {
		verify(eventStreamRepository).saveEventStream(assertArg(actual -> assertThat(actual.getSkolemizationDomain()).isNotNull()));
	}

	@And("I verify the saved event stream has no skolemization domain")
	public void iVerifyTheSavedEventStreamHasNoSkolemizationDomain() {
		verify(eventStreamRepository).saveEventStream(assertArg(actual -> assertThat(actual.getSkolemizationDomain()).isNull()));
	}

	@And("I verify no event stream has been saved to the db")
	public void iVerifyNoEventStreamHasBeenSavedToTheDb() {
		verify(eventStreamRepository, never()).saveEventStream(any());
	}

	@When("the client posts an event source to that event stream")
	public void theClientPostsAnEventSourceToThatEventStream() throws Exception {
		resultActions = mockMvc.perform(put("/admin/api/v1/eventstreams/{collection}/eventsource", COLLECTION)
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile("retention/event-source.ttl")));
	}
}
