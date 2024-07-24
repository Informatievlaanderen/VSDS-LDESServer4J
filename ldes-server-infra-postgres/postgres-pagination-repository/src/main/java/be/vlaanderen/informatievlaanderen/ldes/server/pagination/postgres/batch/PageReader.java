package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class PageReader {

	@Bean
	@StepScope
	public ItemReader<Page> pageItemReader(DataSource dataSource,
	                                       @Value("#{stepExecutionContext}") Map<String, Object> stepExecutionContext) {
		return new JdbcPagingItemReaderBuilder<Page>()
				.dataSource(dataSource)
				.queryProvider(openPageQuery())
				.parameterValues(Map.of("bucket", stepExecutionContext.get("bucketId")))
				.rowMapper(new PaginationRowMapper())
				.saveState(false)
				.build();
	}

	private PostgresPagingQueryProvider openPageQuery() {
		PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
		queryProvider.setSelectClause("DISTINCT p.page_id, p.bucket_id, p.partial_url, v.page_size, COUNT(member_id) AS assigned_members");
		queryProvider.setFromClause("FROM pages p " +
		                            "LEFT JOIN page_members m ON p.page_id = m.page_id " +
		                            "JOIN buckets b ON p.bucket_id = b.bucket_id " +
		                            "JOIN views v ON v.view_id = b.view_id ");
		queryProvider.setWhereClause("b.bucket_id = :bucket AND " +
		                             "p.page_id NOT IN (SELECT from_page_id FROM page_relations)");
		queryProvider.setGroupClause("p.page_id, v.page_size");
		queryProvider.setSortKeys(Map.of("page_id", Order.ASCENDING));
		return queryProvider;
	}

}