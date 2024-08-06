package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("java:S3415")
public class CompactionServiceSteps extends LdesServerIntegrationTest {
	private int versionIncremeter = 1;

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
	public void verifyCreationOfTheFollowingFragments(long i) {
		assertThat(entityManager.createQuery("SELECT COUNT(*) FROM PageEntity p").getSingleResult()).isEqualTo(i);
	}

	@And("verify the following pages have no relation pointing to them")
	public void verifyUpdateOfPredecessorRelations(List<Long> ids) {
		var count = entityManager.createQuery("SELECT COUNT(*) FROM RelationEntity r JOIN PageEntity p ON r.toPage = p WHERE p.id IN :ids")
				.setParameter("ids", ids).getSingleResult();
		assertThat(count).isEqualTo(0L);
	}

	@And("verify the following pages no longer exist")
	public void verifyRemovalOfPages(List<Long> ids) {
		await().untilAsserted(() -> {
			var count = entityManager.createQuery("SELECT COUNT(*) FROM PageEntity p WHERE p.id IN :ids")
					.setParameter("ids", ids).getSingleResult();
			assertThat(count).isEqualTo(0L);
		});

	}

	@And("verify the pages have a relation pointing to the new page {long}")
	public void verifyUpdateOfPredecessorRelations(long id) {
		var countNewPage = entityManager.createQuery("SELECT COUNT(*) FROM RelationEntity r JOIN PageEntity p ON r.toPage = p WHERE p.id = :id")
				.setParameter("id", id).getSingleResult();
		assertThat(countNewPage).isEqualTo(3L);
	}

	@And("verify the following pages have no members")
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

	@Then("wait for {int} seconds until deletion has executed at least once")
	public void waitForSecondsUntilDeltionHasExecutedAtLeastOnce(int secondsToWait) {
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
