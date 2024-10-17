package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.projection;

public interface CompactionCandidateProjection {
	Long getFragmentId();
	Integer getSize();
	Long getToPage();
	Long getBucketId();
	String getPartialUrl();
}
