package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberRowMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import io.micrometer.core.instrument.Metrics;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.PostgresIngestMemberConstants.LDES_SERVER_DELETED_MEMBERS_COUNT;

@Repository
public class MemberPostgresRepository implements MemberRepository, TreeMemberRepository {
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
			String sql = "INSERT INTO members (subject, collection_id, version_of, timestamp, transaction_id, member_model, old_id) VALUES (?,?,?,?,?,?,?)";

			final List<Object[]> batchArgs = members.stream()
					.map(member -> new Object[]{
							member.getSubject(),
							collectionId,
							member.getVersionOf(),
							member.getTimestamp(),
							member.getTransactionId(),
							modelConverter.convertToDatabaseColumn(member.getModel()),
							member.getCollectionName() + "/" + member.getSubject()
					})
					.toList();

			jdbcTemplate.batchUpdate(sql, batchArgs);

			return members;
		} else {
			return List.of();
		}
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
	public Stream<IngestedMember> findAllByIds(List<String> memberIds) {
		return repository.findAllByOldIdIn(memberIds)
				.stream()
				.map(mapper::toMember);
	}

	@Override
	public Stream<IngestedMember> findAllByCollectionAndSubject(String collectionName, List<String> subjects) {
		return repository.findAllByCollectionNameAndSubjectIn(collectionName, subjects)
				.stream()
				.map(mapper::toMember);
	}

	@Override
	@Transactional
	public void deleteMembers(List<Long> ids) {
		repository.deleteAllByIdIn(ids);
		Metrics.counter(LDES_SERVER_DELETED_MEMBERS_COUNT).increment(ids.size());
	}

	@Override
	@Transactional
	public void deleteMembersByCollectionNameAndSubjects(String collectionName, List<String> subjects) {
		repository.deleteAllByCollectionNameAndSubjectIn(collectionName, subjects);
		Metrics.counter(LDES_SERVER_DELETED_MEMBERS_COUNT).increment(subjects.size());
	}

	@Override
	@Transactional
	public void removeFromEventSource(List<Long> ids) {
        jdbcTemplate.update("UPDATE members SET is_in_event_source = false WHERE member_id IN ?", ids);
    }

	@Override
	public List<IngestedMember> getMembersOfCollection(String collectionName) {
		return repository.findAllByCollectionName(collectionName).stream().map(mapper::toMember).toList();
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