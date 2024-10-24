package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch.delegates;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.BucketRelation;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class PageRelationItemWriterConfig {

	@Bean
	public ItemWriter<BucketRelation> bucketRelationItemWriter(DataSource dataSource) {
		String sql = """
					INSERT INTO page_relations (from_page_id, to_page_id, relation_type, value, value_type, path)
					SELECT (SELECT page_id FROM pages WHERE partial_url = :fromPartialUrl),
					       (SELECT page_id FROM pages WHERE partial_url = :toPartialUrl),
					       :treeRelationType, :treeValue, :treeValueType, :treePath
					       on conflict do nothing
					""";
		return new JdbcBatchItemWriterBuilder<BucketRelation>()
				.dataSource(dataSource)
				.sql(sql)
				.itemSqlParameterSourceProvider(item -> new MapSqlParameterSource(Map.of(
						"fromPartialUrl", item.fromPartialUrl(),
						"toPartialUrl", item.toPartialUrl(),
						"treeRelationType", item.relation().treeRelationType(),
						"treeValue", item.relation().treeValue(),
						"treeValueType", item.relation().treeValueType(),
						"treePath", item.relation().treePath()
				)))
				.assertUpdates(false)
				.build();
	}
}
