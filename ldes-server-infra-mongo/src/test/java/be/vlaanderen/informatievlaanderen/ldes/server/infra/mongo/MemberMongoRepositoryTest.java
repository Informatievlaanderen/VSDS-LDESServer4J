package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MemberMongoRepositoryTest {
	private final LdesMemberEntityRepository ldesMemberEntityRepository = mock(LdesMemberEntityRepository.class);
	private final MemberMongoRepository ldesMemberMongoRepository = new MemberMongoRepository(
			ldesMemberEntityRepository);

	@DisplayName("Correct saving of an LdesMember in MongoDB")
	@Test
	void when_LdesMemberIsSavedInRepository_CreatedResourceIsReturned() {
		String member = String.format("""
				<http://one.example/subject1> <%s> <http://one.example/object1>.""", TREE_MEMBER);

		Member treeMember = new Member("some_id", RdfModelConverter.fromString(member, Lang.NQUADS),
				treeNodeReferences);
		LdesMemberEntity ldesMemberEntity = LdesMemberEntity.fromLdesMember(treeMember);
		when(ldesMemberEntityRepository.save(any())).thenReturn(ldesMemberEntity);

		Member actualMember = ldesMemberMongoRepository.saveLdesMember(treeMember);

		assertTrue(treeMember.getModel().isIsomorphicWith(actualMember.getModel()));
		verify(ldesMemberEntityRepository, times(1)).save(any());
	}

	@DisplayName("Correct retrieval of LdesMembers by Id from MongoDB")
	@Test
	void when_LdesMemberExistsByIdIsRequested_ReturnsTrueWhenExisting() {
		String memberId = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165";

		when(ldesMemberEntityRepository.existsById(memberId)).thenReturn(true);

		assertTrue(ldesMemberMongoRepository.memberExists(memberId));
		verify(ldesMemberEntityRepository, times(1)).existsById(memberId);
	}

	@DisplayName("Correct exist check of LdesMembers by Ids from MongoDB")
	@Test
	void when_LdesMemberExistByIdsIsRequested_LdesMembersAreReturned() {
		String memberId = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165";

		when(ldesMemberEntityRepository.existsById(memberId)).thenReturn(true);

		assertTrue(ldesMemberMongoRepository.memberExists(memberId));
		verify(ldesMemberEntityRepository, times(1)).existsById(memberId);
	}
}