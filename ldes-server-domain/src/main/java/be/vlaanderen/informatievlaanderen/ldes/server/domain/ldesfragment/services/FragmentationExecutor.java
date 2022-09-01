package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import org.springframework.cloud.sleuth.annotation.NewSpan;

public interface FragmentationExecutor {

	@NewSpan
	void executeFragmentation(String memberId);
}
