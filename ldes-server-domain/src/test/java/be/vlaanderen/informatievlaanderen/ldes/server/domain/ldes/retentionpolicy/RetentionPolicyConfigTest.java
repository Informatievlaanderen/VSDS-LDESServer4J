package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation.RetentionPolicyCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.creation.RetentionPolicyCreatorImpl;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.retentionpolicy.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.RetentionConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewSpecification;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.BeforeEach;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class RetentionPolicyConfigTest {

	private final RetentionPolicyCreator retentionPolicyCreator = new RetentionPolicyCreatorImpl();
	private EventStreamService eventStreamService;

	@BeforeEach
	void setUp() {
		eventStreamService = mock(EventStreamService.class);
	}

	@Test
	void when_TimeBasedRetentionPolicyIsDefinedInConfig_ItIsAddedToMap() {
		when(eventStreamService.retrieveAllEventStreams()).thenReturn(List.of(getFirstEventStream("timebased")));

		RetentionPolicyConfig retentionPolicyConfig = new RetentionPolicyConfig();
		Map<ViewName, List<RetentionPolicy>> retentionPolicyMap = retentionPolicyConfig.retentionPolicyMap(eventStreamService,
				retentionPolicyCreator);
		assertEquals(1, retentionPolicyMap.size());
		assertEquals(1, retentionPolicyMap.get(ViewName.fromString("parcels/firstView")).size());
		assertTrue(retentionPolicyMap.get(ViewName.fromString("parcels/firstView"))
				.get(0) instanceof TimeBasedRetentionPolicy);
	}

	@Test
	@Disabled("To be enabled again once timebased retention is finished")
	void when_OtherRetentionPoliciesAreDefinedInConfig_IllegalArgumentExceptionIsThrown() {
		when(eventStreamService.retrieveAllEventStreams()).thenReturn(List.of(getFirstEventStream("other")));

		RetentionPolicyConfig retentionPolicyConfig = new RetentionPolicyConfig();
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> retentionPolicyConfig.retentionPolicyMap(eventStreamService, retentionPolicyCreator));
		assertEquals("Invalid retention Policy: other", exception.getMessage());
	}

	private EventStreamResponse getFirstEventStream(String policyName) {
		String collectionName = "parcels";
		return new EventStreamResponse(
				collectionName,
				"http://www.w3.org/ns/prov#generatedAtTime",
				null,
				"https://vlaanderen.be/implementatiemodel/gebouwenregister#Perceel",
				List.of(getFirstViewSpecification(policyName, collectionName)),
				ModelFactory.createDefaultModel());

	}

	private ViewSpecification getFirstViewSpecification(String policyName, String collectionName) {
		ViewSpecification viewSpecification = new ViewSpecification();
		viewSpecification.setName(new ViewName(collectionName, "firstView"));
		RetentionConfig retentionConfig = new RetentionConfig();
		retentionConfig.setName(policyName);
		retentionConfig.setConfig(Map.of("duration", "PT1M"));
		viewSpecification.setRetentionPolicies(List.of(retentionConfig));
		return viewSpecification;
	}
}