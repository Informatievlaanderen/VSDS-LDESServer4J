package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompactionCandidateTest {

	@Test
	void getFragmentWhenNotInitialised() {
		String fragmentId = "/event-stream/view";
		CompactionCandidate compactionCandidate = new CompactionCandidate(fragmentId, 10);
		assertThrows(RuntimeException.class, compactionCandidate::getFragment);

		Fragment fragment = new Fragment(LdesFragmentIdentifier.fromFragmentId(fragmentId));

		compactionCandidate.setFragment(fragment);
		assertEquals(fragment, compactionCandidate.getFragment());
	}

}