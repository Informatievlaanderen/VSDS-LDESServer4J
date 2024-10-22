package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Repository
public class MemberPostgresRepository implements MemberRepository {
	private final MemberEntityRepository repository;
	private final MemberEntityMapper mapper;
	private final DatabaseColumnModelConverter modelConverter;
	private final JdbcTemplate jdbcTemplate;

	public MemberPostgresRepository(MemberEntityRepository repository,
	                                MemberEntityMapper mapper,
	                                DatabaseColumnModelConverter modelConverter,
	                                DataSource dataSource) {
		this.repository = repository;
		this.mapper = mapper;
		this.modelConverter = modelConverter;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	@Transactional
	public int insertAll(List<IngestedMember> members) {
		final int collectionId = getCollectionId(members.getFirst().getCollectionName());
		if (membersContainDuplicateIds(members)) {
			return 0;
		}
		String sql = """
				INSERT INTO members (subject, collection_id, version_of, timestamp, transaction_id, member_model)
				VALUES (?,?,?,?,?,?)
				""";
		try {
			final List<Object[]> batchArgs = members.stream()
					.map(member -> new Object[]{
							member.getSubject(),
							collectionId,
							member.getVersionOf(),
							member.getTimestamp(),
							member.getTransactionId(),
							modelConverter.convertToDatabaseColumn(member.getModel()),
					})
					.toList();
			return Arrays.stream(jdbcTemplate.batchUpdate(sql, batchArgs)).sum();
		} catch (DuplicateKeyException e) {
			return 0;
		}
	}

	protected boolean membersContainDuplicateIds(List<IngestedMember> members) {
		return members.size() != members.stream().map(IngestedMember::getSubject).distinct().count();
	}


	@Override
	public Stream<IngestedMember> findAllByCollectionAndSubject(String collectionName, List<String> subjects) {
		return repository.findAllByCollectionNameAndSubjectIn(collectionName, subjects)
				.stream()
				.map(mapper::toMember);
	}

	@Override
	@Transactional
	public void deleteMembersByCollectionNameAndSubjects(String collectionName, List<String> subjects) {
		repository.deleteAllByCollectionNameAndSubjectIn(collectionName, subjects);
	}

	private int getCollectionId(String collectionName) {
		final Integer collectionId = jdbcTemplate.queryForObject("SELECT collection_id FROM collections WHERE name = ?", Integer.class, collectionName);
		if (collectionId == null) {
			throw new MissingResourceException("eventstream", collectionName);
		}
		return collectionId;
	}
}