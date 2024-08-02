package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("java:S3415")
public class CompactionServiceSteps extends LdesServerIntegrationTest {
    @And("the members are ingested")
    public void theFollowingPagesAreAvailable() throws Exception {
        List<Integer> ids = new ArrayList<>();
        ids.addAll(IntStream.rangeClosed(1, 18)
                .boxed().toList());
        for (int i = 0; i < 10; i++) {
            ids.add(19);
        }
        for (int i = 0; i < 4; i++) {
            ids.add(20);
        }
        for (int i = 0; i < 3; i++) {
            ids.add(20);
        }
        for (int i = 0; i < 5; i++) {
            ids.add(21);
        }
        ids.addAll(IntStream.rangeClosed(22, 32)
                .boxed().toList());
        for (int id : ids) {
            String memberContent = readMemberTemplate("data/input/members/mob-hind.template.ttl")
                    .replace("ID", String.valueOf(id))
                    .replace("DATETIME", getCurrentTimestamp());
            mockMvc.perform(post("/" + "mobility-hindrances")
                            .contentType("text/turtle")
                            .content(memberContent))
                    .andExpect(status().is2xxSuccessful());
        }
    }

    @And("verify there are {int} pages")
    public void verifyCreationOfTheFollowingFragments(long i) {
        assertThat(entityManager.createQuery("SELECT COUNT(*) FROM PageEntity p").getSingleResult()).isEqualTo(i);
    }

    @And("verify update of predecessor relations")
    public void verifyUpdateOfPredecessorRelations(List<Long> ids) {
        var count = entityManager.createQuery("SELECT COUNT(*) FROM RelationEntity r JOIN PageEntity p ON r.toPage = p WHERE p.id IN :ids")
                .setParameter("ids", ids).getSingleResult();
        assertThat(count).isEqualTo(0L);
        var countNewPage = entityManager.createQuery("SELECT COUNT(*) FROM RelationEntity r JOIN PageEntity p ON r.toPage = p WHERE p.id = :id")
                .setParameter("id", 6L).getSingleResult();
        assertThat(countNewPage).isEqualTo(4L);
    }

    @And("verify fragmentation of members")
    public void verifyFragmentationOfMembers(List<Long> ids) {
        var count = entityManager.createQuery("SELECT COUNT(*) FROM PageMemberEntity p where p.page.id IN :ids")
                .setParameter("ids", ids).getSingleResult();
        assertThat(count).isEqualTo(0L);
    }

    @Then("wait for {int} seconds until compaction has executed at least once")
    public void waitForSecondsUntilCompactionHasExecutedAtLeastOnce(int secondsToWait) {
        await()
                .timeout(secondsToWait + 1, SECONDS)
                .pollDelay(secondsToWait, SECONDS)
                .untilAsserted(() -> assertThat(true).isTrue());
    }

    public record FragmentAllocations(String fragmentId, List<MemberAllocation> memberAllocations) {
    }

    public record MemberFragmentations(String fragmentId, List<String> memberIds) {
    }

    private String readMemberTemplate(String fileName) throws IOException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
        return Files.lines(path).collect(Collectors.joining());
    }
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.[SSS]'Z'"));
    }
}
