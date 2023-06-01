package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.SpringIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.entities.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.shacl.entities.ShaclShape;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.FragmentationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminEventStreamsRestControllerSteps extends SpringIntegrationTest {
	private static final String COLLECTION = "name1";
	private static final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
	private static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
	private static final String MEMBER_TYPE = "https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder";
	private ResultActions resultActions;

	@Given("a db containing multiple eventstreams")
	public void aDbContainingMultipleEventstreams() throws URISyntaxException {
		final String collection2 = "name2";
		final EventStream eventStream = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE,
				false);
		final EventStream eventStream2 = new EventStream(collection2, TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE,
				true);
		Model shape = readModelFromFile("example-shape.ttl");
		FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName("fragmentationStrategy");
		fragmentationConfig.setConfig(Map.of("property", "ldes:propertyPath"));
		ViewSpecification singleView = new ViewSpecification(
				new ViewName(collection2, "view1"),
				List.of(),
				List.of(fragmentationConfig));
		List<ViewSpecification> views = List.of(
				new ViewSpecification(
						new ViewName(COLLECTION, "view2"),
						List.of(),
						List.of(fragmentationConfig)),
				new ViewSpecification(
						new ViewName(COLLECTION, "view3"),
						List.of(),
						List.of(fragmentationConfig)));

		when(eventStreamRepository.retrieveAllEventStreams()).thenReturn(List.of(eventStream, eventStream2));
		when(viewRepository.retrieveAllViewsOfCollection(COLLECTION)).thenReturn(views);
		when(viewRepository.retrieveAllViewsOfCollection(collection2)).thenReturn(List.of(singleView));
		when(shaclShapeRepository.retrieveShaclShape(COLLECTION))
				.thenReturn(Optional.of(new ShaclShape(COLLECTION, shape)));
		when(shaclShapeRepository.retrieveShaclShape(collection2))
				.thenReturn(Optional.of(new ShaclShape(collection2, shape)));
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
		Model expectedModel = readModelFromFile("multiple-ldes.ttl");
		resultActions.andExpect(IsIsomorphic.with(expectedModel));
		verify(eventStreamRepository).retrieveAllEventStreams();
		verify(viewRepository).retrieveAllViewsOfCollection(COLLECTION);
		verify(shaclShapeRepository).retrieveShaclShape(COLLECTION);
	}

	@Given("a db containing one event stream")
	public void aDbContainingOneEventStream() throws URISyntaxException {
		final EventStream eventStream = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE, true);
		Model shape = readModelFromFile("example-shape.ttl");
		when(shaclShapeRepository.retrieveShaclShape(COLLECTION))
				.thenReturn(Optional.of(new ShaclShape(COLLECTION, shape)));
		when(eventStreamRepository.retrieveEventStream(COLLECTION)).thenReturn(Optional.of(eventStream));
	}

	@And("the client receives a single event stream")
	public void theClientReceivesASingleEventStream() throws Exception {
		Model expectedModel = readModelFromFile("ldes-1.ttl");
		resultActions.andExpect(IsIsomorphic.with(expectedModel));
		verify(eventStreamRepository).retrieveEventStream(COLLECTION);
		verify(viewRepository).retrieveAllViewsOfCollection(COLLECTION);
		verify(shaclShapeRepository).retrieveShaclShape(COLLECTION);
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
		verify(eventStreamRepository).retrieveEventStream(COLLECTION);
	}

	@Given("a db which does not contain specified event stream")
	public void aDbWhichDoesNotContainSpecifiedEventStream() throws URISyntaxException {
		assertEquals(Optional.empty(), eventStreamRepository.retrieveEventStream(COLLECTION));
		final EventStream eventStream = new EventStream(COLLECTION, TIMESTAMP_PATH, VERSION_OF_PATH, MEMBER_TYPE, true);
		final Model shacl = readModelFromFile("example-shape.ttl");
		when(eventStreamRepository.saveEventStream(any(EventStream.class))).thenReturn(eventStream);
		when(shaclShapeRepository.saveShaclShape(any(ShaclShape.class))).thenReturn(new ShaclShape(COLLECTION, shacl));
	}

	@When("the client puts a valid model")
	public void theClientPutsAValidModel() throws Exception {
		resultActions = mockMvc.perform(put("/admin/api/v1/eventstreams")
				.accept(Lang.TURTLE.getHeaderString())
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile("ldes-1.ttl")));
	}

	@And("I verify the event stream in the response body")
	public void iVerifyTheEventStreamInTheResponseBody() throws Exception {
		final Model expectedModel = readModelFromFile("ldes-1.ttl");
		resultActions.andExpect(IsIsomorphic.with(expectedModel));

		verify(eventStreamRepository).saveEventStream(any());
		verify(shaclShapeRepository).saveShaclShape(any());
	}

	@When("^the client puts invalid model from file (.*)$")
	public void theClientPutsInvalidModelFromFileFileName(String fileName) throws Exception {
		resultActions = mockMvc.perform(put("/admin/api/v1/eventstreams")
				.accept(Lang.TURTLE.getHeaderString())
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile(fileName)));
	}

	@And("I verify the absent of interactions")
	public void iVerifyTheAbsentOfInteractions() {
		verifyNoInteractions(eventStreamRepository);
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
}
