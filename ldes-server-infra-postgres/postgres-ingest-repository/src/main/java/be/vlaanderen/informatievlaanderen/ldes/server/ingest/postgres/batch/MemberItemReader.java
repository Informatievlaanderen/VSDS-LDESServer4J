package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	Logger log = LoggerFactory.getLogger(MemberItemReader.class);

	@Bean("newMemberReader")
	public JdbcPagingItemReader<IngestedMember> newMemberReader(DataSource dataSource) {
		return new JdbcPagingItemReaderBuilder<IngestedMember>()
				.dataSource(dataSource)
				.rowMapper(new MemberRowMapper())
				.queryProvider(newMembersQuery())
				.pageSize(150)
				.fetchSize(150)
				.saveState(false)
				.build();
	}

	@Bean("refragmentEventStream")
	@StepScope
	public JdbcPagingItemReader<IngestedMember> refragmentEventStream(@Value("#{jobParameters}") Map<String, Object> jobParameters,
	                                                                  DataSource dataSource) {
		return new JdbcPagingItemReaderBuilder<IngestedMember>()
				.dataSource(dataSource)
				.rowMapper(new MemberRowMapper())
				.queryProvider(refragmentQuery())
				.parameterValues(Map.of("viewName", jobParameters.get("viewName")))
				.pageSize(150)
				.fetchSize(150)
				.saveState(false)
				.build();
	}

	private PostgresPagingQueryProvider newMembersQuery() {
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("timestamp", Order.ASCENDING);
		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
		queryProvider.setSelectClause("m.*, c.name");
		queryProvider.setFromClause("members m LEFT JOIN fragmentation_bucketisation fb on m.old_id = fb.member_id LEFT JOIN collections c on m.collection_id = c.collection_id");
		queryProvider.setWhereClause("fb.id IS NULL");
		queryProvider.setSortKeys(sortKeys);
		return queryProvider;
	}

	private PostgresPagingQueryProvider refragmentQuery() {
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("timestamp", Order.ASCENDING);
		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
		queryProvider.setSelectClause("m.*, c.name");
		queryProvider.setFromClause("members m LEFT JOIN fragmentation_bucketisation fb on m.old_id = fb.member_id " +
				"AND fb.view_name = :viewName " +
				"LEFT JOIN collections c on m.collection_id = c.collection_id");
		queryProvider.setWhereClause("fb.id IS NULL");
		queryProvider.setSortKeys(sortKeys);
		return queryProvider;
	}

}
