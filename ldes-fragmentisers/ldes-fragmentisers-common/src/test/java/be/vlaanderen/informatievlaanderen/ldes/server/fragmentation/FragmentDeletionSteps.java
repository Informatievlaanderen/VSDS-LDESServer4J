package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.GENERIC_TREE_RELATION;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("java:S3415")
public class FragmentDeletionSteps extends FragmentDeletionIntegrationTest {

	@DataTableType(replaceWithEmptyString = "[blank]")
	public Fragment FragmentEntryTransformer(Map<String, String> row) {

		return new Fragment(
				LdesFragmentIdentifier.fromFragmentId(row.get("fragmentIdentifier")),
				Boolean.parseBoolean(row.get("immutable")), Integer.parseInt(row.get("nrOfMembersAdded")),
				row.get("relation").isEmpty() ? new ArrayList<>()
						: Arrays.stream(row.get("relation").split(",")).map(treeNode -> new TreeRelation("",
								LdesFragmentIdentifier.fromFragmentId(treeNode), "", "", GENERIC_TREE_RELATION))
						.toList(),
				getDeleteTime(row.get("daysUntilDeletion")));
	}

	@Given("the following Fragments are present")
	public void theFollowingFragmentsArePresent(List<Fragment> fragments) {
		Stream<Fragment> deletionCandidates = fragments.stream().filter(fragment -> fragment.getDeleteTime() != null);

//		when(fragmentRepository.getDeletionCandidates()).thenReturn(deletionCandidates);
	}

	private LocalDateTime getDeleteTime(String daysUntilDeletion) {
		int daysUntilDeletionInteger = Integer.parseInt(daysUntilDeletion);
		return LocalDateTime.now().plusDays(daysUntilDeletionInteger);
	}

	@Then("wait for {int} seconds until fragment deletion has executed at least once")
	public void waitForSecondsUntilFragmentDeletionHasExecutedAtLeastOnce(int secondsToWait) {
		await()
				.timeout(secondsToWait + 1, SECONDS)
				.pollDelay(secondsToWait, SECONDS)
				.untilAsserted(() -> assertThat(true).isTrue());
	}

	@And("verify the deletion of the following fragments")
	public void verifyTheDeletionOfTheFollowingFragments(List<String> fragmentIds) {
//		fragmentIds.forEach(fragmentId -> verify(fragmentRepository).removeRelationsPointingToFragmentAndDeleteFragment(
//				argThat(fragment -> fragment.asDecodedFragmentId().equals(fragmentId))
//		));
	}
}
