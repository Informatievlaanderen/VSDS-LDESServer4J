package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;

import java.util.Optional;

public interface PaginationExecutor {

	Optional<LdesFragment> getLastFragment();

	void setLastFragment(LdesFragment lastFragment);

	void linkFragments(LdesFragment fragment);
}
