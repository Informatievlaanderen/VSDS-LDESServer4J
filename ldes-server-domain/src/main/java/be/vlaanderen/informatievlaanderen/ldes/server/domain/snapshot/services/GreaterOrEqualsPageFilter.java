package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.exception.GreaterOrEqualsPageFilterException;

import java.util.function.Predicate;

public class GreaterOrEqualsPageFilter implements Predicate<LdesFragment> {
	public static final String PAGE_NUMBER_KEY = "pageNumber";
	private final String page;

	public GreaterOrEqualsPageFilter(String lastFragment) {
		page = LdesFragmentIdentifier.fromFragmentId(lastFragment).getValueOfFragmentPairKey(PAGE_NUMBER_KEY)
				.orElseThrow(() -> new GreaterOrEqualsPageFilterException(lastFragment));
	}

	@Override
	public boolean test(LdesFragment ldesFragment) {
		return Integer.parseInt(ldesFragment.getValueOfKey(PAGE_NUMBER_KEY).orElseGet(() -> "0")) >= Integer
				.parseInt(page);
	}
}
