package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection;

public interface CompactionCandidateProjection {
	Long getFragmentId();
	Integer getSize();
	Long getToPage();
	Long getBucketId();
	String getPartialUrl();
}
