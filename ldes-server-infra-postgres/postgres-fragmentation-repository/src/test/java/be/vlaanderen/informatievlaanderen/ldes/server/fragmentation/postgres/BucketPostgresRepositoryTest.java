package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

class BucketPostgresRepositoryTest extends PostgresBucketisationIntegrationTest {
	private static final ViewName VIEW_NAME = new ViewName("collection", "name");
	private static final BucketDescriptor BUCKET_DESCRIPTOR = BucketDescriptor.fromString("key=value&k=v");
	@Autowired
	private BucketPostgresRepository bucketPostgresRepository;


}