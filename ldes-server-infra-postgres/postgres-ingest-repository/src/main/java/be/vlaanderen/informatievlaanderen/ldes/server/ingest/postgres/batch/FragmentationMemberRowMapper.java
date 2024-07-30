package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.PostgresIngestMemberConstants;
import org.apache.jena.riot.RDFParser;
import org.springframework.jdbc.core.RowMapper;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class FragmentationMemberRowMapper implements RowMapper<FragmentationMember> {
	@Override
	public FragmentationMember mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new FragmentationMember(
				rs.getLong("member_id"),
				rs.getString("subject"),
				rs.getString("version_of"),
				rs.getObject("timestamp", LocalDateTime.class),
				new EventStreamProperties(rs.getString("name"), rs.getString("version_of_path"), rs.getString("timestamp_path"), rs.getBoolean("create_versions")),
				RDFParser.source(new ByteArrayInputStream(rs.getBytes("member_model"))).lang(PostgresIngestMemberConstants.SERIALISATION_LANG).toModel()
		);
	}
}
