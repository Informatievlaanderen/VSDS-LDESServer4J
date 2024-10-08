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

	static final String SUBJECT_KEY = "subject";
	static final String MEMBER_MODEL_KEY = "member_model";

	@Override
	public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
		final Model model = RDFParser
				.source(new ByteArrayInputStream(rs.getBytes(MEMBER_MODEL_KEY)))
				.lang(PostgresIngestMemberConstants.SERIALISATION_LANG)
				.toModel();
		return new Member(rs.getString(SUBJECT_KEY), model);
	}


}
