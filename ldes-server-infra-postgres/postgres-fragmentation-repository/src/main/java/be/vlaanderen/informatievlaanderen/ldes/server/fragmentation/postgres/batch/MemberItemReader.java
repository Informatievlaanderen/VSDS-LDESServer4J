package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.FragmentationMemberRowMapper;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.batch.BucketStepDefinitions.CHUNK_SIZE;

@Configuration
public class MemberItemReader {
	private static final int PAGE_SIZE = CHUNK_SIZE;

	@Bean
	@StepScope
	public JdbcPagingItemReader<FragmentationMember> memberReader(@Value("#{jobParameters}") Map<String, Object> jobParameters,
	                                                              DataSource dataSource) {
		return new JdbcPagingItemReaderBuilder<FragmentationMember>()
				.name("memberReader")
				.dataSource(dataSource)
				.rowMapper(new FragmentationMemberRowMapper())
				.queryProvider(memberQuery())
				.parameterValues(jobParameters)
				.pageSize(PAGE_SIZE)
				.maxItemCount(40 * PAGE_SIZE)
				.build();
	}

	private PostgresPagingQueryProvider memberQuery() {
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("member_id", Order.ASCENDING);
		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
		queryProvider.setSelectClause("""
				member_id, subject, version_of, timestamp,
				name, version_of_path, timestamp_path, version_delimiter IS NOT NULL as create_versions,
				member_model
				""");
		queryProvider.setFromClause("""
				collections c
				join processable_members m on m.collection_id = c.collection_id
				""");
		queryProvider.setWhereClause("""
				 m.member_id > (
				    select vs.bucketized_last_id from view_stats vs where vs.view_id = :viewId
				   )
				   AND c.collection_id = :collectionId
				""");
		queryProvider.setSortKeys(sortKeys);
		return queryProvider;
	}
}
