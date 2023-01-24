package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.ValidationConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventStreamFactoryImplTest {
	EventStreamFactory eventStreamFactory;

	@BeforeEach
	void setUp() {
		LdesConfig ldesConfig = getLdesConfig();
		ViewConfig viewConfig = getViewConfig();
		eventStreamFactory = new EventStreamFactoryImpl(ldesConfig, viewConfig);
	}

	@Test
	void test() {
		EventStream eventStream = eventStreamFactory.getEventStream();
		assertEquals("mobility-hindrances", eventStream.collection());
		assertEquals("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape",
				eventStream.shape());
		assertEquals("http://www.w3.org/ns/prov#generatedAtTime", eventStream.timestampPath());
		assertEquals("http://purl.org/dc/terms/isVersionOf", eventStream.versionOfPath());
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
		ValidationConfig validationConfig = new ValidationConfig();
		validationConfig.setShape("https://private-api.gipod.test-vlaanderen.be/api/v1/ldes/mobility-hindrances/shape");
		ldesConfig.setValidation(validationConfig);
		return ldesConfig;
	}
}