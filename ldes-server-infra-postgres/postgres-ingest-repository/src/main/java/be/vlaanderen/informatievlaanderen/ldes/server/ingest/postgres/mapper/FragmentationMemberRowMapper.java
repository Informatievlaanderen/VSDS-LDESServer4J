package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.PostgresIngestMemberConstants;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.springframework.jdbc.core.RowMapper;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class FragmentationMemberRowMapper implements RowMapper<FragmentationMember> {

	static final String MEMBER_ID_COLUMN_KEY = "member_id";
	static final String SUBJECT_COLUMN_KEY = "subject";
	static final String VERSION_OF_COLUMN_KEY = "version_of";
	static final String TIMESTAMP_COLUMN_KEY = "timestamp";
	static final String EVENT_STREAM_NAME_COLUMN_KEY = "name";
	static final String VERSION_OF_PATH_COLUMN_KEY = "version_of_path";
	static final String TIMESTAMP_PATH_COLUMN_KEY = "timestamp_path";
	static final String CREATE_VERSIONS_COLUMN_KEY = "create_versions";
	static final String MEMBER_MODEL_COLUMN_KEY = "member_model";

	@Override
	public FragmentationMember mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new FragmentationMember(
				rs.getLong(MEMBER_ID_COLUMN_KEY),
				rs.getString(SUBJECT_COLUMN_KEY),
				rs.getString(VERSION_OF_COLUMN_KEY),
				rs.getObject(TIMESTAMP_COLUMN_KEY, LocalDateTime.class),
				mapEventStreamProperties(rs),
				mapMemberModel(rs)
		);
	}

	private static EventStreamProperties mapEventStreamProperties(ResultSet rs) throws SQLException {
		return new EventStreamProperties(
				rs.getString(EVENT_STREAM_NAME_COLUMN_KEY),
				rs.getString(VERSION_OF_PATH_COLUMN_KEY),
				rs.getString(TIMESTAMP_PATH_COLUMN_KEY),
				rs.getBoolean(CREATE_VERSIONS_COLUMN_KEY)
		);
	}

	private static Model mapMemberModel(ResultSet rs) throws SQLException {
		return RDFParser.source(new ByteArrayInputStream(rs.getBytes(MEMBER_MODEL_COLUMN_KEY))).lang(PostgresIngestMemberConstants.SERIALISATION_LANG).toModel();
	}
}
