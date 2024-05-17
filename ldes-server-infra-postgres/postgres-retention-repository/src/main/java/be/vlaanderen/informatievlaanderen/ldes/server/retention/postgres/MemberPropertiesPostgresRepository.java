package be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.entities.MemberProperties;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.entity.MemberViewsEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.postgres.mapper.MemberPropertiesEntityMapper;
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

	public MemberPropertiesPostgresRepository(MemberPropertiesEntityRepository propertiesRepo, MemberViewEntityRepository viewsRepo, EntityManager entityManager,
	                                          MemberPropertiesEntityMapper propertiesMapper) {
		this.propertiesRepo = propertiesRepo;
		this.viewsRepo = viewsRepo;
		this.entityManager = entityManager;
		this.propertiesMapper = propertiesMapper;
	}

	@Override
	@Transactional
	public void insertAll(List<MemberProperties> memberProperties) {
		propertiesRepo.saveAll(memberProperties.stream()
				.map(propertiesMapper::toMemberPropertiesEntity)
				.toList());
	}

	@Override
	public Optional<MemberProperties> retrieve(String id) {
		return propertiesRepo.findById(id).map(propertiesMapper::toMemberProperties);
	}

	@Override
	@Transactional
	public void addViewToAll(ViewName viewName) {
		propertiesRepo.findAllByCollectionName(viewName.getCollectionName())
				.stream()
				.map(memberProperty -> new MemberViewsEntity(memberProperty, viewName.asString()))
				.forEach(entityManager::persist);
	}

	@Override
	public List<MemberProperties> getMemberPropertiesOfVersionAndView(String versionOf, String viewName) {
		return propertiesRepo.findMembersWithVersionAndView(versionOf, viewName)
				.map(propertiesMapper::toMemberProperties)
				.toList();
	}

	@Override
	public Stream<MemberProperties> getMemberPropertiesWithViewReference(ViewName viewName) {
		return propertiesRepo.findMembersWithView(viewName.asString())
				.map(propertiesMapper::toMemberProperties);
	}

	@Override
	@Transactional
	public void removeViewReference(String id, String viewName) {
		viewsRepo.deleteViewForMember(viewName, id);
	}

	@Override
	@Transactional
	public void removeMemberPropertiesOfCollection(String collectionName) {
		propertiesRepo.deleteAllByCollectionName(collectionName);
	}

	@Override
	public void deleteAllByIds(List<String> ids) {
		propertiesRepo.deleteAllById(ids);
	}

	@Override
	public void removeFromEventSource(List<String> ids) {
		Query query = entityManager.createQuery("UPDATE MemberPropertiesEntity m SET m.isInEventSource = :isInEventSource " +
		                                        "WHERE m.id IN :memberIds");
		query.setParameter("isInEventSource", false);
		query.setParameter("memberIds", ids);
		query.executeUpdate();
	}

	@Override
	public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
	                                                            TimeBasedRetentionPolicy policy) {
		return propertiesRepo.findAllByCollectionNameAndTimestampBefore(viewName.getCollectionName(), LocalDateTime.now().minus(policy.duration()))
				.stream()
				.filter(entity -> entity.getViews().contains(viewName.asString()))
				.map(propertiesMapper::toMemberProperties);
	}

	@Override
	public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
	                                                            VersionBasedRetentionPolicy policy) {

		return propertiesRepo.findAllByCollectionName(viewName.getCollectionName())
				.stream()
				.filter(entity -> entity.getViews().contains(viewName.asString()))
				.sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
				.map(propertiesMapper::toMemberProperties)
				.collect(Collectors.groupingBy(MemberProperties::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.skip(policy.numberOfMembersToKeep()));
	}

	@Override
	public Stream<MemberProperties> findExpiredMemberProperties(ViewName viewName,
	                                                            TimeAndVersionBasedRetentionPolicy policy) {
		return propertiesRepo.findAllByCollectionNameAndTimestampBefore(viewName.getCollectionName(), LocalDateTime.now().minus(policy.duration()))
				.stream()
				.filter(entity -> entity.getViews().contains(viewName.asString()))
				.sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
				.map(propertiesMapper::toMemberProperties)
				.collect(Collectors.groupingBy(MemberProperties::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.skip(policy.numberOfMembersToKeep()));
	}

	@Override
	public Stream<MemberProperties> findExpiredMemberProperties(String collectionName, TimeBasedRetentionPolicy policy) {
		return propertiesRepo.findAllByCollectionNameAndTimestampBefore(collectionName, LocalDateTime.now().minus(policy.duration()))
				.stream()
				.map(propertiesMapper::toMemberProperties);
	}

	@Override
	public Stream<MemberProperties> findExpiredMemberProperties(String collectionName, VersionBasedRetentionPolicy policy) {
		return propertiesRepo.findAllByCollectionName(collectionName)
				.stream()
				.sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
				.map(propertiesMapper::toMemberProperties)
				.collect(Collectors.groupingBy(MemberProperties::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.skip(policy.numberOfMembersToKeep()));
	}

	@Override
	public Stream<MemberProperties> findExpiredMemberProperties(String collectionName, TimeAndVersionBasedRetentionPolicy policy) {
		return propertiesRepo.findAllByCollectionNameAndTimestampBefore(collectionName, LocalDateTime.now().minus(policy.duration()))
				.stream()
				.sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
				.map(propertiesMapper::toMemberProperties)
				.collect(Collectors.groupingBy(MemberProperties::getVersionOf))
				.values()
				.stream()
				.flatMap(memberPropertiesGroup -> memberPropertiesGroup.stream()
						.skip(policy.numberOfMembersToKeep()));
	}

}
