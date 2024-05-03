package be.vlaanderen.informatievlaanderen.ldes.server.retention.projection;

import be.vlaanderen.informatievlaanderen.ldes.server.retention.entity.MemberPropertiesEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public interface MemberPropertyVersionProjection {
	String getId();
	String getCollectionName();
	String getVersionOf();
	LocalDateTime getTimestamp();
	Set<MemberViewsProjection> getViews();
	Integer getVersionNumber();

	default MemberPropertiesEntity toMemberPropertiesEntity() {
		return new MemberPropertiesEntity(getId(), getCollectionName(),
				getViews().stream()
						.map(MemberViewsProjection::getView)
						.collect(Collectors.toSet()),
				getVersionOf(), getTimestamp());
	}
}
