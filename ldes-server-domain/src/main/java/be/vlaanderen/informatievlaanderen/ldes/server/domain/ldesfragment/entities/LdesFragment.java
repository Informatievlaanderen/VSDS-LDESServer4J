package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LdesFragment {

	private final LdesFragmentIdentifier identifier;
	private Boolean immutable;
	private final int numberOfMembers;
	private final List<TreeRelation> relations;

	public LdesFragment(LdesFragmentIdentifier identifier) {
		this(identifier, false, 0, new ArrayList<>());
	}

	public LdesFragment(LdesFragmentIdentifier identifier, Boolean immutable, int numberOfMembers,
			List<TreeRelation> relations) {
		this.identifier = identifier;
		this.immutable = immutable;
		this.numberOfMembers = numberOfMembers;
		this.relations = relations;
	}

	public LdesFragmentIdentifier getFragmentId() {
		return identifier;
	}

	public List<FragmentPair> getFragmentPairs() {
		return this.identifier.getFragmentPairs();
	}

	public void makeImmutable() {
		this.immutable = true;
	}

	public boolean isImmutable() {
		return this.immutable;
	}

	public LdesFragment createChild(FragmentPair fragmentPair) {
		ArrayList<FragmentPair> childFragmentPairs = new ArrayList<>(
				this.identifier.getFragmentPairs().stream().toList());
		childFragmentPairs.add(fragmentPair);
		return new LdesFragment(new LdesFragmentIdentifier(getViewName(), childFragmentPairs));
	}

	public Optional<String> getValueOfKey(String key) {
		return this.identifier.getValueOfFragmentPairKey(key);
	}

	public ViewName getViewName() {
		return this.identifier.getViewName();
	}

	public int getNumberOfMembers() {
		return this.numberOfMembers;
	}

	public Optional<LdesFragmentIdentifier> getParentId() {
		return identifier.getParentId();
	}

	public String getParentIdAsString() {
		return identifier.getParentId().map(LdesFragmentIdentifier::asString).orElseGet(() -> "root");
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
		return this.identifier.getFragmentPairs().isEmpty();
	}

}
