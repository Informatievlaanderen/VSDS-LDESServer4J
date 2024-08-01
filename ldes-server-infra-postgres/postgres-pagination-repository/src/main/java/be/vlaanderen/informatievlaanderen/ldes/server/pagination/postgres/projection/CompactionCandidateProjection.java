package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.entity.RelationEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface CompactionCandidateProjection {
	Long getFragmentId();
	Integer getSize();
	Long getToPage();
	Boolean getImmutable();
	LocalDateTime getExpiration();
	Long getBucketId();
	String getPartialUrl();
}
