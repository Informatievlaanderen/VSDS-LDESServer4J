package be.vlaanderen.informatievlaanderen.ldes.server.ingest.events;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberIngestedEventTest {

	@Test
	void getIngestedMember() {
		Model model = RDFParser.source("example-ldes-member.nq").build().toModel();
		Member member = new Member(
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1", "collectionName",
				0L, model);
		MemberIngestedEvent memberIngestedEvent = new MemberIngestedEvent(member);

		assertEquals(member, memberIngestedEvent.getIngestedMember());
	}

}