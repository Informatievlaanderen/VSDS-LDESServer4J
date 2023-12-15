package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

public class CompactionCandidate {
	private final String id;
	private final Integer size;
	private Fragment fragment;

	public CompactionCandidate(String id, Integer size) {
		this.id = id;
		this.size = size;
	}

	public Fragment getFragment() {
		if (fragment == null) {
			throw new RuntimeException("Fragment has not yet been initialized");
		}
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

	public String getId() {
		return id;
	}

	public Integer getSize() {
		return size;
	}
}
