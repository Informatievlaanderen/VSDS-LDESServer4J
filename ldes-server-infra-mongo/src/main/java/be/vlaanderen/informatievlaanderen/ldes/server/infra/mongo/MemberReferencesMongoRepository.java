package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MemberNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.memberreferences.entities.MemberReferencesRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities.MemberReferencesEntity;
import be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.repositories.MemberReferencesEntityRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

public class MemberReferencesMongoRepository implements MemberReferencesRepository {

	private final MemberReferencesEntityRepository repository;

	public MemberReferencesMongoRepository(MemberReferencesEntityRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public synchronized void saveMemberReference(String memberId, String treeNodeId) {
		MemberReferencesEntity memberReferencesEntity = repository.findById(memberId)
				.orElseGet(() -> new MemberReferencesEntity(memberId, new ArrayList<>()));
		memberReferencesEntity.addMemberReference(treeNodeId);
		repository.save(memberReferencesEntity);
	}

	@Override
	@Transactional
	public synchronized void removeMemberReference(String memberId, String treeNodeId) {
		MemberReferencesEntity memberReferencesEntity = repository
				.findById(memberId)
				.orElseThrow(() -> new MemberNotFoundException(memberId));
		memberReferencesEntity.removeMemberReference(treeNodeId);
		repository.save(memberReferencesEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public synchronized boolean hasMemberReferences(String memberId) {
		return repository
				.findById(memberId)
				.map(MemberReferencesEntity::hasMemberReferences)
				.orElseThrow(() -> new MemberNotFoundException(memberId));
	}

	@Override
	@Transactional
	public synchronized void deleteMemberReference(String memberId) {
		repository.deleteById(memberId);
	}
}
