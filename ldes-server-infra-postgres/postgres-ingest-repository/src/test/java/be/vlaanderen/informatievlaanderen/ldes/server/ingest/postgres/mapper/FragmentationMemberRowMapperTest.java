package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.FragmentationMember;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.PostgresIngestMemberConstants;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.FragmentationMemberRowMapper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FragmentationMemberRowMapperTest {
	public static final long MEMBER_ID = 123L;
	public static final String SUBJECT = "http://mob-hind/1/be-vla/123";
	public static final String VERSION_OF = "http://mob-hind/1/be-vla";
	public static final LocalDateTime TIMESTAMP = LocalDateTime.now();
	public static final String EVENT_STREAM_NAME = "mobility-hindrances";
	public static final String TIMESTAMP_PATH = "http://purl.org/dc/terms/created";
	public static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";
	public static final boolean VERSION_CREATION_ENABLED = false;
	private static Model memberModel;
	private static FragmentationMember fragmentationMember;
	private final FragmentationMemberRowMapper rowMapper = new FragmentationMemberRowMapper();
	@Mock
	private ResultSet resultSet;

	@BeforeAll
	static void beforeAll() {
		memberModel = RDFParser.source("members/member.ttl").toModel();
		fragmentationMember = new FragmentationMember(
				MEMBER_ID,
				SUBJECT,
				VERSION_OF,
				TIMESTAMP,
				new EventStreamProperties(EVENT_STREAM_NAME, VERSION_OF_PATH, TIMESTAMP_PATH, VERSION_CREATION_ENABLED),
				memberModel
		);
	}


	@Test
	void test_MapRow() throws SQLException {
		when(resultSet.getLong(MEMBER_ID_COLUMN_KEY)).thenReturn(MEMBER_ID);
		when(resultSet.getString(SUBJECT_COLUMN_KEY)).thenReturn(SUBJECT);
		when(resultSet.getString(VERSION_OF_COLUMN_KEY)).thenReturn(VERSION_OF);
		when(resultSet.getObject(TIMESTAMP_COLUMN_KEY, LocalDateTime.class)).thenReturn(TIMESTAMP);
		when(resultSet.getString(EVENT_STREAM_NAME_COLUMN_KEY)).thenReturn(EVENT_STREAM_NAME);
		when(resultSet.getString(TIMESTAMP_PATH_COLUMN_KEY)).thenReturn(TIMESTAMP_PATH);
		when(resultSet.getString(VERSION_OF_PATH_COLUMN_KEY)).thenReturn(VERSION_OF_PATH);
		when(resultSet.getBoolean(CREATE_VERSIONS_COLUMN_KEY)).thenReturn(VERSION_CREATION_ENABLED);
		when(resultSet.getBytes(MEMBER_MODEL_COLUMN_KEY)).thenReturn(mapModelToByteArray());

		final FragmentationMember result = rowMapper.mapRow(resultSet, 1);

		assertThat(result)
				.isNotNull()
				.matches(actual -> actual.getVersionModel().isIsomorphicWith(memberModel))
				.usingRecursiveComparison()
				.ignoringFields("model")
				.isEqualTo(fragmentationMember);
	}

	private byte[] mapModelToByteArray() {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		RDFWriter.source(memberModel).lang(PostgresIngestMemberConstants.SERIALISATION_LANG).output(output);
		return output.toByteArray();
	}
}