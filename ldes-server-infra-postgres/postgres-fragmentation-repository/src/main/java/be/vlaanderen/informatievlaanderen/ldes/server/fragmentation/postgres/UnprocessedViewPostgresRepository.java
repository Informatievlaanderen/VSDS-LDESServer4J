package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.UnprocessedView;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper.UnprocessedViewRowMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.repository.UnprocessedViewRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UnprocessedViewPostgresRepository implements UnprocessedViewRepository {
	public static final String SQL = "select * from unprocessed_views";
	private final JdbcTemplate jdbcTemplate;

	public UnprocessedViewPostgresRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UnprocessedView> findAll() {
		return jdbcTemplate.query(SQL, new UnprocessedViewRowMapper());
	}
}
