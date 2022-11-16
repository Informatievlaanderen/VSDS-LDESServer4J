package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.MemberReferencesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.MemberReferencesEntityRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class MemberReferencesMongoRepositoryTest {
	private final MemberReferencesEntityRepository memberReferencesEntityRepository = mock(
			MemberReferencesEntityRepository.class);
	private final MemberReferencesMongoRepository memberReferencesMongoRepository = new MemberReferencesMongoRepository(
			memberReferencesEntityRepository);

	@Test
	void when_ReferenceIsSaved_EntityIsSavedInMongoDB() {
		memberReferencesMongoRepository.saveMemberReference("memberId", "treeNodeId");

		verify(memberReferencesEntityRepository, times(1)).findById("memberId");
		verify(memberReferencesEntityRepository, times(1))
				.save(any());
		verifyNoMoreInteractions(memberReferencesEntityRepository);
	}

	@Test
	void when_ReferenceIsRemoved_ListOfReferencesIsUpdatedInMongoDB() {
		when(memberReferencesEntityRepository.findById("memberId")).thenReturn(Optional
				.of(new MemberReferencesEntity("memberId", new ArrayList<>(List.of("treeNodeId", "treeNodeIdTwo")))));
		memberReferencesMongoRepository.removeMemberReference("memberId", "treeNodeId");

		verify(memberReferencesEntityRepository, times(1)).findById("memberId");
		verify(memberReferencesEntityRepository, times(1))
				.save(any());
		verifyNoMoreInteractions(memberReferencesEntityRepository);
	}

	@Test
	void when_NumberOfReferencesIsChecked_BooleanIsReturned() {
		when(memberReferencesEntityRepository.findById("memberId")).thenReturn(Optional
				.of(new MemberReferencesEntity("memberId", List.of())));
		assertFalse(memberReferencesMongoRepository.hasMemberReferences("memberId"));
	}
}