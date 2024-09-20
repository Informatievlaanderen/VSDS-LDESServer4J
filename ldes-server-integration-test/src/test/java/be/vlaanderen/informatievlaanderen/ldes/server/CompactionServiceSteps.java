package be.vlaanderen.informatievlaanderen.ldes.server;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.PageRelationEntity;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

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

	@And("verify the following pages have no members")
	public void verifyFragmentationOfMembers(List<Long> ids) {
		await().untilAsserted(() -> {
			var count = entityManager.createQuery("SELECT COUNT(*) FROM PageMemberEntity p where p.page.id IN :ids")
					.setParameter("ids", ids).getSingleResult();


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
}
