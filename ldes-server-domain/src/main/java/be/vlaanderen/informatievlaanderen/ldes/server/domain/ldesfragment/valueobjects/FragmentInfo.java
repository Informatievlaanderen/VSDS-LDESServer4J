package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import java.util.List;
import java.util.Optional;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

public class FragmentInfo {

	private final String collectionName;
	private final String viewName;
	private final List<FragmentPair> fragmentPairs;
	private Boolean immutable;

	public FragmentInfo(String collectionName, String viewName, List<FragmentPair> fragmentPairs) {
		this.collectionName = collectionName;
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
		this.immutable = false;
	}

	public Optional<String> getValueOfKey(String key) {
		return fragmentPairs
				.stream()
				.filter(fragmentPair -> fragmentPair.fragmentKey().equals(key))
				.map(FragmentPair::fragmentValue)
				.findFirst();
	}

	public List<FragmentPair> getFragmentPairs() {
		return fragmentPairs;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public String getViewName() {
		return viewName;
	}

	public Boolean getImmutable() {
		return immutable;
	}

	public void setImmutable(Boolean immutable) {
		this.immutable = immutable;
	}
}
