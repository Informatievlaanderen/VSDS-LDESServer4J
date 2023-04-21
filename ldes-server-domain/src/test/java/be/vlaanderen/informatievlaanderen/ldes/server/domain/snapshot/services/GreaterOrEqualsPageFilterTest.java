package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services.GreaterOrEqualsPageFilter.PAGE_NUMBER_KEY;
import static org.junit.jupiter.api.Assertions.*;

class GreaterOrEqualsPageFilterTest {

	LdesFragment fragmentRoot;
	LdesFragment fragment1;
	LdesFragment fragment2;
	LdesFragment fragment3;
	GreaterOrEqualsPageFilter filter;

	@BeforeEach
	void setUp() {
		fragmentRoot = new LdesFragment("collection", "view", List.of());
		fragment1 = fragmentRoot.createChild(new FragmentPair(PAGE_NUMBER_KEY, "1"));
		fragment2 = fragmentRoot.createChild(new FragmentPair(PAGE_NUMBER_KEY, "2"));
		fragment3 = fragmentRoot.createChild(new FragmentPair(PAGE_NUMBER_KEY, "3"));
	}

	@Test
	void when_PageNrSmaller_False() {
		filter = new GreaterOrEqualsPageFilter(fragment2.getFragmentId());
		assertFalse(filter.test(fragment1));
	}

	@Test
	void when_PageNrBigger_Then_True() {
		filter = new GreaterOrEqualsPageFilter(fragment2.getFragmentId());
		assertTrue(filter.test(fragment3));
	}

	@Test
	void when_Root_Then_False() {
		filter = new GreaterOrEqualsPageFilter(fragment2.getFragmentId());
		assertFalse(filter.test(fragmentRoot));
	}

}