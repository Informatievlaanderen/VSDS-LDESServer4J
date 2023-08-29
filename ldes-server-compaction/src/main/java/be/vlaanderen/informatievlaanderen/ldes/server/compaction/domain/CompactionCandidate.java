package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

public class CompactionCandidate {
	private final Fragment firstFragment;
	private final Fragment secondFragment;

	public CompactionCandidate(Fragment firstFragment, Fragment secondFragment) {
		this.firstFragment = firstFragment;
		this.secondFragment = secondFragment;
	}

	public Fragment getFirstFragment() {
		return firstFragment;
	}

	public Fragment getSecondFragment() {
		return secondFragment;
	}
}
