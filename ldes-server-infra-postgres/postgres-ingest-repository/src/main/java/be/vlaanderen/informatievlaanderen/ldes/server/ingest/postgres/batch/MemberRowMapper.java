package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.PostgresIngestMemberConstants;
import org.apache.jena.riot.RDFParser;
import org.springframework.jdbc.core.RowMapper;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberRowMapper implements RowMapper<IngestedMember> {
	@Override
	public IngestedMember mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new IngestedMember(rs.getString(1), rs.getString(2),
				rs.getString(3), rs.getTimestamp(4).toLocalDateTime(),
				rs.getBoolean(5), rs.getString(6),
				RDFParser.source(new ByteArrayInputStream(rs.getBytes(7))).lang(PostgresIngestMemberConstants.SERIALISATION_LANG).toModel());
	}
}
