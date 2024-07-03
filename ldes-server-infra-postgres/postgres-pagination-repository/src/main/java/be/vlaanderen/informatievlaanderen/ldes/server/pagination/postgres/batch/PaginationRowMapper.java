package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.PaginationPage;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaginationRowMapper implements RowMapper<PaginationPage> {
	@Override
	public PaginationPage mapRow(ResultSet rs, int rowNum) throws SQLException {
		return PaginationPage.createWithPartialUrl(
				rs.getLong("page_id"),
				rs.getLong("bucket_id"),
				rs.getString("partial_url"),
				rs.getInt("available_member_capacity")
		);	}
}
