package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LdesFragmentNamingStrategyTest {

	@Test
	void when_FragmentNameIsGenerated_HostNameCollectionNameAndFragmentPairsAreFormatted() {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setCollectionName("mobility-hindrances");
		ldesConfig.setHostName("http://localhost:8080");
		FragmentInfo fragmentInfo = new FragmentInfo(ldesConfig.getCollectionName(),
				List.of(new FragmentPair("tile", "0/1/2"),
						new FragmentPair("generatedAtTime", "2020-12-05T09:00:00.000Z")));

		String fragmentId = LdesFragmentNamingStrategy.generateFragmentName(ldesConfig, fragmentInfo);

		String expectedFragmentId = "http://localhost:8080/mobility-hindrances?tile=0/1/2&generatedAtTime=2020-12-05T09:00:00.000Z";

		assertEquals(expectedFragmentId, fragmentId);
	}
}
