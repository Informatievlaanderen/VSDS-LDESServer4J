package be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.batch.MemberRowMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.repositories.MemberRepository;
import io.micrometer.core.instrument.Metrics;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
	private final EntityManager entityManager;

	public MemberPostgresRepository(MemberEntityRepository repository,
	                                MemberEntityMapper mapper, DatabaseColumnModelConverter modelConverter, EntityManager entityManager) {
		this.repository = repository;
		this.mapper = mapper;
		this.modelConverter = modelConverter;
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public List<IngestedMember> insertAll(List<IngestedMember> members) {
		if (!membersContainDuplicateIds(members) && !membersExist(members)) {
			String sql = "INSERT INTO members (subject, collection_id, version_of, timestamp, transaction_id, is_in_event_source, member_model, old_id) SELECT o.subject, c.collection_id, o.version, cast(o.timestamp as timestamp), transaction, cast(o.eventSource as BOOLEAN), cast(o.model as BYTEA), o.oldId FROM collections c, (VALUES ";

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < members.size(); i++) {
				sb.append("(?, ?, ?, ?, ?, ?, ?, ?),");
			}
			sql += sb.substring(0, sb.length() - 1);
			sql += ") AS o(subject, version, timestamp, transaction, eventSource, model, collectionName, oldId) WHERE c.name = o.collectionName";

			Query query = entityManager.createNativeQuery(sql);

			int index = 1;
			for (IngestedMember member : members) {
				query.setParameter(index++, member.getSubject());
				query.setParameter(index++, member.getVersionOf());
				query.setParameter(index++, member.getTimestamp());
				query.setParameter(index++, member.getTransactionId());
				query.setParameter(index++, member.isInEventSource());
				query.setParameter(index++, modelConverter.convertToDatabaseColumn(member.getModel()));
				query.setParameter(index++, member.getCollectionName());
				query.setParameter(index++, member.getCollectionName() + "/" + member.getSubject());
			}

			query.executeUpdate();
			return members;
		} else {
			return List.of();
		}
	}

	protected boolean membersExist(List<IngestedMember> members) {
		return repository.existsByOldIdIn(members.stream().map(member -> member.getCollectionName() + "/" + member.getSubject()).toList());
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
	public void deleteMembers(List<String> oldIds) {
		repository.deleteAllByOldIdIn(oldIds);
		Metrics.counter(LDES_SERVER_DELETED_MEMBERS_COUNT).increment(oldIds.size());
	}

	@Override
	@Transactional
	public void deleteMembersByCollectionNameAndSubjects(String collectionName, List<String> subjects) {
		repository.deleteAllByCollectionNameAndSubjectIn(collectionName, subjects);
		Metrics.counter(LDES_SERVER_DELETED_MEMBERS_COUNT).increment(subjects.size());
	}

	@Override
	@Transactional
	public void removeFromEventSource(List<String> ids) {
		Query query = entityManager.createQuery("UPDATE MemberEntity m SET m.isInEventSource = false " +
				"WHERE m.oldId IN :memberIds");
		query.setParameter("memberIds", ids);
		query.executeUpdate();
	}

	@Override
	public List<IngestedMember> getMembersOfCollection(String collectionName) {
		return repository.findAllByCollectionName(collectionName).stream().map(mapper::toMember).toList();
	}

	@Override
	public Stream<Member> findAllByTreeNodeUrl(String url) {
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(entityManager.unwrap(DataSource.class));
		final String sql = "SELECT m.subject, m.member_model FROM members m JOIN page_members pm USING (member_id) JOIN pages p USING (page_id) WHERE p.partial_url = ?";
		return jdbcTemplate.query(sql, new MemberRowMapper(), url).stream();
	}
}