package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.PostgresIngestMemberConstants;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.springframework.jdbc.core.RowMapper;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberRowMapper implements RowMapper<Member> {
	@Override
	public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
		final Model model = RDFParser
				.source(new ByteArrayInputStream(rs.getBytes("member_model")))
				.lang(PostgresIngestMemberConstants.SERIALISATION_LANG)
				.toModel();
		return new Member(rs.getString("subject"), model);
	}
}
