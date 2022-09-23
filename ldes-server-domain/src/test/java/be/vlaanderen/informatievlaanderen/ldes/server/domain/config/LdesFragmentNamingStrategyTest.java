package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LdesFragmentNamingStrategyTest {

	@Test
	void when_FragmentNameIsGenerated_HostNameCollectionNameAndFragmentPairsAreFormatted() {
		List<FragmentPair> fragmentPairs = List.of(new FragmentPair("tile", "0/1/2"),
				new FragmentPair("generatedAtTime", "2020-12-05T09:00:00.000Z"));

		String fragmentId = LdesFragmentNamingStrategy.generateFragmentName("http://localhost:8080", "firstView",
				fragmentPairs);

		String expectedFragmentId = "http://localhost:8080/firstView?tile=0/1/2&generatedAtTime=2020-12-05T09:00:00.000Z";
		assertEquals(expectedFragmentId, fragmentId);
	}
}
