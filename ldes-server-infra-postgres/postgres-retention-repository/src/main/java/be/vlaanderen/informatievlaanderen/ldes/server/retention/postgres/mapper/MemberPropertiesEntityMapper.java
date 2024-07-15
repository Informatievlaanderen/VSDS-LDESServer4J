package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.entity.MemberPropertiesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.projection.RetentionMemberProjection;
import org.springframework.stereotype.Component;

@Component
public class MemberPropertiesEntityMapper {

	public MemberProperties toMemberProperties(RetentionMemberProjection entity) {
		MemberProperties memberProperties = new MemberProperties(entity.getId(),
				entity.getCollectionName(),
				entity.getVersionOf(),
				entity.getTimestamp(),
				entity.getInEventSource(),
				entity.getInView());
//		if (memberPropertiesEntity.getViews() != null) {
//			memberPropertiesEntity.getViews().forEach(memberProperties::addViewReference);
//		}
		return memberProperties;
	}
}
