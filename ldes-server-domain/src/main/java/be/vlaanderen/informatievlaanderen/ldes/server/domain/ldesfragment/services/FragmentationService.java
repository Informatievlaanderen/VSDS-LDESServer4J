package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import org.springframework.cloud.sleuth.annotation.NewSpan;

public interface FragmentationService {
	@NewSpan("fragmenter")
	void addMemberToFragment(LdesFragment parentFragment, String ldesMemberId);
}