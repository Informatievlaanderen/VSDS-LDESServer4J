package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.resultactionsextensions.MemberCounter;
import be.vlaanderen.informatievlaanderen.ldes.server.resultactionsextensions.ResponseToModelConverter;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.pollinterval.IterativePollInterval.iterative;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FragmentationSteps extends LdesServerIntegrationTest {
	private static final Logger log = LoggerFactory.getLogger(LdesServerIntegrationTest.class);
	private static final String TREE = "https://w3id.org/tree#";
	private Model currentFragment;
	private String currentPath;
	private String currentFragmentCacheControl;

	@When("I fetch the root {string} fragment of {string}")
	public void iFetchTheRootFragment(String view, String collection) throws Exception {
		currentPath = "/%s/%s".formatted(collection, view);
		fetchFragment(currentPath);
	}

	private void fetchFragment(String path) throws Exception {
		currentPath = path;
		MockHttpServletResponse response = mockMvc.perform(get(new URI(path))
						.accept("text/turtle"))
				.andReturn()
				.getResponse();

		if (response.getStatus() != 404) {
			currentFragmentCacheControl = response.getHeader("Cache-Control");
			currentFragment = new ResponseToModelConverter(response).convert();
		} else {
			currentFragment = null;
		}
	}

	@And("I fetch the next fragment through the first {string}")
	public void iFetchTheNextFragmentThroughTheFirst(String relation) {
		await()
				.atMost(90, TimeUnit.SECONDS)
				.pollInterval(iterative(duration -> duration.getSeconds() < 10 ? duration.plus(1, ChronoUnit.SECONDS) : duration))
				.untilAsserted(() -> {
					fetchFragment(currentPath);
					assertNotNull(currentFragment);
					assertTrue(currentFragment.listStatements(null, RDF.type, createResource(TREE + relation))
							.hasNext());
				});
		Resource relationSubj = currentFragment.listStatements(null, RDF.type, createResource(TREE + relation))
				.next().getSubject();

		currentPath = currentFragment.listStatements(relationSubj, createProperty(TREE, "node"), (Resource) null)
				.next().getObject().toString();

		await().atMost(60, TimeUnit.SECONDS)
				.untilAsserted(() -> {
					fetchFragment(currentPath);
					assertNotNull(currentFragment);
				});
	}

	@Then("this fragment only has {int} {string} relation")
	public void thisFragmentOnlyHasOne(int expectedRelationCount, String relation) {
		await().atMost(Duration.of(60, ChronoUnit.SECONDS)).until(() -> {
			fetchFragment(currentPath);
			int relationCount = currentFragment.listStatements(null, RDF.type, createResource(TREE + relation))
					.toList().size();
			log.debug(currentPath);
			log.debug("relationcounts: {}", relationCount);
			return relationCount == expectedRelationCount;
		});
	}

	@And("this fragment is immutable")
	public void thisFragmentIsImmutable() {
		Awaitility.await().untilAsserted(() -> {
			fetchFragment(currentPath);

			assertThat(currentFragmentCacheControl)
					.contains("immutable")
					.contains("max-age=31536000");
		});

	}

	@And("this fragment contains {int} members")
	public void thisFragmentContainsMembers(int expectedMemberCount) {
		await().atMost(Duration.of(60, ChronoUnit.SECONDS)).until(() -> {
			fetchFragment(currentPath);
			return MemberCounter.countMembers(expectedMemberCount).matches(currentFragment);
		});
	}

	@And("this fragment is mutable")
	public void thisFragmentIsNotImmutable() {
		assertFalse(currentFragmentCacheControl.contains("immutable"));
	}

	@And("this fragment has no relations")
	public void thisFragmentHasNoRelations() {
		await().atMost(Duration.of(60, ChronoUnit.SECONDS)).until(() -> {
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
		currentFragment = new ResponseToModelConverter(response).convert();
	}

	@When("I fetch the timebased fragment {string} fragment of this month of {string}")
	public void iFetchTheTimebasedFragmentFragmentOfTodayOf(String view, String collection) {
		LocalDateTime now = LocalDateTime.now();
		currentPath = "/%s/%s?year=%s&month=%02d".formatted(collection, view, now.getYear(), now.getMonthValue());

		await()
				.atMost(10, TimeUnit.SECONDS)
				.until(() -> {
					fetchFragment(currentPath);
					return currentFragment != null;
				});
	}

	@Then("the following fragment URL {string} contains member with ID {string}")
	public void theLDESCollectionContainsFragments(String fragment, String memberId) {
		await()
				.atMost(Duration.ofSeconds(20))
				.pollInterval(Duration.ofSeconds(1))
				.untilAsserted(() -> mockMvc.perform(get(fragment))
						.andExpect(status().is2xxSuccessful())
						.andExpect(content().string(containsString(memberId))));

	}

	@And("this fragment contains {int} members with {int} skolemized identifiers")
	public void thisFragmentContainsOnlyMembersWithSkolemizedIdentifiers(int memberCount, int skolemizedIdCount) {
		List<Integer> skolemizedIdCountPerMember = currentFragment.listObjectsOfProperty(TREE_MEMBER)
				.filterKeep(RDFNode::isResource)
				.mapWith(RDFNode::asResource)
				.mapWith(Resource::listProperties)
				.mapWith(FragmentationSteps::countSkolemizedIds)
				.toList();

		assertThat(skolemizedIdCountPerMember)
				.hasSize(memberCount)
				.allSatisfy(actualSkolemizedIdCount -> assertThat(actualSkolemizedIdCount).isEqualTo(skolemizedIdCount));
	}

	@Then("I wait until all members are fragmented")
	public void waitUntilAllMembersAreFragmented() {
		await().until(() -> unprocessedViewRepository.findAll().isEmpty());
	}

	private static Integer countSkolemizedIds(StmtIterator stmtIterator) {
		return stmtIterator
				.mapWith(Statement::getObject)
				.filterDrop(RDFNode::isAnon)
				.filterKeep(RDFNode::isResource)
				.mapWith(RDFNode::asResource)
				.mapWith(Resource::listProperties)
				.filterKeep(StmtIterator::hasNext)
				.toList()
				.size();
	}


}
