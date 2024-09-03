package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection;

import java.time.LocalDateTime;

public interface CompactionCandidateProjection {
	Long getFragmentId();
	Integer getSize();
	Long getToPage();
	Boolean getImmutable();
	LocalDateTime getExpiration();
	Long getBucketId();
	String getPartialUrl();
}
