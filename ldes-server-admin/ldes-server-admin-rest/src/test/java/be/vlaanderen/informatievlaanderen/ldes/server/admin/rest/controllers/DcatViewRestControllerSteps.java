package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.SpringIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.DcatViewsRestController.BASE_URL;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DcatViewRestControllerSteps extends SpringIntegrationTest {

	private static final String COLLECTION_NAME = "collectionName";
	private static final String VIEW = "viewName";
	private static final ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);

	private ResultActions resultActions;
	private String turtleDataservice;

	@After
	public void cleanup() {
		resourceRemover.removeUsedResources();
	}

	@Given("I have a valid dcat dataservice")
	public void iHaveAValidDcatDataservice() throws Exception {
		turtleDataservice = readDataFromFile("dcat/dataservice/dcat-view-valid.ttl");
	}

	@Given("I have a invalid dcat dataservice")
	public void iHaveAnInvalidDcatDataservice() throws Exception {
		turtleDataservice = readDataFromFile("dcat/dataservice/dcat-view-invalid.ttl");
	}

	@When("I POST this dataservice")
	public void iCanPOSTThisDataservice() throws Exception {
		resultActions = mockMvc.perform(post(BASE_URL, COLLECTION_NAME, VIEW)
				.contentType(Lang.TURTLE.getHeaderString())
				.content(turtleDataservice));
	}

	@When("I PUT this dataservice")
	public void iPUTThisDataservice() throws Exception {
		resultActions = mockMvc.perform(put(BASE_URL, COLLECTION_NAME, VIEW)
				.contentType(Lang.TURTLE.getHeaderString())
				.content(turtleDataservice));
	}

	@When("I DELETE this dataservice")
	public void iDELETEThisDataservice() throws Exception {
		resultActions = mockMvc.perform(delete(BASE_URL, COLLECTION_NAME, VIEW));
	}

	private String readDataFromFile(String filename) throws IOException {
		File file = ResourceUtils.getFile("classpath:" + filename);
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}

	@And("The dataservice metadata will be stored")
	public void theMetadataWillBeStored() {
		Model model = RDFParser.create().fromString(turtleDataservice).lang(Lang.TURTLE).build().toModel();
		DcatView dcatView = DcatView.from(VIEW_NAME, model);
		verify(dcatViewRepository, times(1)).save(dcatView);
	}

	@And("The dataservice metadata will not be stored")
	public void theMetadataWillNotBeStored() {
		Model model = RDFParser.create().fromString(turtleDataservice).lang(Lang.TURTLE).build().toModel();
		DcatView dcatView = DcatView.from(VIEW_NAME, model);
		verify(dcatViewRepository, times(0)).save(dcatView);
	}

	@Then("The dataservice metadata will be deleted")
	public void theDataserviceMetadataWillBeDeleted() {
		verify(dcatViewRepository, times(1)).delete(VIEW_NAME);
	}

	@And("Response with http {int} will be returned")
	public void responseWithHttpWillBeReturned(int httpCode) throws Exception {
		resultActions.andExpect(status().is(httpCode));
	}

	@And("The dataservice already exists")
	public void theDataserviceAlreadyExists() {
		Model defaultModel = ModelFactory.createDefaultModel();
		when(dcatViewRepository.findByViewName(VIEW_NAME))
				.thenReturn(Optional.of(DcatView.from(VIEW_NAME, defaultModel)));
	}

	@And("The dataservice does not yet exist")
	public void theDataserviceDoesNotYetExist() {
		when(dcatViewRepository.findByViewName(VIEW_NAME)).thenReturn(Optional.empty());
	}

}
