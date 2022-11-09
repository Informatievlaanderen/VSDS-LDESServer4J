package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.LdesMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.LdesMemberEntityRepository;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParserBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.TREE_MEMBER;
import static org.junit.jupiter.api.Assertions.*;
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

		Member treeMember = new Member("some_id", RdfModelConverter.fromString(member, Lang.NQUADS));
		LdesMemberEntity ldesMemberEntity = LdesMemberEntity.fromLdesMember(treeMember);
		when(ldesMemberEntityRepository.save(any())).thenReturn(ldesMemberEntity);

		Member actualMember = ldesMemberMongoRepository.saveLdesMember(treeMember);

		assertTrue(treeMember.getModel().isIsomorphicWith(actualMember.getModel()));
		verify(ldesMemberEntityRepository, times(1)).save(any());
	}

	@DisplayName("Correct retrieval of LdesMembers by Id from MongoDB")
	@Test
	void when_LdesMemberByIdIsRequested_LdesMemberIsReturnedWhenExisting() {
		String memberId = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165";
		Model ldesMemberModel = RDFParserBuilder.create().fromString(
				"""
						<http://localhost:8080/mobility-hindrances> <https://w3id.org/tree#member> <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165> .""")
				.lang(Lang.NQUADS).toModel();
		Member expectedMember = new Member(memberId, ldesMemberModel);
		LdesMemberEntity ldesMemberEntity = LdesMemberEntity.fromLdesMember(expectedMember);

		when(ldesMemberEntityRepository.findById(memberId)).thenReturn(Optional.of(ldesMemberEntity));

		Optional<Member> actualLdesMember = ldesMemberMongoRepository.getLdesMemberById(memberId);

		assertTrue(actualLdesMember.isPresent());
		assertEquals(expectedMember.getLdesMemberId(),
				actualLdesMember.get().getLdesMemberId());
		verify(ldesMemberEntityRepository, times(1)).findById(memberId);
	}

	@DisplayName("Throwing of LdesMemberNotFoundException")
	@Test
	void when_LdesMemberByIdAndLdesMemberDoesNotExist_EmptyOptionalIsReturned() {
		String memberId = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165";
		when(ldesMemberEntityRepository.findById(memberId)).thenReturn(Optional.empty());

		Optional<Member> ldesMemberById = ldesMemberMongoRepository.getLdesMemberById(memberId);

		assertFalse(ldesMemberById.isPresent());
		verify(ldesMemberEntityRepository, times(1)).findById(memberId);
	}

	@DisplayName("Correct retrieval of LdesMembers by Ids from MongoDB")
	@Test
	void when_LdesMemberByIdsIsRequested_LdesMembersAreReturned() {
		String memberId = "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165";
		Model ldesMemberModel = RDFParserBuilder.create().fromString(
				"""
						<http://localhost:8080/mobility-hindrances> <https://w3id.org/tree#member> <https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/165> .""")
				.lang(Lang.NQUADS).toModel();
		Member expectedMember = new Member(memberId, ldesMemberModel);
		LdesMemberEntity ldesMemberEntity = LdesMemberEntity.fromLdesMember(expectedMember);

		when(ldesMemberEntityRepository.findById(memberId)).thenReturn(Optional.of(ldesMemberEntity));

		Optional<Member> actualLdesMember = ldesMemberMongoRepository.getLdesMemberById(memberId);

		assertTrue(actualLdesMember.isPresent());
		assertEquals(expectedMember.getLdesMemberId(),
				actualLdesMember.get().getLdesMemberId());
		verify(ldesMemberEntityRepository, times(1)).findById(memberId);
	}
}