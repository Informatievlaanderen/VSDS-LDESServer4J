package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.mapper;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.UnprocessedView;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UnprocessedViewRowMapper implements RowMapper<UnprocessedView> {
	@Override
	public UnprocessedView mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new UnprocessedView(
				rs.getInt("collection_id"),
				rs.getString("collection_name"),
				rs.getInt("view_id"),
				rs.getString("view_name")
		);
	}
}
