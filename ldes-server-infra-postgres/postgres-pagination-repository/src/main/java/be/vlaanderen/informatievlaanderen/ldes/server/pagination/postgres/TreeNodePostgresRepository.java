package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeNodeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation.VersionObjectCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation.VersionObjectCreatorFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper.TreeNodeMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeRelationProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.RelationEntityRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TreeNodePostgresRepository implements TreeNodeRepository {
	private final PageEntityRepository pageEntityRepository;
	private final RelationEntityRepository relationEntityRepository;
	private final PageMemberEntityRepository pageMemberEntityRepository;
	private final Map<String, VersionObjectCreator> versionObjectCreatorMap = new HashMap<>();

	public TreeNodePostgresRepository(PageEntityRepository pageEntityRepository, RelationEntityRepository relationEntityRepository, PageMemberEntityRepository pageMemberEntityRepository) {
		this.pageEntityRepository = pageEntityRepository;
		this.relationEntityRepository = relationEntityRepository;
		this.pageMemberEntityRepository = pageMemberEntityRepository;
	}

	@Override
	public Optional<TreeNode> findByFragmentIdentifier(LdesFragmentIdentifier fragmentIdentifier) {
		return pageEntityRepository
				.findTreeNodeByPartialUrl(fragmentIdentifier.asDecodedFragmentId())
				.map(page -> {
					final List<TreeRelationProjection> relations = relationEntityRepository.findDistinctByFromPageId(page.getId());

					var versionObjectCreator = versionObjectCreatorMap.get(page.getCollectionName());

					final List<Member> members = pageMemberEntityRepository.findAllMembersByPageId(page.getId())
							.stream()
							.map(treeMemberProjection -> new Member(treeMemberProjection.getSubject(),
									versionObjectCreator.createFromMember(treeMemberProjection.getSubject(),
											treeMemberProjection.getModel(), treeMemberProjection.getVersionOf(),
											treeMemberProjection.getTimestamp())))
							.toList();
					return TreeNodeMapper.fromProjection(page, relations, members);
				});
	}

	@Override
	public Optional<TreeNode> findTreeNodeWithoutMembers(LdesFragmentIdentifier fragmentIdentifier) {
		return pageEntityRepository
				.findTreeNodeByPartialUrl(fragmentIdentifier.asDecodedFragmentId())
				.map(page -> {
					final List<TreeRelationProjection> relations = relationEntityRepository.findDistinctByFromPageId(page.getId());
					return TreeNodeMapper.fromProjection(page, relations, List.of());
				});
	}

	@EventListener
	public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
		final EventStream eventStream = event.eventStream();
		final VersionObjectCreator versionObjectCreator = VersionObjectCreatorFactory.createVersionObjectCreator(eventStream);
		versionObjectCreatorMap.put(eventStream.getCollection(), versionObjectCreator);
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		versionObjectCreatorMap.remove(event.collectionName());
	}
}
