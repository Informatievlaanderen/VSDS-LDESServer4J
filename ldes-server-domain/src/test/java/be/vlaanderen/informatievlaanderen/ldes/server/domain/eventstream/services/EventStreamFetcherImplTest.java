package be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventStreamFetcherImplTest {
	EventStreamFetcher eventStreamFetcher;

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = getLdesConfig();
		ViewConfig viewConfig = getViewConfig();
		eventStreamFetcher = new EventStreamFetcherImpl(ldesConfig, viewConfig);
	}

	@Test
	void test() {
		EventStream eventStream = eventStreamFetcher.fetchEventStream();
		assertEquals("mobility-hindrances", eventStream.collection());
		assertEquals("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape",
				eventStream.shape());
		assertEquals("http://www.w3.org/ns/prov#generatedAtTime", eventStream.timestampPath());
		assertEquals("http://purl.org/dc/terms/isVersionOf", eventStream.versionOf());
		assertEquals(List.of("firstView", "secondView"), eventStream.views());
	}

	private ViewConfig getViewConfig() {
		ViewConfig viewConfig = new ViewConfig();
		ViewSpecification firstViewSpecification = new ViewSpecification();
		firstViewSpecification.setName("firstView");
		ViewSpecification secondViewSpecification = new ViewSpecification();
		secondViewSpecification.setName("secondView");
		viewConfig.setViews(List.of(firstViewSpecification, secondViewSpecification));
		return viewConfig;
	}

	private LdesConfig getLdesConfig() {
		LdesConfig ldesConfig = new LdesConfig();
		ldesConfig.setCollectionName("mobility-hindrances");
		ldesConfig.setTimestampPath("http://www.w3.org/ns/prov#generatedAtTime");
		ldesConfig.setVersionOf("http://purl.org/dc/terms/isVersionOf");
		ldesConfig.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
		return ldesConfig;
	}
}