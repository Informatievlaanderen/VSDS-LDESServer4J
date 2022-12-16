package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

public class LdesFragment {

	private final String fragmentId;

	private final FragmentInfo fragmentInfo;

	public LdesFragment(final FragmentInfo fragmentInfo) {
		this.fragmentInfo = fragmentInfo;
		this.fragmentId = fragmentInfo.generateFragmentId();
	}

	public String getFragmentId() {
		return fragmentId;
	}

	public FragmentInfo getFragmentInfo() {
		return fragmentInfo;
	}

	public int getCurrentNumberOfMembers() {
		return fragmentInfo.getNumberOfMembers();
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
		return this.getFragmentInfo().getSoftDeleted();
	}
}
