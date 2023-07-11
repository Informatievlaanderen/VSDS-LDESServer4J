package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.FragmentationStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
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

	public void fragmentSnapshotMembers(Set<Member> membersOfSnapshot, Fragment rootTreeNodeOfSnapshot) {
		Observation parentObservation = Observation.createNotStarted("execute snapshot fragmentation",
				observationRegistry)
				.start();
		membersOfSnapshot.forEach(
				member -> fragmentationStrategy.addMemberToFragment(rootTreeNodeOfSnapshot, member.getLdesMemberId(),
						member.getModel(), parentObservation));
		parentObservation.stop();
	}
}
