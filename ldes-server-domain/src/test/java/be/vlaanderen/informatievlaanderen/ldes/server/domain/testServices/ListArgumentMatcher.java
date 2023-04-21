package be.vlaanderen.informatievlaanderen.ldes.server.domain.testServices;

import org.mockito.ArgumentMatcher;

import java.util.List;

public class ListArgumentMatcher implements ArgumentMatcher<List> {

	private final List expectedList;

	public ListArgumentMatcher(List expectedList) {
		this.expectedList = expectedList;
	}

	@Override
	public boolean matches(List actualList) {
		return expectedList.containsAll(actualList) && actualList.containsAll(expectedList);
	}
}