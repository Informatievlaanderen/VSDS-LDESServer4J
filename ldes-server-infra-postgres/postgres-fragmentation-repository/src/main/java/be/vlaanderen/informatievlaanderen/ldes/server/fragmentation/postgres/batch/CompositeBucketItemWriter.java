package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.chunk.ChunkCollector;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class CompositeBucketItemWriter implements ItemWriter<Bucket> {
	private final ItemWriter<Bucket> bucketItemWriter;
	private final ItemWriter<Bucket> pageItemWriter;
	private final ItemWriter<BucketisedMember> bucketisedMemberItemWriter;
	private final ItemWriter<BucketRelation> bucketRelationWriter;

	public CompositeBucketItemWriter(ItemWriter<Bucket> bucketItemWriter,
	                                 ItemWriter<Bucket> pageItemWriter,
	                                 ItemWriter<BucketisedMember> bucketisedMemberItemWriter,
	                                 ItemWriter<BucketRelation> bucketRelationWriter) {
		this.bucketItemWriter = bucketItemWriter;
		this.pageItemWriter = pageItemWriter;
		this.bucketisedMemberItemWriter = bucketisedMemberItemWriter;
		this.bucketRelationWriter = bucketRelationWriter;
	}

	@Override
	public void write(Chunk<? extends Bucket> chunk) throws Exception {
		final Chunk<Bucket> flatBucketChunk = flattenBucketChunk(chunk);
		bucketItemWriter.write(flatBucketChunk);
		pageItemWriter.write(flatBucketChunk);
		bucketisedMemberItemWriter.write(extractAllMembers(flatBucketChunk));
		bucketRelationWriter.write(extractAllBucketRelations(chunk));
	}


	private static Chunk<Bucket> flattenBucketChunk(final Chunk<? extends Bucket> chunk) {
		return chunk.getItems().stream()
				.flatMap(bucket -> bucket.getBucketTree().stream())
				.distinct()
				.collect(new ChunkCollector<>());
	}

	private static Chunk<BucketisedMember> extractAllMembers(Chunk<? extends Bucket> flatBucketChunk) {
		return flatBucketChunk.getItems().stream()
				.flatMap(bucket -> bucket.getBucketisedMembers().stream())
				.collect(new ChunkCollector<>());
	}

	private static Chunk<BucketRelation> extractAllBucketRelations(Chunk<? extends Bucket> chunk) {
		return chunk.getItems().stream()
				.flatMap(bucket -> bucket.getBucketTree().stream())
				.flatMap(bucket -> bucket.getChildRelations().stream())
				.distinct()
				.collect(new ChunkCollector<>());
	}

}
