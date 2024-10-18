package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberRowMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class MemberPostgresRepository implements MemberRepository, TreeMemberRepository {
	private static final String INSERT_SQL = "INSERT INTO members (subject, collection_id, version_of, timestamp, transaction_id, member_model) VALUES (?,?,?,?,?,?)";
	private final MemberEntityRepository repository;
	private final MemberEntityMapper mapper;
	private final DatabaseColumnModelConverter modelConverter;
	private final JdbcTemplate jdbcTemplate;

	public MemberPostgresRepository(MemberEntityRepository repository,
	                                MemberEntityMapper mapper, DatabaseColumnModelConverter modelConverter, DataSource dataSource) {
		this.repository = repository;
		this.mapper = mapper;
		this.modelConverter = modelConverter;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	@Transactional
	public List<IngestedMember> insertAll(List<IngestedMember> members) {
		final int collectionId = getCollectionId(members.getFirst().getCollectionName());
		final List<String> subjects = members.stream().map(IngestedMember::getSubject).toList();
		if (!membersContainDuplicateIds(members) && !membersExistInCollection(collectionId, subjects)) {
			var toBatchMembers = members.stream()
					.filter(ingestedMember -> !ingestedMember.equals(members.getLast()))
					.toList();

			final List<Object[]> batchArgs = toBatchMembers.stream()
					.map(member -> new Object[]{
							member.getSubject(),
							collectionId,
							member.getVersionOf(),
							member.getTimestamp(),
							member.getTransactionId(),
							modelConverter.convertToDatabaseColumn(member.getModel()),
					})
					.toList();

			jdbcTemplate.batchUpdate(INSERT_SQL, batchArgs);

			int lastId = insertLastMember(members.getLast(), collectionId); // TODO: remove unused variable

			updateCollectionStats(members.size(), collectionId);

			return members;
		} else {
			return List.of();
		}
	}

	private int insertLastMember(IngestedMember lastMember, int collectionId) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, lastMember.getSubject());
			ps.setInt(2, collectionId);
			ps.setString(3, lastMember.getVersionOf());
			ps.setObject(4, lastMember.getTimestamp());
			ps.setString(5, lastMember.getTransactionId());
			ps.setBytes(6, modelConverter.convertToDatabaseColumn(lastMember.getModel()));
			return ps;
		}, keyHolder);

		return Integer.parseInt(Objects.requireNonNull(keyHolder.getKeys().get("member_id").toString()));
	}

	private void updateCollectionStats(int memberCount, int collectionId) {
		String SQL = """
				update collection_stats cs set
				ingested_count = cs.ingested_count + ?
				where collection_id = ?;
				""";

		jdbcTemplate.update(SQL, ps -> {
			ps.setInt(1, memberCount);
			ps.setInt(2, collectionId);
		});
	}

	protected boolean membersExistInCollection(int collectionId, List<String> subjects) {
		return repository.existsByCollectionAndSubjectIn(collectionId, subjects);
	}

	protected boolean membersContainDuplicateIds(List<IngestedMember> members) {
		return members.stream()
				.map(IngestedMember::getSubject)
				.collect(Collectors.toSet())
				.size() != members.size();
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

	@Override
	public Stream<Member> findAllByTreeNodeUrl(String url) {
		final String sql = """
				SELECT m.subject, m.member_model
				FROM members m
				    JOIN page_members USING (member_id)
				    JOIN pages p USING (page_id)
				WHERE p.partial_url = ?""";
		return jdbcTemplate.query(sql, new MemberRowMapper(), url).stream();
	}

	private int getCollectionId(String collectionName) {
		final Integer collectionId = jdbcTemplate.queryForObject("SELECT collection_id FROM collections WHERE name = ?", Integer.class, collectionName);
		if (collectionId == null) {
			throw new MissingResourceException("eventstream", collectionName);
		}
		return collectionId;
	}
}