package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.projections;

public interface BucketProjection {
	Long getBucketId();
	String getBucketDescriptor();
	String getViewName();
}
