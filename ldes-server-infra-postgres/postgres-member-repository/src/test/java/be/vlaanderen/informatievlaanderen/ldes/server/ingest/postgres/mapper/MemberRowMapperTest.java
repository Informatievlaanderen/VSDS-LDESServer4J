package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
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

import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberRowMapper.MEMBER_MODEL_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberRowMapper.SUBJECT_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberRowMapperTest {
	private static Model memberModel;
	private final MemberRowMapper memberRowMapper = new MemberRowMapper();
	@Mock
	private ResultSet resultSet;

	@BeforeAll
	static void beforeAll() {
		memberModel = RDFParser.source("members/member.ttl").toModel();
	}

	@Test
	void given_ValidAndCompleteResultSet_test_MapRow() throws SQLException {
		final String subject = "http://mob-hind/1/0";
		when(resultSet.getString(SUBJECT_KEY)).thenReturn(subject);
		when(resultSet.getBytes(MEMBER_MODEL_KEY)).thenReturn(readMemberModel());

		final Member result = memberRowMapper.mapRow(resultSet, 1);

		assertThat(result)
				.isNotNull()
				.satisfies(actual -> assertThat(actual.subject()).isEqualTo(subject))
				.satisfies(actual -> assertThat(actual.model()).matches(memberModel::isIsomorphicWith));
	}

	private byte[] readMemberModel() {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		RDFWriter
				.source(memberModel)
				.lang(PostgresIngestMemberConstants.SERIALISATION_LANG)
				.output(output);
		return output.toByteArray();
	}


}