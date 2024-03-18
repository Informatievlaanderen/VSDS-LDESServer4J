package be.vlaanderen.informatievlaanderen.ldes.server;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.vocabulary.RDF;
import org.springframework.mock.web.MockHttpServletResponse;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class FragmentationSteps extends LdesServerIntegrationTest {
	private static final String TREE = "https://w3id.org/tree#";
	private static final Property TREE_MEMBER = createProperty(TREE, "member");
	private Model currentFragment;
	private String currentPath;
	private String currentFragmentCacheControl;

	@When("I fetch the root {string} fragment of {string}")
	public void iFetchTheRootFragment(String view, String collection) throws Exception {
		currentPath = "/%s/%s".formatted(collection, view);
		MockHttpServletResponse response = mockMvc.perform(get(new URI(currentPath)).accept("text/turtle"))
				.andReturn()
				.getResponse();
		currentFragmentCacheControl = response.getHeader("Cache-Control");
		currentFragment = RDFParser.fromString(response.getContentAsString()).lang(Lang.TURTLE).toModel();
	}

	private void fetchFragment(String path) throws Exception {
		currentPath = path;
		MockHttpServletResponse response = mockMvc.perform(get(new URI(path))
				.accept("text/turtle"))
				.andReturn()
				.getResponse();
		currentFragmentCacheControl = response.getHeader("Cache-Control");
		currentFragment = RDFParser.fromString(response.getContentAsString()).lang(Lang.TURTLE).toModel();
	}

	@And("I fetch the next fragment through the first {string}")
	public void iFetchTheNextFragmentThroughTheFirst(String relation) throws Exception {
		Resource relationSubj = currentFragment.listStatements(null, RDF.type, createResource(TREE + relation))
				.next().getSubject();

		currentPath = currentFragment.listStatements(relationSubj, createProperty(TREE, "node"), (Resource) null)
				.next().getObject().toString();

		MockHttpServletResponse response = mockMvc.perform(get(new URI(currentPath)).accept("text/turtle"))
				.andReturn()
				.getResponse();
		currentFragmentCacheControl = response.getHeader("Cache-Control");
		currentFragment = RDFParser.fromString(response.getContentAsString()).lang(Lang.TURTLE).toModel();
	}

	@Then("this fragment only has {int} {string} relation")
	public void thisFragmentOnlyHasOne(int expectedRelationCount, String relation) {
		await().atMost(Duration.of(20, ChronoUnit.SECONDS)).until(() -> {
			fetchFragment(currentPath);
			int relationCount = currentFragment.listStatements(null, RDF.type, createResource(TREE + relation))
					.toList().size();
			System.out.println(currentPath);
			System.out.println("relationcounts: " + relationCount);
			return relationCount == expectedRelationCount;
		});
	}

	@And("this fragment is immutable")
	public void thisFragmentIsImmutable() {
		assertTrue(currentFragmentCacheControl.contains("immutable"));
	}

	@And("this fragment contains {int} members")
	public void thisFragmentContainsMembers(int expectedMemberCount) {
		await().atMost(Duration.of(20, ChronoUnit.SECONDS)).until(() -> {
			fetchFragment(currentPath);
			return currentFragment.listObjectsOfProperty(TREE_MEMBER).toList().size() == expectedMemberCount;
		});
	}

	@And("this fragment is mutable")
	public void thisFragmentIsNotImmutable() {
		assertFalse(currentFragmentCacheControl.contains("immutable"));
	}

	@And("this fragment has no relations")
	public void thisFragmentHasNoRelations() {
		await().atMost(Duration.of(20, ChronoUnit.SECONDS)).until(() -> {
			fetchFragment(currentPath);
			return !currentFragment.listObjectsOfProperty(createProperty(TREE + "relation")).hasNext();
		});
	}

	@When("I fetch the {string} fragment for {string} from the {string} view of {string}")
	public void iFetchTheFragmentOf(String fragmentKey, String fragmentValue, String view, String collection)
			throws Exception {
		currentPath = "/%s/%s?%s=%s".formatted(collection, view, fragmentKey, fragmentValue);
		iFetchTheFragmentOf(currentPath);
	}

	@When("I fetch the {string} fragment")
	public void iFetchTheFragmentOf(String path)
			throws Exception {
		currentPath = path;
		MockHttpServletResponse response = mockMvc.perform(get(new URI(currentPath)).accept("text/turtle"))
				.andReturn()
				.getResponse();
		currentFragmentCacheControl = response.getHeader("Cache-Control");
		currentFragment = RDFParser.fromString(response.getContentAsString()).lang(Lang.TURTLE).toModel();
	}

	@When("I fetch the timebased fragment {string} fragment of this month of {string}")
	public void iFetchTheTimebasedFragmentFragmentOfTodayOf(String view, String collection) throws Exception {
		LocalDateTime now = LocalDateTime.now();
		currentPath = "/%s/%s?year=%s&month=%02d".formatted(collection, view, now.getYear(), now.getMonthValue());
		String response = mockMvc.perform(get(new URI(currentPath)).accept("text/turtle"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		currentFragment = RDFParser.fromString(response).lang(Lang.TURTLE).toModel();
	}
}
