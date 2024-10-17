package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.projection.TreeMemberProjection;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFParser;
import org.springframework.jdbc.core.RowMapper;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.SERIALISATION_LANG;

public class TreeMemberRowMapper implements RowMapper<TreeMemberProjection> {
	@Override
	public TreeMemberProjection mapRow(ResultSet rs, int rowNum) throws SQLException {
		Model model = RDFParser.source(new ByteArrayInputStream(rs.getBytes("model")))
				.lang(SERIALISATION_LANG).toModel();;

		return new TreeMemberProjection(rs.getString("subject"), model, rs.getString("versionOf"),
				LocalDateTime.parse(rs.getString("timestamp")));
	}
}
