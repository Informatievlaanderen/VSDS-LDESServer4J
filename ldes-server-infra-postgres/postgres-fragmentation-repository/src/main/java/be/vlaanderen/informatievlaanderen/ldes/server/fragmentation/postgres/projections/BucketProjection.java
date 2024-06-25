package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.projections;

public interface BucketProjection {
	String bucketDescriptor();
	String viewName();
	Long memberCount();
}
