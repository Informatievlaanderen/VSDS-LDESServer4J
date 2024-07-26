package be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.repository.TreeNodeRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.projection.TreeMemberProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.postgres.repository.MemberEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.mapper.TreeNodeMapper;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.projection.TreeRelationProjection;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.PageEntityRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.pagination.postgres.repository.RelationEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TreeNodePostgresRepository implements TreeNodeRepository {
	private final PageEntityRepository pageEntityRepository;
	private final RelationEntityRepository relationEntityRepository;
	private final MemberEntityRepository memberEntityRepository;

	public TreeNodePostgresRepository(PageEntityRepository pageEntityRepository, RelationEntityRepository relationEntityRepository, MemberEntityRepository memberEntityRepository) {
		this.pageEntityRepository = pageEntityRepository;
		this.relationEntityRepository = relationEntityRepository;
		this.memberEntityRepository = memberEntityRepository;
	}

	@Override
	public Optional<TreeNode> findByFragmentIdentifier(LdesFragmentIdentifier fragmentIdentifier) {
		return pageEntityRepository
				.findTreeNodeByPartialUrl(fragmentIdentifier.asDecodedFragmentId())
				.map(page -> {
					final List<TreeRelationProjection> relations = relationEntityRepository.findDistinctByFromPageId(page.getId());
					final List<TreeMemberProjection> members = memberEntityRepository.findAllByPageId(page.getId());
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
}
