package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.projections.BucketProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;

public class BucketMapper {
	private BucketMapper() {
	}

	public static Bucket fromProjection(BucketProjection projection) {
		return new Bucket(
				BucketDescriptor.fromString(projection.bucketDescriptor()),
				ViewName.fromString(projection.viewName()),
				projection.memberCount().intValue()
		);
	}

}
