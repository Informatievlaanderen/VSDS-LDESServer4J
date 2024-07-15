package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.entity.MemberEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.mapper.MemberEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.entity.MemberViewsEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.mapper.MemberPropertiesEntityMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.projection.RetentionMemberProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.repository.MemberPropertiesEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.repository.MemberViewEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timeandversionbased.TimeAndVersionBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.timebased.TimeBasedRetentionPolicy;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.services.retentionpolicy.definition.versionbased.VersionBasedRetentionPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MemberPropertiesPostgresRepository implements MemberPropertiesRepository {
	private final MemberPropertiesEntityRepository propertiesRepo;
	private final MemberViewEntityRepository viewsRepo;
	private final EntityManager entityManager;
	private final MemberPropertiesEntityMapper propertiesMapper;
	private final MemberEntityRepository memberEntityRepository;
	private final MemberEntityMapper mapper;

	public MemberPropertiesPostgresRepository(MemberPropertiesEntityRepository propertiesRepo, MemberViewEntityRepository viewsRepo, EntityManager entityManager,
                                              MemberPropertiesEntityMapper propertiesMapper, MemberEntityRepository memberEntityRepository, MemberEntityMapper mapper) {
		this.propertiesRepo = propertiesRepo;
		this.viewsRepo = viewsRepo;
		this.entityManager = entityManager;
		this.propertiesMapper = propertiesMapper;
        this.memberEntityRepository = memberEntityRepository;
        this.mapper = mapper;
    }

	@Override
	@Transactional
	public void removeViewReference(String id, String viewName) {
		viewsRepo.deleteViewForMember(viewName, id);
	}

	@Override
	public void removePageMemberEntity(Long id, String viewName) {
		Query query = entityManager.createQuery("DELETE FROM page_members p WHERE p.MemberEntity.id == :memberId AND p.BucketEntity.ViewEntity.name == :viewName");
		query.setParameter("viewName", viewName);
		query.setParameter("memberId", id);
		query.executeUpdate();
	}

	@Override
	@Transactional
	public void removeMemberPropertiesOfCollection(String collectionName) {
		propertiesRepo.deleteAllByCollectionName(collectionName);
	}

	@Override
	public void deleteAllByIds(List<Long> ids) {
		memberEntityRepository.deleteAllByIdIn(ids);
	}

	@Override
	@Transactional
	public void removeFromEventSource(List<Long> ids) {
		Query query = entityManager.createQuery("UPDATE MemberEntity m SET m.isInEventSource = :isInEventSource " +
		                                        "WHERE m.id IN :memberIds");
		query.setParameter("isInEventSource", false);
		query.setParameter("memberIds", ids);
		query.executeUpdate();
	}

	@Override
	@Transactional
	public void removeExpiredMembers(ViewName viewName,
									 TimeBasedRetentionPolicy policy) {
		memberEntityRepository.findAllByViewNameAndTimestampBefore(viewName.getCollectionName(), LocalDateTime.now().minus(policy.duration()))
				.forEach(m -> removePageMemberEntity(m.getId(), viewName.asString()));
	}

	@Override
	@Transactional
	public void removeExpiredMembers(ViewName viewName,
									 VersionBasedRetentionPolicy policy) {

		memberEntityRepository.findAllByViewName(viewName.getCollectionName())
				.sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
				.collect(Collectors.groupingBy(MemberEntity::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.skip(policy.numberOfMembersToKeep()))
				.forEach(m -> removePageMemberEntity(m.getId(), viewName.asString()));;

	}

	@Override
	@Transactional
	public void removeExpiredMembers(ViewName viewName,
									 TimeAndVersionBasedRetentionPolicy policy) {
		memberEntityRepository.findAllByViewNameAndTimestampBefore(viewName.getCollectionName(), LocalDateTime.now().minus(policy.duration()))
				.sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
				.collect(Collectors.groupingBy(MemberEntity::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.skip(policy.numberOfMembersToKeep()))
				.forEach(m -> removePageMemberEntity(m.getId(), viewName.asString()));;
	}

	@Override
	public Stream<MemberProperties> retrieveExpiredMembers(String collectionName, TimeBasedRetentionPolicy policy) {
		return propertiesRepo.findAllByCollectionNameAndTimestampBefore(collectionName, LocalDateTime.now().minus(policy.duration()))
				.map(propertiesMapper::toMemberProperties);
	}

	@Override
	public Stream<MemberProperties> retrieveExpiredMembers(String collectionName, VersionBasedRetentionPolicy policy) {
		return propertiesRepo.findAllByCollectionName(collectionName)
				.stream()
				.sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
				.collect(Collectors.groupingBy(RetentionMemberProjection::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.skip(policy.numberOfMembersToKeep()))
				.map(propertiesMapper::toMemberProperties);
	}

	@Override
	public Stream<MemberProperties> retrieveExpiredMembers(String collectionName, TimeAndVersionBasedRetentionPolicy policy) {
		return propertiesRepo.findAllByCollectionNameAndTimestampBefore(collectionName, LocalDateTime.now().minus(policy.duration()))
				.sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
				.collect(Collectors.groupingBy(RetentionMemberProjection::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.skip(policy.numberOfMembersToKeep()))
				.map(propertiesMapper::toMemberProperties);
	}

}
