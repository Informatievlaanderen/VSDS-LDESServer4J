package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.mapper.MemberRowMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeMemberRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class TreeMemberPostgresRepository implements TreeMemberRepository {

	private final JdbcTemplate jdbcTemplate;

	public TreeMemberPostgresRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Stream<Member> findAllByTreeNodeUrl(String url) {
		String sql = """
				SELECT m.subject, m.member_model
				FROM members m
				    JOIN page_members USING (member_id)
				    JOIN pages p USING (page_id)
				WHERE p.partial_url = ?""";
		return jdbcTemplate.query(sql, new MemberRowMapper(), url).stream();
	}
}
