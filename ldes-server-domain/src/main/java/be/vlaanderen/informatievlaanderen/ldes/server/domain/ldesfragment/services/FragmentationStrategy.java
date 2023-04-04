package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import io.micrometer.observation.Observation;

public interface FragmentationStrategy {

	void addMemberToFragment(LdesFragment parentFragment, Member member, Observation parentObservation);
}