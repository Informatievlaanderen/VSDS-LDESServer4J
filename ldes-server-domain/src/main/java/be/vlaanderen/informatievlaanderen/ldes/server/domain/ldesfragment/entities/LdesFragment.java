package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class LdesFragment {

	private final String viewName;
	private final List<FragmentPair> fragmentPairs;
	private Boolean immutable;
	private LocalDateTime immutableTimestamp;
	private Boolean softDeleted;
	private final int numberOfMembers;

	public LdesFragment(final String viewName, final List<FragmentPair> fragmentPairs) {
		this(viewName, fragmentPairs, false, null, false, 0);
	}

	public LdesFragment(String viewName, List<FragmentPair> fragmentPairs, Boolean immutable,
			LocalDateTime immutableTimestamp, Boolean softDeleted, int numberOfMembers) {
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
		this.immutable = immutable;
		this.immutableTimestamp = immutableTimestamp;
		this.softDeleted = softDeleted;
		this.numberOfMembers = numberOfMembers;
	}

	public String getFragmentId() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("/").append(viewName);

		if (!fragmentPairs.isEmpty()) {
			stringBuilder.append("?");
			stringBuilder.append(fragmentPairs.stream()
					.map(fragmentPair -> fragmentPair.fragmentKey() + "=" + fragmentPair.fragmentValue())
					.collect(Collectors.joining("&")));
		}

		return stringBuilder.toString();
	}

	public List<FragmentPair> getFragmentPairs() {
		return this.fragmentPairs;
	}

	public void makeImmutable() {
		this.immutable = true;
		this.immutableTimestamp = LocalDateTime.now();
	}

	public void setSoftDeleted(boolean softDeleted) {
		this.softDeleted = softDeleted;
	}

	public boolean isImmutable() {
		return this.immutable;
	}

	public LdesFragment createChild(FragmentPair fragmentPair) {
		ArrayList<FragmentPair> childFragmentPairs = new ArrayList<>(this.fragmentPairs.stream().toList());
		childFragmentPairs.add(fragmentPair);
		return new LdesFragment(getViewName(), childFragmentPairs);
	}

	public boolean isSoftDeleted() {
		return this.softDeleted;
	}

	public LocalDateTime getImmutableTimestamp() {
		return this.immutableTimestamp;
	}

	public Optional<String> getValueOfKey(String key) {
		return this.fragmentPairs.stream()
				.filter(fragmentPair -> fragmentPair.fragmentKey().equals(key))
				.map(FragmentPair::fragmentValue).findFirst();
	}

	public String getViewName() {
		return this.viewName;
	}

	public int getNumberOfMembers() {
		return this.numberOfMembers;
	}

	public String getParentId() {

		if (!this.fragmentPairs.isEmpty()) {
			List<FragmentPair> parentPairs = new ArrayList<>(fragmentPairs);
			parentPairs.remove(parentPairs.size() - 1);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder
					.append("/").append(viewName);
			if (!parentPairs.isEmpty()) {

				stringBuilder.append("?");
				stringBuilder
						.append(parentPairs.stream().map(fragmentPair -> fragmentPair.fragmentKey() +
								"=" + fragmentPair.fragmentValue()).collect(Collectors.joining("&")));
			}
			return stringBuilder.toString();
		}

		return "root";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LdesFragment that = (LdesFragment) o;
		return Objects.equals(getFragmentId(), that.getFragmentId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getFragmentId());
	}

}
