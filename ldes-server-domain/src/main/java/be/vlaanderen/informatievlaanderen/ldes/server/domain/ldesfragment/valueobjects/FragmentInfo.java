package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentInfo {

	private final String viewName;
	private final List<FragmentPair> fragmentPairs;
	private Boolean immutable;
	private LocalDateTime immutableTimestamp;

	private Boolean softDeleted;

	private final int numberOfMembers;

	public FragmentInfo(final String viewName, final List<FragmentPair> fragmentPairs) {
		this(viewName, fragmentPairs, false, null, false, 0);
	}

	public FragmentInfo(String viewName, List<FragmentPair> fragmentPairs, Boolean immutable,
			LocalDateTime immutableTimestamp, Boolean softDeleted, int numberOfMembers) {
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
		this.immutable = immutable;
		this.immutableTimestamp = immutableTimestamp;
		this.softDeleted = softDeleted;
		this.numberOfMembers = numberOfMembers;
	}

	public List<FragmentPair> getFragmentPairs() {
		return fragmentPairs;
	}

	public String getViewName() {
		return viewName;
	}

	public Boolean getImmutable() {
		return immutable;
	}

	public LocalDateTime getImmutableTimestamp() {
		return immutableTimestamp;
	}

	public void makeImmutable() {
		this.immutable = true;
		this.immutableTimestamp = LocalDateTime.now();
	}

	public Boolean getSoftDeleted() {
		return softDeleted;
	}

	public void setSoftDeleted(Boolean softDeleted) {
		this.softDeleted = softDeleted;
	}

	public int getNumberOfMembers() {
		return numberOfMembers;
	}

	public FragmentInfo createChild(FragmentPair fragmentPair) {
		ArrayList<FragmentPair> childFragmentPairs = new ArrayList<>(this.fragmentPairs.stream().toList());
		childFragmentPairs.add(fragmentPair);
		return new FragmentInfo(viewName, childFragmentPairs);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FragmentInfo that = (FragmentInfo) o;
		return Objects.equals(viewName, that.viewName) && Objects.equals(fragmentPairs, that.fragmentPairs)
				&& Objects.equals(immutable, that.immutable)
				&& Objects.equals(immutableTimestamp, that.immutableTimestamp)
				&& Objects.equals(softDeleted, that.softDeleted);
	}

	@Override
	public int hashCode() {
		return Objects.hash(viewName, fragmentPairs, immutable, immutableTimestamp, softDeleted);
	}

}
