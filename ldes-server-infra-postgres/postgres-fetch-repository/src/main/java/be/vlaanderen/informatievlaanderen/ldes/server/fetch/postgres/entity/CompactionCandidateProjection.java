package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.entity;

public interface CompactionCandidateProjection {
	String getFragmentId();
	Integer getSize();
}
