package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.entity.MemberPropertiesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.projection.MemberPropertyVersionProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.projection.MemberViewsProjection;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MemberPropertiesEntityMapper {

	public MemberPropertiesEntity toMemberPropertiesEntity(MemberProperties memberProperties) {
		return new MemberPropertiesEntity(memberProperties.getId(), memberProperties.getCollectionName(),
				memberProperties.getViewReferences(), memberProperties.getVersionOf(), memberProperties.getTimestamp());
	}

	public MemberProperties toMemberProperties(MemberPropertiesEntity memberPropertiesEntity) {
		MemberProperties memberProperties = new MemberProperties(memberPropertiesEntity.getId(),
				memberPropertiesEntity.getCollectionName(),
				memberPropertiesEntity.getVersionOf(),
				memberPropertiesEntity.getTimestamp());
		if (memberPropertiesEntity.getViews() != null) {
			memberPropertiesEntity.getViews().forEach(memberProperties::addViewReference);
		}
		return memberProperties;
	}

	public MemberPropertiesEntity toMemberPropertiesEntity(MemberPropertyVersionProjection projection) {
		return new MemberPropertiesEntity(projection.getId(), projection.getCollectionName(),
					projection.getViews().stream()
							.map(MemberViewsProjection::getView)
							.collect(Collectors.toSet()),
					projection.getVersionOf(), projection.getTimestamp());
	}
}
