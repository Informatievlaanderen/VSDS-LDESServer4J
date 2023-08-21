package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Fragment {

	public static final String ROOT = "root";
	private final LdesFragmentIdentifier identifier;
	private Boolean immutable;
	private final int numberOfMembers;
	private final List<TreeRelation> relations;

	public Fragment(LdesFragmentIdentifier identifier) {
		this(identifier, false, 0, new ArrayList<>());
	}

	public Fragment(LdesFragmentIdentifier identifier, Boolean immutable, int numberOfMembers,
			List<TreeRelation> relations) {
		this.identifier = identifier;
		this.immutable = immutable;
		this.numberOfMembers = numberOfMembers;
		this.relations = relations;
	}

	public LdesFragmentIdentifier getFragmentId() {
		return identifier;
	}

	public String getFragmentIdString() {
		return identifier.asString();
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

	public Fragment createChild(FragmentPair fragmentPair) {
		ArrayList<FragmentPair> childFragmentPairs = new ArrayList<>(
				this.identifier.getFragmentPairs().stream().toList());
		childFragmentPairs.add(fragmentPair);
		return new Fragment(new LdesFragmentIdentifier(getViewName(), childFragmentPairs));
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
		return identifier.getParentId().map(LdesFragmentIdentifier::asString).orElseGet(() -> ROOT);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Fragment that = (Fragment) o;
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
