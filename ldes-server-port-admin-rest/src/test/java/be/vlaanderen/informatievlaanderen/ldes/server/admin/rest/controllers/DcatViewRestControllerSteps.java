package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.SpringIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.view.entity.DcatView;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DcatViewRestControllerSteps extends SpringIntegrationTest {

	private final static String COLLECTION_NAME = "collectionName";
	private final static String VIEW = "viewName";
	private final static ViewName VIEW_NAME = new ViewName(COLLECTION_NAME, VIEW);

	private ResultActions resultActions;
	private String turtleDataservice;

	@Given("I have a valid dcat dataservice")
	public void iHaveAValidDcatDataservice() throws Exception {
		turtleDataservice = readDataFromFile("dcat-view-valid.ttl");
	}

	@Given("I have a invalid dcat dataservice")
	public void iHaveAnInvalidDcatDataservice() throws Exception {
		turtleDataservice = readDataFromFile("dcat-view-invalid.ttl");
	}

	@When("I POST this dataservice")
	public void iCanPOSTThisDataservice() throws Exception {
		resultActions = mockMvc.perform(post(DcatViewsRestController.BASE_URL, COLLECTION_NAME, VIEW)
				.contentType(Lang.TURTLE.getHeaderString())
				.content(turtleDataservice));
	}

	@When("I PUT this dataservice")
	public void iPUTThisDataservice() throws Exception {
		resultActions = mockMvc.perform(put(DcatViewsRestController.BASE_URL, COLLECTION_NAME, VIEW)
				.contentType(Lang.TURTLE.getHeaderString())
				.content(turtleDataservice));
	}

	@When("I DELETE this dataservice")
	public void iDELETEThisDataservice() throws Exception {
		resultActions = mockMvc.perform(delete(DcatViewsRestController.BASE_URL, COLLECTION_NAME, VIEW));
	}

	private String readDataFromFile(String filename) throws IOException {
		File file = ResourceUtils.getFile("classpath:" + filename);
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}

	@And("The dataservice metadata will be stored")
	public void theMetadataWillBeStored() {
		Model model = RDFParser.fromString(turtleDataservice).lang(Lang.TURTLE).build().toModel();
		DcatView dcatView = DcatView.from(VIEW_NAME, model);
		verify(dcatViewRepository, times(1)).save(dcatView);
	}

	@And("The dataservice metadata will not be stored")
	public void theMetadataWillNotBeStored() {
		Model model = RDFParser.fromString(turtleDataservice).lang(Lang.TURTLE).build().toModel();
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

	// @When("the client makes a post request with a valid model")
	// public void theClientMakesAPostRequestWithAValidModel() throws Exception {
	// when(dcatServerRepository.saveServerDcat(any()))
	// .thenReturn(new DcatServer(uuid,
	// readModelFromFile("dcat/valid-server-dcat.ttl")));
	//
	// resultActions = mockMvc.perform(post("/admin/api/v1/dcat")
	// .contentType(Lang.TURTLE.getHeaderString())
	// .content(readDataFromFile("dcat/valid-server-dcat.ttl")));
	// }
	//
	// @And("the client receives an UUID")
	// public void theClientReceivesAnUUID() throws Exception {
	// resultActions.andExpect(content().string(uuid));
	// }
	//
	// @Then("the client receives HTTP status {int} for dcatserverrequest")
	// public void theClientReceivesHTTPStatusForDcatserverrequest(int status)
	// throws Exception {
	// resultActions.andExpect(status().is(status));
	// }
	//
	// private byte[] readDataFromFile(String filename) throws IOException {
	// Path path = ResourceUtils.getFile("classpath:" + filename).toPath();
	// return Files.readAllBytes(path);
	// }
	//
	// private Model readModelFromFile(String filename) {
	// return RDFParser.source(filename).lang(Lang.TURTLE).toModel();
	// }
	//
	// @When("the client makes a post request with a invalid model")
	// public void theClientMakesAPostRequestWithAInvalidModel() throws Exception {
	// resultActions = mockMvc.perform(post("/admin/api/v1/dcat")
	// .contentType(Lang.TURTLE.getHeaderString())
	// .content(readDataFromFile("dcat/invalid-server-dcat.ttl")));
	// }
	//
	// @Given("a db containing one dcatserver")
	// public void aDbContainingOneDcatserver() {
	// DcatServer dcatServer = new DcatServer(uuid,
	// readModelFromFile("dcat/valid-server-dcat.ttl"));
	// when(dcatServerRepository.getServerDcat()).thenReturn(List.of(dcatServer));
	// when(dcatServerRepository.getServerDcatById(uuid)).thenReturn(Optional.of(dcatServer));
	// }
	//
	// @When("the client makes a put request with a valid model")
	// public void theClientMakesAPutRequestWithAValidModel() throws Exception {
	// resultActions = mockMvc.perform(put("/admin/api/v1/dcat/{id}", uuid)
	// .contentType(Lang.TURTLE.getHeaderString())
	// .content(readDataFromFile("dcat/valid-server-dcat.ttl")));
	// }
	//
	// @When("the client makes a put request with a invalid model")
	// public void theClientMakesAPutRequestWithAnInValidModel() throws Exception {
	// resultActions = mockMvc.perform(put("/admin/api/v1/dcat/{id}", uuid)
	// .contentType(Lang.TURTLE.getHeaderString())
	// .content(readDataFromFile("dcat/invalid-server-dcat.ttl")));
	// }
	//
	// @When("the client makes a delete serverdcat request")
	// public void theClientMakesADeleteServerdcatRequest() throws Exception {
	// resultActions = mockMvc.perform(delete("/admin/api/v1/dcat/{id}", uuid));
	// }
}
