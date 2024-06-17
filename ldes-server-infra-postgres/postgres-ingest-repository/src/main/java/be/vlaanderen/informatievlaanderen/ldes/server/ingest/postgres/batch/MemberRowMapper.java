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
		return new IngestedMember(rs.getString(2), rs.getString(10),
				rs.getString(9), rs.getTimestamp(7).toLocalDateTime(),
				rs.getLong(6), rs.getBoolean(4), rs.getString(8),
				RDFParser.source(new ByteArrayInputStream(rs.getBytes(5))).lang(PostgresIngestMemberConstants.SERIALISATION_LANG).toModel());
	}
}
