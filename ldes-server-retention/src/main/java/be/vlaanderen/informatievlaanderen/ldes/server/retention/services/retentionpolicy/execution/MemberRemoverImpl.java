package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberRemoverImpl implements MemberRemover {

	private final MemberPropertiesRepository memberPropertiesRepository;

	public MemberRemoverImpl(MemberPropertiesRepository memberPropertiesRepository) {
		this.memberPropertiesRepository = memberPropertiesRepository;
	}

	@Override
	public void removeMembersFromEventSource(List<MemberProperties> memberProperties) {
		List<Long> ids = memberProperties.stream().filter(MemberProperties::isInEventSource).map(MemberProperties::id).toList();
		if (!ids.isEmpty()) {
			memberPropertiesRepository.removeFromEventSource(ids);
		}
	}

	@Override
	public void deleteMembers(List<MemberProperties> memberProperties) {
		List<Long> ids = memberProperties.stream().map(MemberProperties::id).toList();
		memberPropertiesRepository.deleteAllByIds(ids);
	}
}
