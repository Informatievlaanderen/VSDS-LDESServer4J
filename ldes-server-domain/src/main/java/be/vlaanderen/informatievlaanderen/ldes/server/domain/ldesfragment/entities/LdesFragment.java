package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LdesFragment {

	private final FragmentInfo fragmentInfo;

	public LdesFragment(final FragmentInfo fragmentInfo) {
		this.fragmentInfo = fragmentInfo;
	}

	public String getFragmentId() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("/").append(getViewName());

		List<FragmentPair> fragmentPairs = getFragmentPairs();
		if (!fragmentPairs.isEmpty()) {
			stringBuilder.append("?");
			stringBuilder.append(fragmentPairs.stream()
					.map(fragmentPair -> fragmentPair.fragmentKey() + "=" + fragmentPair.fragmentValue())
					.collect(Collectors.joining("&")));
		}

		return stringBuilder.toString();
	}

	public List<FragmentPair> getFragmentPairs() {
		return this.fragmentInfo.getFragmentPairs();
	}

	public FragmentInfo getFragmentInfo() {
		return fragmentInfo;
	}

	public void makeImmutable() {
		this.fragmentInfo.makeImmutable();
	}

	public void setSoftDeleted(boolean softDeleted) {
		this.fragmentInfo.setSoftDeleted(softDeleted);
	}

	public boolean isImmutable() {
		return this.fragmentInfo.getImmutable();
	}

	public LdesFragment createChild(FragmentPair fragmentPair) {
		return new LdesFragment(fragmentInfo.createChild(fragmentPair));
	}

	public boolean isSoftDeleted() {
		return this.fragmentInfo.getSoftDeleted();
	}

	public LocalDateTime getImmutableTimestamp() {
		return this.fragmentInfo.getImmutableTimestamp();
	}

	public Optional<String> getValueOfKey(String key) {
		return this.fragmentInfo.getFragmentPairs().stream()
				.filter(fragmentPair -> fragmentPair.fragmentKey().equals(key))
				.map(FragmentPair::fragmentValue).findFirst();
	}

	public String getViewName() {
		return this.fragmentInfo.getViewName();
	}

	public int getNumberOfMembers() {
		return this.fragmentInfo.getNumberOfMembers();
	}

	public String getParentId() {

		if (!this.getFragmentInfo().getFragmentPairs().isEmpty()) {
			List<FragmentPair> parentPairs = new ArrayList<>(this.getFragmentInfo().getFragmentPairs());
			parentPairs.remove(parentPairs.size() - 1);
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder
					.append("/").append(getViewName());
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
}
