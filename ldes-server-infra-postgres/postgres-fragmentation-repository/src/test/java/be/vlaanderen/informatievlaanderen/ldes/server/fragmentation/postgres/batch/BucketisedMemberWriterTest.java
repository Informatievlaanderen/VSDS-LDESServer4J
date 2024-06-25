package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.PostgresFragmentationIntegrationTest;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.entity.MemberBucketEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.repository.MemberBucketEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class BucketisedMemberWriterTest extends PostgresFragmentationIntegrationTest {
	@Autowired
	BucketisedMemberWriter writer;

	@Autowired
	MemberBucketEntityRepository repository;

	private final AtomicInteger atomicInteger = new AtomicInteger();
	private final ViewName viewName = new ViewName("es", "v1");

	@Test
	void testWriter() throws Exception {
		writer.write(Chunk.of(List.of(bucketisedMember(), bucketisedMember()),
				List.of(bucketisedMember(), bucketisedMember())));

		var members = repository.findAll(Pageable.unpaged()).get().toList();

		assertThat(members)
				.hasSize(4)
				.map(MemberBucketEntity::getMemberId)
				.last()
				.isEqualTo("4");
	}

	BucketisedMember bucketisedMember() {
		return new BucketisedMember(String.valueOf(atomicInteger.incrementAndGet()), viewName, viewName.asString());
	}
}
