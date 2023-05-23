package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.views;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.SpringIntegrationTest;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminViewsRestControllerSteps extends SpringIntegrationTest {
	private static final String COLLECTION = "name1";
	private static final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
	private static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
	private static final String MEMBER_TYPE = "https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder";
	private ResultActions resultActions;
	@Autowired
	private MockMvc mockMvc;

	@Given("a dbs containing multiple eventstreams")
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

		when(eventStreamCollection.retrieveAllEventStreams()).thenReturn(List.of(eventStream, eventStream2));
		when(viewRepository.retrieveAllViewsOfCollection(COLLECTION)).thenReturn(views);
		when(viewRepository.retrieveAllViewsOfCollection(collection2)).thenReturn(List.of(singleView));
		when(shaclShapeRepository.retrieveShaclShape(COLLECTION))
				.thenReturn(Optional.of(new ShaclShape(COLLECTION, shape)));
		when(shaclShapeRepository.retrieveShaclShape(collection2))
				.thenReturn(Optional.of(new ShaclShape(collection2, shape)));
	}

	@When("the clients calls {string}")
	public void theClientCallsAdminApiVEventStreams(String url) throws Exception {
		resultActions = mockMvc.perform(get(url).accept(MediaType.valueOf("text/turtle")));
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

	@Then("the clients receives HTTP status {int}")
	public void theClientReceivesHTTPStatus(int status) throws Exception {
		resultActions.andExpect(status().is(status));
	}

	@And("the clients receives a valid list of event streams")
	public void theClientReceivesAValidListOfEventStreams() throws Exception {
		Model expectedModel = readModelFromFile("multiple-ldes.ttl");
		resultActions.andExpect(IsIsomorphic.with(expectedModel));
		verify(eventStreamCollection).retrieveAllEventStreams();
		verify(viewRepository).retrieveAllViewsOfCollection(COLLECTION);
		verify(shaclShapeRepository).retrieveShaclShape(COLLECTION);
	}

}
