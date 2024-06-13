package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.batch;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberEntityMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberRowMapper implements RowMapper<IngestedMember> {
	MemberEntityMapper mapper = new MemberEntityMapper();
	@Override
	public IngestedMember mapRow(ResultSet rs, int rowNum) throws SQLException {
		var entity = new MemberEntity(rs.getString(1), rs.getString(2),
				rs.getString(8), rs.getTimestamp(6).toLocalDateTime(),
				rs.getLong(5), rs.getBoolean(3), rs.getString(7),
				rs.getBytes(4));
		return mapper.toMember(entity);
	}
}
