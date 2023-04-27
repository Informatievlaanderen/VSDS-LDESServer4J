package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.service;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity.LdesMemberEntity;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.jena.rdf.model.ResourceFactory.*;
import static org.junit.jupiter.api.Assertions.*;

class LdesMemberEntityShaclShapeEntityConverterTest {

	private final LdesMemberEntityConverter converter = new LdesMemberEntityConverter();

	@Test
	void testReconstructionOfLdesMember() {
		Model defaultModel = ModelFactory.createDefaultModel();
		defaultModel.add(createStatement(
				createResource("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464"),
				createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
				createResource("https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder")));
		Member member = new Member("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
				"collectionName",
				0L,
				"https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464",
				LocalDateTime.of(1, 1, 1, 1, 1), defaultModel, List.of());

		LdesMemberEntity ldesMemberEntity = converter.fromLdesMember(member);
		Member reconstructedMember = converter.toLdesMember(ldesMemberEntity);

		assertEquals(member.getLdesMemberId(), reconstructedMember.getLdesMemberId());
		assertEquals(member.getCollectionName(), reconstructedMember.getCollectionName());
		assertEquals(member.getSequenceNr(), reconstructedMember.getSequenceNr());
		assertEquals(member.getVersionOf(), reconstructedMember.getVersionOf());
		assertEquals(member.getTimestamp(), reconstructedMember.getTimestamp());
		assertTrue(member.getModel().isIsomorphicWith(reconstructedMember.getModel()));
		assertEquals(member.getTreeNodeReferences(), reconstructedMember.getTreeNodeReferences());
	}

}
