package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class LdesFragment {

	private final String viewName;
	private final List<FragmentPair> fragmentPairs;
	private Boolean immutable;
	private LocalDateTime immutableTimestamp;
	private Boolean softDeleted;
	private final int numberOfMembers;

	public Optional<LdesFragment> getPrev() {
		return Optional.ofNullable(prev);
	}

	private void setPrev(LdesFragment prev) {
		this.prev = prev;
	}

	public Optional<LdesFragment> getNext() {
		return ofNullable(next);
	}

	private void setNext(LdesFragment next) {
		this.next = next;
	}

	private LdesFragment prev;

	private LdesFragment next;

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
		this.prev = null;
		this.next = null;
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

	public void linkPrevFragment(LdesFragment prevFragment) {
		this.setPrev(prevFragment);
		prevFragment.setNext(this);
	}
}
