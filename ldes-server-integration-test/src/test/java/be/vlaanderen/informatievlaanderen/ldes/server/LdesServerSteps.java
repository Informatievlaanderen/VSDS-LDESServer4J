package be.vlaanderen.informatievlaanderen.ldes.server;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LdesServerSteps extends LdesServerIntegrationTest {

	@Given("I create the eventstream {string}")
	public void iCreateTheEventstream(String eventStreamDescriptionFile) throws Exception {
		String eventstream = readBodyFromFile(eventStreamDescriptionFile);
		mockMvc.perform(post("/admin/api/v1/eventstreams")
				.contentType(RDFLanguages.guessContentType(eventStreamDescriptionFile).getContentTypeStr())
				.content(eventstream))
				.andExpect(status().isCreated());
	}

	private Model getResponseAsModel(String url) throws Exception {
		return RDFParser.fromString(mockMvc.perform(get(url)
				.accept("text/turtle"))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString()).lang(Lang.TURTLE).toModel();
	}

	@When("I ingest {int} members to the collection {string}")
	public void iIngestMembersToTheCollection(int numberOfMembers, String collectionName) throws Exception {
		for (int i = 0; i < numberOfMembers; i++) {
			Model member = RDFParser.fromString(readMemberTemplate("data/input/members/member_template.ttl")
					.replace("ID", String.valueOf(i)))
					.lang(Lang.TURTLE)
					.toModel();
			mockMvc.perform(post("/" + collectionName)
					.contentType("text/turtle")
					.content(RDFWriter.source(member).lang(Lang.TURTLE).asString()))
					.andExpect(status().isOk());
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

	@Then("I can fetch the TreeNode {string} and it contains {int} members and the expected response is equal to {string}")
	public void iCanFetchTheTreeNodeAndItContainsMembersAndTheExpectedResponseIsEqualTo(String url,
			int expectedNumberOfMembers, String expectedOutputFile) throws Exception {
		await()
				.atMost(10, SECONDS)
				.pollInterval(1, SECONDS)
				.until(() -> {
					int size = getResponseAsModel(url).listObjectsOfProperty(TREE_MEMBER).toList().size();
					return size == expectedNumberOfMembers;
				});
		Model expectedModel = readModelFromFile(expectedOutputFile);
		Model actualModel = getResponseAsModel(url);
		assertTrue(actualModel.isIsomorphicWith(expectedModel));
	}

	@When("I ingest the member described in {string} the collection {string}")
	public void iIngestTheMemberDescribedInTheCollection(String memberFileName, String collectionName)
			throws Exception {
		String member = readBodyFromFile(memberFileName);
		ContentType contentType = RDFLanguages.guessContentType(memberFileName);
		mockMvc.perform(post("/" + collectionName)
				.contentType(contentType.getContentTypeStr())
				.content(member))
				.andExpect(status().isOk());
	}

	private String readBodyFromFile(String fileName) throws URISyntaxException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		URI uri = Objects.requireNonNull(classLoader.getResource(fileName)).toURI();
		return Files.lines(Paths.get(uri)).collect(Collectors.joining("\n"));
	}

	@Then("I delete the eventstream {string}")
	public void iDeleteTheEventstream(String eventStreamName) throws Exception {
		mockMvc.perform(delete("/admin/api/v1/eventstreams/" + eventStreamName))
				.andExpect(status().isOk());
	}
}
