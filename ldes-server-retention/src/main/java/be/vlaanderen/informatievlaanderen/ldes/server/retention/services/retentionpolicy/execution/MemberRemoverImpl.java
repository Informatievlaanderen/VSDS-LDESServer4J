package be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.execution;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.springframework.stereotype.Component;

@Component
public class MemberRemoverImpl implements MemberRemover {

	private final MemberPropertiesRepository memberPropertiesRepository;

	public MemberRemoverImpl(MemberPropertiesRepository memberPropertiesRepository) {
		this.memberPropertiesRepository = memberPropertiesRepository;
	}

	@Override
	public void removeMemberFromView(MemberProperties memberProperties, String viewName) {
		memberProperties.deleteViewReference(viewName);
		memberPropertiesRepository.removeViewReference(memberProperties.getId(), viewName);
		// TODO: 06/07/23 fire unallocated event
		if (memberProperties.hasNoViewReferences()) {
			memberPropertiesRepository.deleteById(memberProperties.getId());
			// TODO: 06/07/23 fire deleted event
		}
	}
}
