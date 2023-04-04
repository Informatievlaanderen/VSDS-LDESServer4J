package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SnapshotFragmenterImpl implements SnapshotFragmenter {

	private final FragmentationStrategy fragmentationStrategy;
	private final ObservationRegistry observationRegistry;

	public SnapshotFragmenterImpl(@Qualifier("snapshot-fragmentation") FragmentationStrategy fragmentationStrategy,
			ObservationRegistry observationRegistry) {
		this.fragmentationStrategy = fragmentationStrategy;
		this.observationRegistry = observationRegistry;
	}

	public void fragmentSnapshotMembers(Set<Member> membersOfSnapshot, LdesFragment rootTreeNodeOfSnapshot) {
		Observation parentObservation = Observation.createNotStarted("execute snapshot fragmentation",
				observationRegistry)
				.start();
		membersOfSnapshot.forEach(
				member -> fragmentationStrategy.addMemberToFragment(rootTreeNodeOfSnapshot, member, parentObservation));
		parentObservation.stop();
	}
}
