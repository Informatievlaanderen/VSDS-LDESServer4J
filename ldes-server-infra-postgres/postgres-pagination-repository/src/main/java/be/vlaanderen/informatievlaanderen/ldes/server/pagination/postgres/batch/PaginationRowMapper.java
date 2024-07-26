package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.pagination.entities.Page;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaginationRowMapper implements RowMapper<Page> {
	@Override
	public Page mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Page.createWithPartialUrl(
				rs.getLong("page_id"),
				rs.getLong("bucket_id"),
				rs.getString("partial_url"),
				rs.getInt("available_member_capacity")
		);	}
}
