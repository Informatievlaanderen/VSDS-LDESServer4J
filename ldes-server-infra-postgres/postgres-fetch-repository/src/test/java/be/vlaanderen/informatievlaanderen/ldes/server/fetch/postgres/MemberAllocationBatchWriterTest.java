package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.batch.MemberAllocationBatchWriter;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.entity.MemberAllocationEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.repository.AllocationEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.MemberAllocation;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberAllocationBatchWriterTest extends PostgresAllocationIntegrationTest {

	@Autowired
	MemberAllocationBatchWriter memberAllocationBatchWriter;

	@Autowired
	AllocationEntityRepository allocationEntityRepository;

	AtomicInteger atomicInteger = new AtomicInteger();

	@Test
	void testWriter() throws Exception {
		memberAllocationBatchWriter.write(Chunk.of(List.of(memberAllocation(), memberAllocation()),
				List.of(memberAllocation(), memberAllocation())));

		var items = allocationEntityRepository.findAll()
				.stream()
				.sorted(Comparator.comparing(MemberAllocationEntity::getId))
				.toList();

		assertEquals(4, items.size());
		assertEquals("4", items.getLast().getMemberId());
	}

	MemberAllocation memberAllocation() {
		return new MemberAllocation(String.valueOf(atomicInteger.incrementAndGet()), "es", "es/v1",
				"es/v1", String.valueOf(atomicInteger.get()));
	}
}
