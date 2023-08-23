package be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

public class FragmentsCompactedEvent {
	private final Fragment firstFragment;
	private final Fragment secondFragment;

	public FragmentsCompactedEvent(Fragment firstFragment, Fragment secondFragment) {
		this.firstFragment = firstFragment;
		this.secondFragment = secondFragment;
	}
}
