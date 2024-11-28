package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.chunk.ChunkCollector;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;

@Component
@StepScope
public class BucketisationItemWriter implements ItemWriter<Bucket> {
	private final ItemWriter<Bucket> bucketItemWriter;
	private final ItemWriter<Bucket> pageItemWriter;
	private final ItemWriter<BucketisedMember> bucketisedMemberItemWriter;
	private final ItemWriter<BucketRelation> bucketRelationWriter;
	private final JdbcTemplate jdbcTemplate;
	private final long viewId;

	public BucketisationItemWriter(ItemWriter<Bucket> bucketItemWriter,
	                               ItemWriter<Bucket> pageItemWriter,
	                               ItemWriter<BucketisedMember> bucketisedMemberItemWriter,
	                               ItemWriter<BucketRelation> bucketRelationWriter,
	                               JdbcTemplate jdbcTemplate,
	                               @Value("#{jobParameters['viewId']}") long viewId) {
		this.bucketItemWriter = bucketItemWriter;
		this.pageItemWriter = pageItemWriter;
		this.bucketisedMemberItemWriter = bucketisedMemberItemWriter;
		this.bucketRelationWriter = bucketRelationWriter;
		this.jdbcTemplate = jdbcTemplate;
		this.viewId = viewId;
	}

	@Override
	public void write(Chunk<? extends Bucket> rootBucketChunk) throws Exception {
		for (var rootbucket : rootBucketChunk) {
			final Chunk<Bucket> flatBucketChunk = new Chunk<>(rootbucket.getBucketTree());
			bucketItemWriter.write(flatBucketChunk);
			pageItemWriter.write(flatBucketChunk);
			bucketRelationWriter.write(extractAllBucketRelations(rootbucket));
			var members = extractAllMembers(flatBucketChunk);
			bucketisedMemberItemWriter.write(members);

			var uniqueMemberCount = members.getItems().stream()
					.map(BucketisedMember::memberId)
					.distinct()
					.count();
			updateViewStats(members.getItems().getLast().memberId(), uniqueMemberCount);
		}
	}

	private void updateViewStats(long lastMemberId, long uniqueMemberCount) {
		String sql = """
				update view_stats vs set
				      bucketized_count = vs.bucketized_count + ?,
				      bucketized_last_id = ?
				    where view_id = ?;
				""";

		jdbcTemplate.update(sql, uniqueMemberCount, lastMemberId, viewId);
	}


	private static Chunk<BucketisedMember> extractAllMembers(Chunk<? extends Bucket> flatBucketChunk) {
		return flatBucketChunk.getItems().stream()
				.map(Bucket::getMember)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.sorted(Comparator.comparing(BucketisedMember::memberId))
				.collect(new ChunkCollector<>());
	}

	private static Chunk<BucketRelation> extractAllBucketRelations(Bucket rootbucket) {
		return rootbucket.getBucketTree().stream()
				.flatMap(bucket -> bucket.getChildRelations().stream())
				.distinct()
				.collect(new ChunkCollector<>());
	}

}
