package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FragmentSequenceTest {

	@Test
	void createNeverProcessedSequence() {
		ViewName viewName = ViewName.fromString("col/view");
		var sequence = FragmentSequence.createNeverProcessedSequence(viewName);

		assertEquals(viewName, sequence.viewName());
		assertEquals(-1, sequence.sequenceNr());
	}
}