package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.FragmentationMemberRowMapper;
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

@Configuration
public class MemberItemReader {
	private static final int PAGE_SIZE = 500;

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
				.build();
	}

	private PostgresPagingQueryProvider memberQuery() {
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("timestamp", Order.ASCENDING);
		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
		queryProvider.setSelectClause("m.member_id, m.subject, m.version_of, m.timestamp, c.name, c.version_of_path, c.timestamp_path, c.create_versions, m.member_model");
		queryProvider.setFromClause("""
				        members m
				        JOIN collections c ON c.collection_id = m.collection_id
				        JOIN views v ON v.collection_id = m.collection_id
				""");
		queryProvider.setWhereClause("""
				      NOT EXISTS (
				        SELECT 1 FROM page_members mb
				        JOIN buckets b ON mb.bucket_id = b.bucket_id
				        JOIN views v ON v.view_id = b.view_id AND v.collection_id = c.collection_id
				        WHERE mb.member_id = m.member_id
				      )
				      AND v.name = :viewName AND c.name = :collectionName
				""");
		queryProvider.setSortKeys(sortKeys);
		return queryProvider;
	}

}
