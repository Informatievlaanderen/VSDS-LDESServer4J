package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.springframework.cloud.sleuth.Span;

public interface FragmentationStrategy {

	void addMemberToFragment(LdesFragment parentFragment, Member member, Span parentSpan);
}