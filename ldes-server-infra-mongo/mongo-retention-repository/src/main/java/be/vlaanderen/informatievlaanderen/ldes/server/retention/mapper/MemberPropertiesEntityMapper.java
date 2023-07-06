package be.vlaanderen.informatievlaanderen.ldes.server.retention.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberPropertiesEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberPropertiesEntityMapper {

	public MemberPropertiesEntity toMemberEntity(MemberProperties memberProperties) {
		return new MemberPropertiesEntity(memberProperties.getId(), memberProperties.getCollectionName(),
				memberProperties.getViewReferences(), memberProperties.getVersionOf(), memberProperties.getTimestamp());
	}

	public MemberProperties toMember(MemberPropertiesEntity memberPropertiesEntity) {
		MemberProperties memberProperties = new MemberProperties(memberPropertiesEntity.getId(),
				memberPropertiesEntity.getCollectionName(),
				memberPropertiesEntity.getVersionOf(),
				memberPropertiesEntity.getTimestamp());
		memberPropertiesEntity.getViews().forEach(memberProperties::addViewReference);
		return memberProperties;
	}
}
