package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MemberToFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.MemberToFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.mapper.MemberToFragmentEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.MemberToFragmentEntityRepository;

import java.util.List;
import java.util.Optional;

public class MemberToFragmentMongoRepository implements MemberToFragmentRepository {

	private final MemberToFragmentEntityRepository entityRepository;
	private final MemberToFragmentEntityMapper memberToFragmentEntityMapper;

	public MemberToFragmentMongoRepository(MemberToFragmentEntityRepository entityRepository,
			MemberToFragmentEntityMapper memberToFragmentEntityMapper) {
		this.entityRepository = entityRepository;
		this.memberToFragmentEntityMapper = memberToFragmentEntityMapper;
	}

	@Override
	public void create(List<ViewName> views, Member member) {
		views.stream().map(
				viewName -> memberToFragmentEntityMapper.toMemberEntity(viewName, member))
				.forEach(entityRepository::save);
	}

	@Override
	public Optional<Member> getNextMemberToFragment(ViewName viewName) {
		return entityRepository
				.findFirstByViewNameOrderBySequenceNrAsc(viewName.asString())
				.map(memberToFragmentEntityMapper::toMember);
	}

	@Override
	public void delete(ViewName viewName, Long sequenceNr) {
		entityRepository.deleteById(viewName.asString() + "/" + sequenceNr);
	}

}
