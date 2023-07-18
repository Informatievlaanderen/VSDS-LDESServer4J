package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class LdesFragment {

	private final ViewName viewName;
	private final List<FragmentPair> fragmentPairs;
	private Boolean immutable;
	private final int numberOfMembers;
	private final List<TreeRelation> relations;

	public LdesFragment(ViewName viewName, final List<FragmentPair> fragmentPairs) {
		this(viewName, fragmentPairs, false, 0, new ArrayList<>());
	}

	public LdesFragment(ViewName viewName, List<FragmentPair> fragmentPairs, Boolean immutable, int numberOfMembers,
			List<TreeRelation> relations) {
		this.viewName = viewName;
		this.fragmentPairs = fragmentPairs;
		this.immutable = immutable;
		this.numberOfMembers = numberOfMembers;
		this.relations = relations;
	}

	public String getFragmentId() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("/").append(viewName.asString());

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
	}

	public boolean isImmutable() {
		return this.immutable;
	}

	public LdesFragment createChild(FragmentPair fragmentPair) {
		ArrayList<FragmentPair> childFragmentPairs = new ArrayList<>(this.fragmentPairs.stream().toList());
		childFragmentPairs.add(fragmentPair);
		return new LdesFragment(getViewName(), childFragmentPairs);
	}

	public Optional<String> getValueOfKey(String key) {
		return this.fragmentPairs.stream()
				.filter(fragmentPair -> fragmentPair.fragmentKey().equals(key))
				.map(FragmentPair::fragmentValue).findFirst();
	}

	public ViewName getViewName() {
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
					.append("/").append(viewName.asString());
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

	public void addRelation(TreeRelation relation) {
		relations.add(relation);
	}

	public boolean containsRelation(TreeRelation parentChildRelation) {
		return relations.contains(parentChildRelation);
	}

	public List<TreeRelation> getRelations() {
		return relations;
	}

	public boolean isRoot() {
		return this.fragmentPairs.isEmpty();
	}

}
