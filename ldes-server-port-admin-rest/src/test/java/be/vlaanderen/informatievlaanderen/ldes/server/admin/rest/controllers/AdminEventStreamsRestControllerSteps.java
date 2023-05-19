package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.integration.AdminRestControllerIntegrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AdminEventStreamsRestControllerSteps extends AdminRestControllerIntegrationTest {


	@Given("a db containing multiple eventstreams")
	public void aDbContainingMultipleEventstreams() {
//		Mockito.when(eventStreamRepository.retrieveAllEventStreams()).thenReturn(List.of());
	}

	@When("the client calls {string}")
	public void theClientCallsAdminApiVEventStreams(String url) throws Exception {
		executeGet(url);
	}

	@Then("the client receives list of event streams")
	public void theClientReceivesListOfEventStreams() {

	}
}
