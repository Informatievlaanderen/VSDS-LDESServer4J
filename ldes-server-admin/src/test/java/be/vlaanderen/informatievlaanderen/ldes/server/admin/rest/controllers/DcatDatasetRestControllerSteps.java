package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers;

import be.vlaanderen.informatievlaanderen.ldes.server.admin.domain.dcat.dcatdataset.entities.DcatDataset;
import be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.config.SpringIntegrationTest;
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

import static be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.controllers.DcatDatasetRestController.BASE_URL;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DcatDatasetRestControllerSteps extends SpringIntegrationTest {
	private final static String COLLECTION_NAME = "collectionName";
	private ResultActions resultActions;
	private String datasetString;

	@Given("I have a valid dcat dataset")
	public void iHaveAValidDcatDataset() throws Exception {
		datasetString = readDataFromFile("dcat/dataset/valid.ttl");
	}

	@Given("I have a invalid dcat dataset")
	public void iHaveAnInvalidDcatDataset() throws Exception {
		datasetString = readDataFromFile("dcat/dataset/not-valid.ttl");
	}

	@When("I POST this dataset")
	public void iCanPOSTThisDataset() throws Exception {
		resultActions = mockMvc.perform(post(BASE_URL, COLLECTION_NAME)
				.contentType(Lang.TURTLE.getHeaderString())
				.content(datasetString));
	}

	@When("I PUT this dataset")
	public void iPUTThisDataset() throws Exception {
		resultActions = mockMvc.perform(put(BASE_URL, COLLECTION_NAME)
				.contentType(Lang.TURTLE.getHeaderString())
				.content(datasetString));
	}

	@When("I DELETE this dataset")
	public void iDELETEThisDataservice() throws Exception {
		resultActions = mockMvc.perform(delete(BASE_URL, COLLECTION_NAME));
	}

	private String readDataFromFile(String filename) throws IOException {
		File file = ResourceUtils.getFile("classpath:" + filename);
		return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	}

	@And("The dataset will be stored")
	public void theMetadataWillBeStored() {
		Model model = RDFParser.fromString(datasetString).lang(Lang.TURTLE).build().toModel();
		DcatDataset dataset = new DcatDataset(COLLECTION_NAME, model);
		verify(dcatDatasetRepository, times(1)).saveDataset(dataset);
	}

	@And("The dataset will not be stored")
	public void theMetadataWillNotBeStored() {
		Model model = RDFParser.fromString(datasetString).lang(Lang.TURTLE).build().toModel();
		DcatDataset dataset = new DcatDataset(COLLECTION_NAME, model);
		verify(dcatDatasetRepository, never()).saveDataset(dataset);
	}

	@Then("The dataset will be deleted")
	public void theDataserviceMetadataWillBeDeleted() {
		verify(dcatDatasetRepository, times(1)).deleteDataset(COLLECTION_NAME);
	}

	@Then("The dataset will not be deleted")
	public void theDataserviceMetadataWillNotBeDeleted() {
		verify(dcatDatasetRepository, never()).deleteDataset(COLLECTION_NAME);
	}

	@And("Response with http {int} will be returned for dataset")
	public void responseWithHttpWillBeReturned(int httpCode) throws Exception {
		resultActions.andExpect(status().is(httpCode));
	}

	@And("The dataset already exists")
	public void theDataserviceAlreadyExists() {
		Model defaultModel = ModelFactory.createDefaultModel();
		when(dcatDatasetRepository.retrieveDataset(COLLECTION_NAME))
				.thenReturn(Optional.of(new DcatDataset(COLLECTION_NAME, defaultModel)));
	}

	@And("The dataset does not yet exist")
	public void theDataserviceDoesNotYetExist() {
		when(dcatDatasetRepository.retrieveDataset(COLLECTION_NAME)).thenReturn(Optional.empty());
	}

}
