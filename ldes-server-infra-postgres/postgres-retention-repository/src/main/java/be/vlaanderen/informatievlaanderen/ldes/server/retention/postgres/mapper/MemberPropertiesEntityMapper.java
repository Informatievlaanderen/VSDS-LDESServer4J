package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.entity.MemberPropertiesEntity;
import org.springframework.stereotype.Component;

@Component
public class MemberPropertiesEntityMapper {

	public MemberPropertiesEntity toMemberPropertiesEntity(MemberProperties memberProperties) {
		return new MemberPropertiesEntity(memberProperties.getId(), memberProperties.getCollectionName(),
				memberProperties.getViewReferences(), memberProperties.isInEventSource(), memberProperties.getVersionOf(), memberProperties.getTimestamp());
	}

	public MemberProperties toMemberProperties(MemberPropertiesEntity memberPropertiesEntity) {
		MemberProperties memberProperties = new MemberProperties(memberPropertiesEntity.getId(),
				memberPropertiesEntity.getCollectionName(),
				memberPropertiesEntity.getVersionOf(),
				memberPropertiesEntity.getTimestamp(),
				memberPropertiesEntity.isInEventSource());
		if (memberPropertiesEntity.getViews() != null) {
			memberPropertiesEntity.getViews().forEach(memberProperties::addViewReference);
		}
		return memberProperties;
	}

	public MemberProperties toMemberProperties(MemberEntity entity) {
		MemberProperties memberProperties = new MemberProperties(entity.getOldId(),
				entity.getCollection().getName(),
				entity.getVersionOf(),
				entity.getTimestamp(),
				entity.isInEventSource());
//		if (memberPropertiesEntity.getViews() != null) {
//			memberPropertiesEntity.getViews().forEach(memberProperties::addViewReference);
//		}
		return memberProperties;
	}
}
