package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.IsIsomorphic;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.SpringIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.*;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminViewsRestControllerSteps extends SpringIntegrationTest {

	private ResultActions resultActions;
	private final String COLLECTION_NAME = "name1";
	private Model view1;
	private Model view2;

	@After
	public void cleanup() {
		resourceRemover.removeUsedResources();
	}

	@When("I POST a view from file {string} to {string}")
	public void iPOSTAViewFromFile(String fileName, String endpoint) throws Exception {
		resultActions = mockMvc.perform(post(endpoint).contentType(Lang.TURTLE.getHeaderString()).content(readDataFromFile(fileName)));
	}

	private byte[] readDataFromFile(String fileName) throws IOException {
		File file = ResourceUtils.getFile("classpath:" + fileName);
		return FileUtils.readFileToByteArray(file);
	}

	@Then("I obtain an HTTP status {int}")
	public void iObtainAnHTTPStatusStatus(int status) throws Exception {
		resultActions.andExpect(status().is(status));
	}

	@Given("an LDES server with an event stream")
	public void anLDESServerWithAnEventStream() {
		eventPublisher.publishEvent(new EventStreamCreatedEvent(new EventStream(COLLECTION_NAME, "timestamp-path", "isVersionOf-path", VersionCreationProperties.disabled(), "skol-dom")));
	}

	@When("I DELETE a view with {string}")
	public void iDELETEAView(String id) throws Exception {
		ViewName viewName = ViewName.fromString(id);
		resultActions = mockMvc.perform(delete("/admin/api/v1/eventstreams/{collectionName}/views/{viewName}", viewName.getCollectionName(), viewName.getViewName()));
	}

	@And("I verify there was {int} db deletion")
	public void iVerifyTheDbInteractions(int numberOfDeletions) {
		final ViewName viewName = new ViewName(COLLECTION_NAME, "view1");
		verify(viewRepository, times(numberOfDeletions)).deleteViewByViewName(viewName);
	}

	@And("the event stream contains two views")
	public void theEventStreamContainsTwoViews() {
		view1 = RDFDataMgr.loadModel("view/view.ttl");
		view2 = RDFDataMgr.loadModel("view/another-view.ttl");

		final FragmentationConfig fragmentationConfig = new FragmentationConfig();
		fragmentationConfig.setName("ReferenceFragmentation");
		fragmentationConfig.setConfig(Map.of("property", "ldes:propertyPath"));

		final Model retention = RDFDataMgr.loadModel("retention/ten-minutes-timebased-policy.ttl");

		final ViewSpecification viewSpecification1 = new ViewSpecification(new ViewName(COLLECTION_NAME, "view1"), List.of(retention), List.of(fragmentationConfig), 100);
		final ViewSpecification viewSpecification2 = new ViewSpecification(new ViewName(COLLECTION_NAME, "view2"), List.of(retention), List.of(fragmentationConfig), 100);

		when(viewRepository.retrieveAllViewsOfCollection(COLLECTION_NAME)).thenReturn(List.of(viewSpecification1, viewSpecification2));
		when(viewRepository.getViewByViewName(new ViewName(COLLECTION_NAME, "view1"))).thenReturn(Optional.of(viewSpecification1));
	}

	@When("I GET the views of the event stream")
	public void iGETTheViewsOfTheEventStream() throws Exception {
		resultActions = mockMvc.perform(get("/admin/api/v1/eventstreams/name1/views").accept(Lang.NQUADS.getHeaderString()));
	}

	@And("I check the response body is isomorphic with the two views")
	public void iCheckTheResponseBodyIsIsomorphicWithTheTwoViews() throws Exception {
		resultActions.andExpect(IsIsomorphic.with(view1.add(view2)));
	}

	@And("I check the response body is isomorphic with the single view")
	public void iCheckTheResponseBodyIsIsomorphicWithTheSingleView() throws Exception {
		resultActions.andExpect(IsIsomorphic.with(view1));
	}

	@When("I GET a view of the event stream wit id {string}")
	public void iGETAViewOfTheEventStreamWithId(String id) throws Exception {
		ViewName viewName = ViewName.fromString(id);
		resultActions = mockMvc.perform(
				get("/admin/api/v1/eventstreams/{collectionName}/views/{viewName}", viewName.getCollectionName(), viewName.getViewName())
						.accept(Lang.NQUADS.getHeaderString())
		);
	}

	@And("I check if all views from collection {string} were retrieved from the db")
	public void iCheckIfAllViewWereRetrievedFromTheDb(String collectionName) {
		verify(viewRepository).retrieveAllViewsOfCollection(collectionName);
	}

	@And("I check if view with id {string} was retrieved from the db")
	public void iCheckIfViewWereRetrievedFromTheDb(String viewName) {
		verify(viewRepository).getViewByViewName(ViewName.fromString(viewName));
	}

	@And("I check if there were no interactions with the db")
	public void iCheckIfThereWereNoInteractionsWithTheDb() {
		/*
		 * If the test with this step is ran as very first test, there will always be one interaction with the db,
		 * due to the initialization of the views in the service. Therefor, when no interactions are expected,
		 * there could be at most one interaction to retrieve all the views.
		 * Otherwise, no interactions are expected
		 */
		verify(viewRepository, atMostOnce()).retrieveAllViews();
		verifyNoMoreInteractions(viewRepository);
	}
}
