package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Stream;

@Repository
public class MemberPostgresRepository implements MemberRepository {
    private static final Logger log = LoggerFactory.getLogger(MemberPostgresRepository.class);
	private static final String INSERT_SQL = "INSERT INTO members (subject, collection_id, version_of, timestamp, transaction_id, member_model) VALUES (?,?,?,?,?,?)";
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
        log.atDebug().log("Inserting {} members into collection {}", members.size(), collectionId);

		if (membersContainDuplicateIds(members)) {
            log.atDebug().log("Duplicate members found in collection {}", collectionId);
			return 0;
		}

		try {
			var batchArgs = members.stream()
					.map(member -> new Object[]{
							member.getSubject(),
							collectionId,
							member.getVersionOf(),
							member.getTimestamp(),
							member.getTransactionId(),
							modelConverter.convertToDatabaseColumn(member.getModel()),
					})
					.toList();

			// Note: that we lock the collection row to ensure no interleaved sequence numbers are assigned to the member_id
			// Note: that the lock is released when txn is committed (upon method exit as it is transactional)
            log.atDebug().log("Lock collection row where collection_id = {}", collectionId);
			jdbcTemplate.queryForObject("SELECT name FROM collections WHERE collection_id = ? FOR UPDATE", String.class, collectionId);

            log.atDebug().log("Insert into members (subject, collection_id, version_of, timestamp, transaction_id, member_model): {}", batchArgs);
			jdbcTemplate.batchUpdate(INSERT_SQL, batchArgs);

            log.atDebug().log("Update collection stats: members.size = {}", members.size());
			updateCollectionStats(members.size(), collectionId);

			return members.size();
		} catch (DuplicateKeyException e) {
            log.atDebug().log("DuplicateKeyException - Duplicate members found in collection {}", collectionId);
			return 0;
		}
	}

	private void updateCollectionStats(int memberCount, int collectionId) {
		String sql = """
				update collection_stats cs set
				ingested_count = cs.ingested_count + ?
				where collection_id = ?;
				""";

		jdbcTemplate.update(sql, ps -> {
			ps.setInt(1, memberCount);
			ps.setInt(2, collectionId);
		});
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