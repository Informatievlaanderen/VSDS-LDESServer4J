package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.views;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.SpringIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.riot.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminViewsRestControllerSteps extends SpringIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	private ResultActions resultActions;

	@When("A PUT request is made to {string} with body from file {string}")
	public void aPUTRequestIsMadeToWithBodyFromFile(String endpoint, String fileName) throws Exception {
		resultActions = mockMvc.perform(put(endpoint)
				.accept(Lang.TURTLE.getHeaderString())
				.contentType(Lang.TURTLE.getHeaderString())
				.content(readDataFromFile(fileName)));
	}

	@Then("The ViewSpecification with id {string} is saved in the ViewRepository")
	public void theViewSpecificationWithIdIsSavedInTheViewRepository(String viewName) {
		verify(viewRepository).saveView(new ViewSpecification(ViewName.fromString(viewName), List.of(), List.of()));
	}

	@And("HTTP Status code {int} is received")
	public void httpStatusCodeIsReceived(int statusCode) throws Exception {
		resultActions.andExpect(status().is(statusCode));
	}

	private String readDataFromFile(String fileName)
			throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		return Files.lines(path).collect(Collectors.joining());
	}
}
