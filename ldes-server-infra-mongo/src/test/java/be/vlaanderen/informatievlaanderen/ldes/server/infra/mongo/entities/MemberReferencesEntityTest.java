package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemberReferencesEntityTest {

	@Test
	void test_hasMemberReferences() {
		MemberReferencesEntity memberReferencesEntity = new MemberReferencesEntity("memberId", new ArrayList<>());
		assertFalse(memberReferencesEntity.hasMemberReferences());
		memberReferencesEntity.addMemberReference("treeNodeId");
		assertTrue(memberReferencesEntity.hasMemberReferences());
	}
}