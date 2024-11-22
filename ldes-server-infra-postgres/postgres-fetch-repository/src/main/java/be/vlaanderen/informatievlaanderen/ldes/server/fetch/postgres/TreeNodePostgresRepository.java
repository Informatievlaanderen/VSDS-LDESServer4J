package be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.mapper.TreeNodeMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.repository.FetchPageEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetch.postgres.repository.FetchPageMemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeNodeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation.VersionObjectCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.services.versioncreation.VersionObjectCreatorFactory;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.valueobjects.LdesFragmentIdentifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TreeNodePostgresRepository implements TreeNodeRepository {
	private final FetchPageEntityRepository pageEntityRepository;
	private final FetchPageMemberEntityRepository pageMemberEntityRepository;
	private final Map<String, VersionObjectCreator> versionObjectCreatorMap = new HashMap<>();

	public TreeNodePostgresRepository(FetchPageEntityRepository pageEntityRepository, FetchPageMemberEntityRepository pageMemberEntityRepository) {
		this.pageEntityRepository = pageEntityRepository;
		this.pageMemberEntityRepository = pageMemberEntityRepository;
	}

	@Override
	public Optional<TreeNode> findByFragmentIdentifier(LdesFragmentIdentifier fragmentIdentifier) {
		return pageEntityRepository
				.findTreeNodeProjectionByPartialUrl(fragmentIdentifier.asDecodedFragmentId())
				.map(projection -> {
					var versionObjectCreator = versionObjectCreatorMap.get(projection.getBucket().getView().getEventStream().getName());

					final List<Member> members = pageMemberEntityRepository.findAllMembersByPageId(projection.getId())
							.stream()
							.map(treeMemberProjection -> new Member(treeMemberProjection.getSubject(),
									versionObjectCreator.createFromMember(treeMemberProjection.getSubject(),
											treeMemberProjection.getModel(), treeMemberProjection.getVersionOf(),
											treeMemberProjection.getTimestamp())))
							.toList();
					return TreeNodeMapper.fromProjection(projection, members);
				});
	}

	@Override
	public Optional<TreeNode> findTreeNodeWithoutMembers(LdesFragmentIdentifier fragmentIdentifier) {
		return pageEntityRepository
				.findTreeNodeProjectionByPartialUrl(fragmentIdentifier.asDecodedFragmentId())
				.map(projection -> TreeNodeMapper.fromProjection(projection, List.of()));
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
