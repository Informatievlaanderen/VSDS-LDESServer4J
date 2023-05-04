package be.vlaanderen.informatievlaanderen.ldes.server.domain.testServices;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import org.mockito.ArgumentMatcher;

import java.util.List;

public class ListArgumentMatcher implements ArgumentMatcher<List<LdesFragment>> {

	private final List<LdesFragment> expectedList;

	public ListArgumentMatcher(List<LdesFragment> expectedList) {
		this.expectedList = expectedList;
	}

	@Override
	public boolean matches(List<LdesFragment> actualList) {
		return expectedList.containsAll(actualList) && actualList.containsAll(expectedList);
	}
}