package be.vlaanderen.informatievlaanderen.ldes.server.rest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfigDeprecated;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.CollectionNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesSpecification;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EtagCachingStrategy implements CachingStrategy {

	private final LdesConfig ldesConfig;

	public EtagCachingStrategy(final LdesConfig ldesConfig) {
		this.ldesConfig = ldesConfig;
	}

	@Override
	public String generateCacheIdentifier(EventStream eventStream) {
		LdesSpecification ldesSpecification = ldesConfig.getLdesSpecification(eventStream.collection())
				.orElseThrow(() -> new CollectionNotFoundException(eventStream.collection()));
		return DigestUtils.sha256Hex(ldesSpecification.getBaseUrl());
	}

	@Override
	public String generateCacheIdentifier(TreeNode treeNode) {
		return DigestUtils.sha256Hex(
				treeNode.getFragmentId()
						+ treeNode.getRelations().stream()
								.map(TreeRelation::treeNode)
								.collect(Collectors.joining(""))
						+ treeNode.getMembers().stream()
								.map(Member::getLdesMemberId)
								.collect(Collectors.joining("")));
	}
}
