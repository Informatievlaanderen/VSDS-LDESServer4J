package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.exception.GreaterOrEqualsPageFilterException;

import java.util.function.Predicate;

public class GreaterOrEqualsPageFilter implements Predicate<Fragment> {
	public static final String PAGE_NUMBER_KEY = "pageNumber";
	private final String page;

	public GreaterOrEqualsPageFilter(LdesFragmentIdentifier lastFragment) {
		page = lastFragment.getValueOfFragmentPairKey(PAGE_NUMBER_KEY)
				.orElseThrow(() -> new GreaterOrEqualsPageFilterException(lastFragment.asString()));
	}

	@Override
	public boolean test(Fragment fragment) {
		return Integer.parseInt(fragment.getValueOfKey(PAGE_NUMBER_KEY).orElseGet(() -> "0")) >= Integer
				.parseInt(page);
	}
}
