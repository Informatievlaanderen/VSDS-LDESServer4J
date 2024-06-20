package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.BucketisedMember;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BucketRowMapper implements RowMapper<BucketisedMember> {
	@Override
	public BucketisedMember mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new BucketisedMember(rs.getString(1), ViewName.fromString(rs.getString(2)),
				rs.getString(3));
	}
}
