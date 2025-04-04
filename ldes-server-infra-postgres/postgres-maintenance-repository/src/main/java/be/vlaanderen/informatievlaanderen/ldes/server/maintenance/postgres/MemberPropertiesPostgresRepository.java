package be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.entity.RetentionMemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.mapper.MemberPropertiesEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.projection.RetentionMemberProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.maintenance.postgres.repository.RetentionMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MemberPropertiesPostgresRepository implements MemberPropertiesRepository {
	private final EntityManager entityManager;
	private final MemberPropertiesEntityMapper propertiesMapper;
	private final RetentionMemberEntityRepository memberEntityRepository;

	public MemberPropertiesPostgresRepository(EntityManager entityManager,
                                              MemberPropertiesEntityMapper propertiesMapper,
											  RetentionMemberEntityRepository memberEntityRepository) {
		this.entityManager = entityManager;
		this.propertiesMapper = propertiesMapper;
        this.memberEntityRepository = memberEntityRepository;
    }

	@Override
	@Transactional
	public void deleteAllByIds(List<Long> ids) {
		memberEntityRepository.deleteAllByIdIn(ids);
	}

	@Override
	@Transactional
	public void removeFromEventSource(List<Long> ids) {
		Query query = entityManager.createQuery("UPDATE RetentionMemberEntity m SET m.isInEventSource = :isInEventSource " +
		                                        "WHERE m.id IN :memberIds");
		query.setParameter("isInEventSource", false);
		query.setParameter("memberIds", ids);
		query.executeUpdate();
	}

	@Override
	@Transactional
	public List<Long> findExpiredMembers(ViewName viewName,
	                                     TimeBasedRetentionPolicy policy) {
		return memberEntityRepository.findAllByViewNameAndTimestampBefore(viewName.getViewName(), viewName.getCollectionName(), LocalDateTime.now().minus(policy.duration()))
				.sorted(Comparator.comparing(RetentionMemberEntity::getId).reversed())
				.map(RetentionMemberEntity::getId)
				.toList();
	}

	@Override
	@Transactional
	public List<Long> findExpiredMembers(ViewName viewName,
	                                     VersionBasedRetentionPolicy policy) {

		return memberEntityRepository.findAllByViewName(viewName.getViewName(), viewName.getCollectionName())
				.collect(Collectors.groupingBy(RetentionMemberEntity::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.sorted(Comparator.comparing(RetentionMemberEntity::getId).reversed())
						.skip(policy.numberOfMembersToKeep()))
				.map(RetentionMemberEntity::getId)
				.toList();

	}

	@Override
	@Transactional
	public List<Long> findExpiredMembers(ViewName viewName,
	                                     TimeAndVersionBasedRetentionPolicy policy) {
		return memberEntityRepository.findAllByViewNameAndTimestampBefore(viewName.getViewName(), viewName.getCollectionName(), LocalDateTime.now().minus(policy.duration()))
				.collect(Collectors.groupingBy(RetentionMemberEntity::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.sorted(Comparator.comparing(RetentionMemberEntity::getId).reversed())
						.skip(policy.numberOfMembersToKeep()))
				.map(RetentionMemberEntity::getId)
				.toList();
	}

	@Override
	@Transactional
	public Stream<MemberProperties> retrieveExpiredMembers(String collectionName, TimeBasedRetentionPolicy policy) {
		return memberEntityRepository.findAllByCollectionNameAndTimestampBefore(collectionName, LocalDateTime.now().minus(policy.duration())).stream()
				.map(propertiesMapper::toMemberProperties);
	}

	@Override
	@Transactional
	public Stream<MemberProperties> retrieveExpiredMembers(String collectionName, VersionBasedRetentionPolicy policy) {
		return memberEntityRepository.findAllByCollectionName(collectionName)
				.stream()
				.collect(Collectors.groupingBy(RetentionMemberProjection::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.skip(policy.numberOfMembersToKeep()))
				.map(propertiesMapper::toMemberProperties);
	}

	@Override
	@Transactional
	public Stream<MemberProperties> retrieveExpiredMembers(String collectionName, TimeAndVersionBasedRetentionPolicy policy) {
		return memberEntityRepository.findAllByCollectionNameAndTimestampBefore(collectionName, LocalDateTime.now().minus(policy.duration())).stream()
				.collect(Collectors.groupingBy(RetentionMemberProjection::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.skip(policy.numberOfMembersToKeep()))
				.map(propertiesMapper::toMemberProperties);
	}
}
