package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.resultactionsextensions.MemberCounter;
import be.vlaanderen.informatievlaanderen.ldes.server.resultactionsextensions.ResponseToModelConverter;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.awaitility.pollinterval.IterativePollInterval.iterative;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FragmentationSteps extends LdesServerIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(FragmentationSteps.class);
    private static final String TREE = "https://w3id.org/tree#";
    private Model currentFragment;
    private String currentPath;
    private String currentFragmentCacheControl;

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

    @When("I fetch the root {string} fragment of {string}")
    public void iFetchTheRootFragment(String view, String collection) throws Exception {
        log.atDebug().log("When I fetch the root {} fragment of {}", view, collection);
        currentPath = "/%s/%s".formatted(collection, view);
        fetchFragment(currentPath);
    }

    private void fetchFragment(String path) throws Exception {
        currentPath = path;
        MockHttpServletResponse response = mockMvc.perform(get(new URI(path))
                        .accept("text/turtle"))
                .andReturn()
                .getResponse();
        if (log.isTraceEnabled()) {
            log.atTrace().log("fetchFragment({}) -> status: {}, response: {}", path, response.getStatus(), response.getContentAsString());
        } else {
            log.atDebug().log("fetchFragment({}) -> status: {}", path, response.getStatus());
        }
        if (response.getStatus() != 404) {
            currentFragmentCacheControl = response.getHeader("Cache-Control");
            currentFragment = new ResponseToModelConverter(response).convert();
        } else {
            currentFragment = null;
        }
    }

    @And("I fetch the next fragment through the first {string}")
    public void iFetchTheNextFragmentThroughTheFirst(String relation) {
        log.atDebug().log("And I fetch the next fragment through the first {}", relation);
        await()
                .atMost(60, SECONDS)
                .pollInterval(iterative(duration -> duration.getSeconds() < 10 ? duration.plus(1, ChronoUnit.SECONDS) : duration))
                .untilAsserted(() -> {
                    fetchFragment(currentPath);
                    assertNotNull(currentFragment);
                    boolean hasNextPage = currentFragment.listStatements(null, RDF.type, createResource(TREE + relation)).hasNext();
                    log.atDebug().log("hasNextPage: {}", hasNextPage);
                    assertTrue(hasNextPage);
                });
        Resource relationSubj = currentFragment.listStatements(null, RDF.type, createResource(TREE + relation))
                .next().getSubject();
        log.atDebug().log("relationSubj: {}", relationSubj.toString());

        currentPath = currentFragment.listStatements(relationSubj, createProperty(TREE, "node"), (Resource) null)
                .next().getObject().toString();
        log.atDebug().log("currentPath: {}", currentPath);

        await().atMost(60, SECONDS)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    fetchFragment(currentPath);
                    assertNotNull(currentFragment);
                });
    }

    @Then("this fragment only has {int} {string} relation")
    public void thisFragmentOnlyHasOne(int expectedRelationCount, String relation) {
        log.atDebug().log("Then this fragment only has {} {}", expectedRelationCount, relation);
        await()
                .atMost(60, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> {
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
        log.atDebug().log("And this fragment is immutable");
        await().atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> {
                    fetchFragment(currentPath);

                    assertThat(currentFragmentCacheControl)
                            .contains("immutable")
                            .contains("max-age=31536000");
                });

    }

    @And("this fragment contains {int} members")
    public void thisFragmentContainsMembers(int expectedMemberCount) {
        log.atDebug().log("And this fragment contains {} members", expectedMemberCount);
        await()
                .atMost(60, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> {
                    fetchFragment(currentPath);
                    return MemberCounter.countMembers(expectedMemberCount).matches(currentFragment);
                });
    }

    @And("this fragment is mutable")
    public void thisFragmentIsNotImmutable() {
        log.atDebug().log("And this fragment is mutable");
        assertFalse(currentFragmentCacheControl.contains("immutable"));
    }

    @And("this fragment has no relations")
    public void thisFragmentHasNoRelations() {
        log.atDebug().log("And this fragment has no relations");
        await().atMost(60, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> {
                    fetchFragment(currentPath);
                    return !currentFragment.listObjectsOfProperty(createProperty(TREE + "relation")).hasNext();
                });
    }

    @When("I fetch the {string} fragment for {string} from the {string} view of {string}")
    public void iFetchTheFragmentOf(String fragmentKey, String fragmentValue, String view, String collection)
            throws Exception {
        log.atDebug().log("When I fetch the {} fragment for {} from the {} view of {}", fragmentKey, fragmentValue, view, collection);
        currentPath = "/%s/%s?%s=%s".formatted(collection, view, fragmentKey, fragmentValue);
        iFetchTheFragmentOf(currentPath);
    }

    @When("I fetch the {string} fragment")
    public void iFetchTheFragmentOf(String path)
            throws Exception {
        log.atDebug().log("When I fetch the {} fragment", path);
        currentPath = path;
        MockHttpServletResponse response = mockMvc.perform(get(new URI(currentPath)).accept("text/turtle"))
                .andReturn()
                .getResponse();
        currentFragmentCacheControl = response.getHeader("Cache-Control");
        currentFragment = new ResponseToModelConverter(response).convert();
    }

    @When("I fetch the timebased fragment {string} fragment of this month of {string}")
    public void iFetchTheTimebasedFragmentFragmentOfTodayOf(String view, String collection) {
        log.atDebug().log("When I fetch the timebased fragment {} fragment of today of {}", view, collection);
        LocalDateTime now = LocalDateTime.now();
        currentPath = "/%s/%s?year=%s&month=%02d".formatted(collection, view, now.getYear(), now.getMonthValue());

        await()
                .atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> {
                    fetchFragment(currentPath);
                    return currentFragment != null;
                });
    }

    @Then("the following fragment URL {string} contains member with ID {string}")
    public void theLDESCollectionContainsFragments(String fragment, String memberId) {
        log.atDebug().log("Then the following fragment URL {} contains member with ID {}", fragment, memberId);
        await()
                .atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .untilAsserted(() -> mockMvc.perform(get(fragment))
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(content().string(containsString(memberId))));

    }

    @And("this fragment contains {int} members with {int} skolemized identifiers")
    public void thisFragmentContainsOnlyMembersWithSkolemizedIdentifiers(int memberCount, int skolemizedIdCount) {
        log.atDebug().log("And this fragment contains {} members with {} skolemized identifiers", memberCount, skolemizedIdCount);
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
        log.atDebug().log("Then I wait until all members are fragmented");
        await().atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> unprocessedViewRepository.findAll().isEmpty());
    }

    @Then("all members of {string} are marked as fragmented")
    public void allMembersAreMarkedAsFragmented(String collection) {
        log.atDebug().log("Then all members of {} are marked as fragmented", collection);
        allMembersAreMarkedFragmented(true);
    }

    private void allMembersAreMarkedFragmented(boolean isFragmented) {
        assertThat(fragmentationMemberEntityRepository.findAll().stream().map(MemberEntity::isFragmented)).containsOnly(isFragmented);
    }

    @Then("all members of {string} are marked as unfragmented")
    public void allMembersAreMarkedAsUnFragmented(String collection) {
        log.atDebug().log("Then all members of {} are marked as unfragmented", collection);
        allMembersAreMarkedFragmented(false);
    }


}
