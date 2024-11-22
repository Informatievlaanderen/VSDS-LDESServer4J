package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.projection;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.entity.FetchBucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.entity.FetchPageRelationEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface TreeNodeProjection {
	long getId();
	String getPartialUrl();
	boolean isImmutable();
	boolean isView();
	List<FetchPageRelationEntity> getRelations();
	FetchBucketEntity getBucket();
	LocalDateTime getNextUpdateTs();
}
