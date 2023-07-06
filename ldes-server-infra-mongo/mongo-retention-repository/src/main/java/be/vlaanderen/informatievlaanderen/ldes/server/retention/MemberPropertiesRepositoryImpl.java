package be.vlaanderen.informatievlaanderen.ldes.server.retention;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.mapper.MemberPropertiesEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberPropertiesRepositoryImpl implements MemberPropertiesRepository {

	private final MemberPropertiesEntityRepository memberPropertiesEntityRepository;
	private final MemberPropertiesEntityMapper memberPropertiesEntityMapper;

	public MemberPropertiesRepositoryImpl(MemberPropertiesEntityRepository memberPropertiesEntityRepository,
			MemberPropertiesEntityMapper memberPropertiesEntityMapper) {
		this.memberPropertiesEntityRepository = memberPropertiesEntityRepository;
		this.memberPropertiesEntityMapper = memberPropertiesEntityMapper;
	}

	@Override
	public void save(MemberProperties memberProperties) {
		memberPropertiesEntityRepository.save(memberPropertiesEntityMapper.toMemberEntity(memberProperties));
	}

	@Override
	public Optional<MemberProperties> retrieve(String id) {
		return memberPropertiesEntityRepository.findById(id).map(memberPropertiesEntityMapper::toMember);
	}

	@Override
	public void allocateMember(String memberId, ViewName viewName) {
		MemberPropertiesEntity member = memberPropertiesEntityRepository.findById(memberId).orElseThrow();
		member.getViews().add(viewName.asString());
		memberPropertiesEntityRepository.save(member);
	}
}
