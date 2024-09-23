package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.chunk.ChunkCollector;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BucketisationItemWriter implements ItemWriter<Bucket> {
	private final ItemWriter<Bucket> bucketItemWriter;
	private final ItemWriter<Bucket> pageItemWriter;
	private final ItemWriter<BucketisedMember> bucketisedMemberItemWriter;
	private final ItemWriter<BucketRelation> bucketRelationWriter;

	public BucketisationItemWriter(ItemWriter<Bucket> bucketItemWriter,
	                               ItemWriter<Bucket> pageItemWriter,
	                               ItemWriter<BucketisedMember> bucketisedMemberItemWriter,
	                               ItemWriter<BucketRelation> bucketRelationWriter) {
		this.bucketItemWriter = bucketItemWriter;
		this.pageItemWriter = pageItemWriter;
		this.bucketisedMemberItemWriter = bucketisedMemberItemWriter;
		this.bucketRelationWriter = bucketRelationWriter;
	}

	@Override
	public void write(Chunk<? extends Bucket> rootBucketChunk) throws Exception {
		for(var rootbucket : rootBucketChunk) {
			final Chunk<Bucket> flatBucketChunk = new Chunk<>(rootbucket.getBucketTree());
			bucketItemWriter.write(flatBucketChunk);
			pageItemWriter.write(flatBucketChunk);
			bucketRelationWriter.write(extractAllBucketRelations(rootbucket));
			bucketisedMemberItemWriter.write(extractAllMembers(flatBucketChunk));
		}
	}

	private static Chunk<BucketisedMember> extractAllMembers(Chunk<? extends Bucket> flatBucketChunk) {
		return flatBucketChunk.getItems().stream()
				.map(Bucket::getMember)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(new ChunkCollector<>());
	}

	private static Chunk<BucketRelation> extractAllBucketRelations(Bucket rootbucket) {
		return rootbucket.getBucketTree().stream()
				.flatMap(bucket -> bucket.getChildRelations().stream())
				.distinct()
				.collect(new ChunkCollector<>());
	}

}
