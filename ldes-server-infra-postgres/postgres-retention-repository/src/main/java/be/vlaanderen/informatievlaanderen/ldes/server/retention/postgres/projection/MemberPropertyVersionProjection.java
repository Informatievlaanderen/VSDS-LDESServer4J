package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.projection;

import java.time.LocalDateTime;
import java.util.Set;

public interface MemberPropertyVersionProjection {
	String getId();
	String getCollectionName();
	String getVersionOf();
	LocalDateTime getTimestamp();
	Set<MemberViewsProjection> getViews();
	Integer getVersionNumber();
}
