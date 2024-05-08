package be.vlaanderen.informatievlaanderen.ldes.server.postgres.fetch.entity;

public interface CompactionCandidateProjection {
	String getFragmentId();
	Integer getSize();
}
