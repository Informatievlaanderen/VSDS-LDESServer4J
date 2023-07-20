package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.MembersToFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.entity.MembersToFragmentEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.mapper.MembersToFragmentEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.repository.MembersToFragmentEntityRepository;
import org.apache.jena.rdf.model.Model;

import java.util.List;
import java.util.Optional;

// TODO TVB: 20/07/23 test alles hier, mss gewoon komkommertje?
public class MembersToFragmentMongoRepository implements MembersToFragmentRepository {

	private final MembersToFragmentEntityRepository entityRepository;
	private final MembersToFragmentEntityMapper membersToFragmentEntityMapper;

	public MembersToFragmentMongoRepository(MembersToFragmentEntityRepository entityRepository,
			MembersToFragmentEntityMapper membersToFragmentEntityMapper) {
		this.entityRepository = entityRepository;
		this.membersToFragmentEntityMapper = membersToFragmentEntityMapper;
	}

	@Override
	public void create(List<ViewName> views, Model model, long sequenceNr, String memberId) {
		views.stream().map(
				viewName -> membersToFragmentEntityMapper.toMemberEntity(viewName, model, sequenceNr, memberId))
				.forEach(entityRepository::save);
	}

	@Override
	public Optional<Member> getNextMemberToFragment(ViewName viewName) {
		return entityRepository
				.findFirstById_ViewNameOrderById_ViewNameAsc(viewName)
				.map(membersToFragmentEntityMapper::toMember);
	}

	@Override
	public void delete(ViewName viewName, Long sequenceNr) {
		entityRepository.deleteById(new MembersToFragmentEntity.MembersToFragmentEntityId(viewName, sequenceNr));
	}

}
