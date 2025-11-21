package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageRelationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.resultactionsextensions.ResponseToModelConverter;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.vocabulary.RDF;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("java:S3415")
public class CompactionServiceSteps extends LdesServerIntegrationTest {
    private int versionIncremeter = 1;
    private ScheduledExecutorService executorService;
    private Future<?> seedingTask;

    @Before
    public void setup() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @After
    public void cleanup() {
        executorService.shutdown();
    }

    @And("I ingest {int} members of different versions")
    public void ingestDifferentVersions(int amount) throws Exception {
        for (int i = 0; i < amount; i++) {
            String memberContent = readMemberTemplate("data/input/members/mob-hind.template.ttl")
                    .replace("ID", String.valueOf(++versionIncremeter))
                    .replace("DATETIME", getCurrentTimestamp());
            mockMvc.perform(post("/" + "mobility-hindrances")
                            .contentType("text/turtle")
                            .content(memberContent))
                    .andExpect(status().is2xxSuccessful());
        }
    }

    @And("I ingest {int} members of the same version")
    public void ingestSameVersions(int amount) throws Exception {
        versionIncremeter++;
        for (int i = 0; i < amount; i++) {
            String memberContent = readMemberTemplate("data/input/members/mob-hind.template.ttl")
                    .replace("ID", String.valueOf(versionIncremeter))
                    .replace("DATETIME", getCurrentTimestamp());
            mockMvc.perform(post("/" + "mobility-hindrances")
                            .contentType("text/turtle")
                            .content(memberContent))
                    .andExpect(status().is2xxSuccessful());
        }
    }

    @And("verify there are {int} pages")
    public void verifyCreationOfTheFollowingFragments(int i) {
        await().untilAsserted(() -> assertThat(pageEntityRepository.findAll()).hasSize(i));

    }

    @And("verify the following pages have no relation pointing to them")
    public void verifyUpdateOfPredecessorRelations(List<Long> ids) {
        await().untilAsserted(() -> {
            var count = pageRelationEntityRepository.findAll()
                    .stream()
                    .filter(relationEntity -> ids.contains(relationEntity.getToPage().getId()))
                    .count();

            assertThat(count).isZero();
        });
    }

    @And("verify the following pages no longer exist")
    public void verifyRemovalOfPages(List<Long> ids) {
        await().atMost(Duration.of(30, ChronoUnit.SECONDS))
                .untilAsserted(() -> {
                    var count = pageEntityRepository.findAll()
                            .stream()
                            .filter(pageEntity -> ids.contains(pageEntity.getId()))
                            .count();
                    assertThat(count).isZero();
                });
    }

    @And("verify {long} pages have a relation pointing to a compacted page")
    public void verifyUpdateOfPredecessorRelations(long pointingCount) {
        await().untilAsserted(() -> {
            var countNewPage = pageRelationEntityRepository.findAll()
                    .stream()
                    .map(PageRelationEntity::getToPage)
                    .map(PageEntity::getPartialUrl)
                    .map(url -> url.split("pageNumber=")[1])
                    .filter(this::isValidUuid)
                    .count();


            assertThat(countNewPage).isEqualTo(pointingCount);
        });
    }

    @And("verify {long} members are connected to a compacted page")
    public void verifyMembersAreConnectedToCompactedPage(long memberCount) {
        await().untilAsserted(() -> {
            var count = pageMemberEntityRepository.findAll()
                    .stream()
                    .map(PageMemberEntity::getPage)
                    .map(PageEntity::getPartialUrl)
                    .map(url -> url.split("pageNumber=")[1])
                    .filter(this::isValidUuid)
                    .count();

            assertThat(count).isEqualTo(memberCount);
        });
    }

    @And("verify the following pages have no members")
    public void verifyFragmentationOfMembers(List<Long> ids) {
        await().untilAsserted(() -> {
            var count = entityManager.createQuery("SELECT COUNT(*) FROM RetentionPageMemberEntity p where p.pageId IN :ids")
                    .setParameter("ids", ids).getSingleResult();


            assertThat(count).isEqualTo(0L);
        });
    }

    @Then("I wait until no fragments can be compacted for collection {string} and view {string} and capacity per page {int}")
    public void waitUntilNoFragmentsCanBeCompacted(String collection, String view, int capacity) {
        await().atMost(90, SECONDS).untilAsserted(() -> {
            var count = compactionPageEntityRepository.findCompactionCandidates(collection.replace("\"", ""), view.replace("\"", ""), 5).size();
            assertThat(count).isEqualTo(0L);
        });
    }

    @Then("wait until no fragments can be compacted")
    public void waitUntilNoFragmentsCanBeCompacted() {
        await().atMost(30, SECONDS);
    }

    @Then("wait for {int} seconds until compaction has executed at least once")
    public void waitForSecondsUntilCompactionHasExecutedAtLeastOnce(int secondsToWait) {
        await()
                .timeout(secondsToWait + 1, SECONDS)
                .pollDelay(secondsToWait, SECONDS)
                .untilAsserted(() -> assertThat(true).isTrue());
    }

    private String readMemberTemplate(String fileName) throws IOException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.lines(path).collect(Collectors.joining());
    }

    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.[SSS]'Z'"));
    }

    private boolean isValidUuid(String pageNumber) {
        try {
            UUID.fromString(pageNumber);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @And("I start seeding {int} members every {int} seconds")
    public void iStartSeedingMembersEverySeconds(int numberOfMembers, int seconds) {
        seedingTask = executorService.scheduleAtFixedRate(() -> ingestNumberOfVersionObjects(numberOfMembers), 0, seconds, SECONDS);
    }

    private void ingestNumberOfVersionObjects(int numberOfMembers) {
        try {
            String memberTemplate = readMemberTemplate("data/input/members/observation.template.json");
            for (int i = 0; i < numberOfMembers; i++) {
                String memberContent = memberTemplate
                        .replace("ID", String.valueOf(i))
                        .replace("DATETIME", getCurrentTimestamp());
                mockMvc.perform(post("/observations")
                                .contentType(Lang.JSONLD.getHeaderString())
                                .content(memberContent))
                        .andExpect(status().is2xxSuccessful());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Then("I wait until {int} members are ingested")
    public void iWaitUntilMembersAreIngested(int number) {
        await().atMost(Duration.ofMinutes(3))
                .pollInterval(Duration.ofSeconds(15))
                .untilAsserted(() -> assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM members", Long.class)).isEqualTo(number));
    }

    @Then("I stop seeding members")
    public void iStopSeedingMembers() {
        seedingTask.cancel(true);
    }


    @When("I wait until the first page does not exists anymore")
    public void iWaitUntilThePageWithPageNumberDoesNotExistsAnymore() {
        await()
                .atMost(Duration.ofMinutes(2))
                .pollInterval(Duration.ofSeconds(5))
                .untilAsserted(() -> mockMvc.perform(get("/observations/time-based?pageNumber=1"))
                        .andExpect(status().isNotFound()));
    }

    @And("I only have one open page")
    public void iOnlyHaveOneOpenPage() {
        final Long openPageCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM open_pages", Long.class);
        assertThat(openPageCount).isEqualTo(1);
    }

    @Then("the root page points to a compacted page")
    public void theRootPagePointsToACompactedPage() throws Exception {
        final var response = mockMvc.perform(get("/observations/time-based").accept(Lang.NQ.getHeaderString()))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse();
        final String pageNumber = new ResponseToModelConverter(response).convert()
                .listSubjectsWithProperty(RDF.type, ResourceFactory.createProperty("https://w3id.org/tree#Relation"))
                .nextResource()
                .listProperties(ResourceFactory.createProperty("https://w3id.org/tree#node"))
                .nextStatement()
                .getResource()
                .getLocalName();
        assertThatNoException().isThrownBy(() -> UUID.fromString(pageNumber));
    }

    @And("I ingest {int} members of version {int}")
    public void iIngestMembersOfVersion(int numberOfMembers, int arg1) {
        try {
            String memberTemplate = readMemberTemplate("data/input/members/observation.template.json");
            for (int i = 0; i < numberOfMembers; i++) {
                String memberContent = memberTemplate
                        .replace("ID", String.valueOf(i))
                        .replace("DATETIME", getCurrentTimestamp());
                mockMvc.perform(post("/observations")
                                .contentType(Lang.JSONLD.getHeaderString())
                                .content(memberContent))
                        .andExpect(status().is2xxSuccessful());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @And("I ingest {int} members of version {int} of template {string} for collection {string}")
    public void iIngestMembersOfVersionOfTemplate(int numberOfMembers, int version, String template, String endpoint) {
        try {
            String memberTemplate = readMemberTemplate(template);
            for (int i = 0; i < numberOfMembers; i++) {
                String memberContent = memberTemplate
                        .replace("ID", String.valueOf(version))
                        .replace("TIMESTAMP", getCurrentTimestamp());
                mockMvc.perform(post("/" + endpoint)
                                .contentType(Lang.TTL.getHeaderString())
                                .content(memberContent))
                        .andExpect(status().is2xxSuccessful());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
