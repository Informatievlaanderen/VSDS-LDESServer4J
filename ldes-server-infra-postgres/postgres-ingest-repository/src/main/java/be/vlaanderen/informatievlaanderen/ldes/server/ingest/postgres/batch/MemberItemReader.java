package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
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

	@Bean
	public JdbcPagingItemReader<FragmentationMember> newMemberReader(DataSource dataSource) {
		return new JdbcPagingItemReaderBuilder<FragmentationMember>()
				.dataSource(dataSource)
				.rowMapper(new MemberRowMapper())
				.queryProvider(memberQuery())
				.pageSize(150)
				.fetchSize(150)
				.saveState(false)
				.build();
	}

	@Bean
	@StepScope
	public JdbcPagingItemReader<FragmentationMember> refragmentEventStream(@Value("#{jobParameters}") Map<String, Object> jobParameters,
	                                                                       DataSource dataSource) {
		return new JdbcPagingItemReaderBuilder<FragmentationMember>()
				.dataSource(dataSource)
				.rowMapper(new MemberRowMapper())
				.queryProvider(memberQuery())
				.parameterValues(Map.of("viewName", jobParameters.get("viewName")))
				.pageSize(150)
				.fetchSize(150)
				.saveState(false)
				.build();
	}

	private PostgresPagingQueryProvider memberQuery() {
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("timestamp", Order.ASCENDING);
		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
		queryProvider.setSelectClause("m.member_id, m.subject, m.version_of, m.timestamp, c.name, c.version_of_path, c.timestamp_path, c.create_versions, m.member_model");
		queryProvider.setFromClause("members m JOIN views v USING (collection_id) JOIN collections c USING (collection_id)");
		queryProvider.setWhereClause("(member_id, view_id) NOT IN (SELECT member_id, b.view_id FROM page_members JOIN buckets b USING (bucket_id))");
		queryProvider.setSortKeys(sortKeys);
		return queryProvider;
	}

}
