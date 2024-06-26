package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.projections;

public interface BucketProjection {
	String getBucketDescriptor();
	String getViewName();
}
