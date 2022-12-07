package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.util.ArrayList;
import java.util.List;

public class LdesFragment {

	private final String fragmentId;

	private final FragmentInfo fragmentInfo;

	private final List<TreeRelation> relations;

	public LdesFragment(final FragmentInfo fragmentInfo) {
		this.fragmentInfo = fragmentInfo;
		this.relations = new ArrayList<>();
		this.fragmentId = fragmentInfo.generateFragmentId();
	}

	public void addRelation(TreeRelation treeRelation) {
		this.relations.add(treeRelation);
	}

	public String getFragmentId() {
		return fragmentId;
	}

	public FragmentInfo getFragmentInfo() {
		return fragmentInfo;
	}

	public List<TreeRelation> getRelations() {
		return relations;
	}

//	public void addMember(String ldesMemberId) {
//		memberIds.add(ldesMemberId);
//	}

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

	public void removeRelation(TreeRelation treeRelation) {
		relations.remove(treeRelation);
	}

	public boolean isSoftDeleted() {
		return this.getFragmentInfo().getSoftDeleted();
	}
}
