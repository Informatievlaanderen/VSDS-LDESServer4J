package be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.http.valueobjects.EventStreamResponse;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.eventstream.services.EventStreamService;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LegacyMemberConverterTest {

	@Mock
	private EventStreamService eventStreamService;

	@InjectMocks
	private LegacyMemberConverter legacyMemberConverter;

	@Test
	void toMember() {
		final var collectionName = "collectionName";
		final var versionOfPath = "http://purl.org/dc/terms/isVersionOf";
		final var timestampPath = "http://purl.org/dc/terms/created";
		final var memberType = "https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder";
		final var eventStreamResponse = new EventStreamResponse(collectionName, timestampPath, versionOfPath,
				memberType, List.of(), ModelFactory.createDefaultModel());
		when(eventStreamService.retrieveEventStream(collectionName)).thenReturn(eventStreamResponse);
		Model model = RDFParser.source("example-ldes-member-complete.nq").lang(Lang.NQUADS).build().toModel();

		Member member = legacyMemberConverter.toMember(collectionName, model);

		assertEquals(
				"collectionName/https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				member.getLdesMemberId());
		assertEquals(collectionName, member.getCollectionName());
		assertEquals("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464",
				member.getVersionOf());
		assertEquals(LocalDateTime.of(2022, 5, 20, 9, 58, 15), member.getTimestamp());
		assertEquals(model, member.getModel());
	}

}