package be.vlaanderen.informatievlaanderen.ldes.server.rest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EtagCachingStrategy implements CachingStrategy {

	private final AppConfig appConfig;

	public EtagCachingStrategy(final AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	@Override
	public String generateCacheIdentifier(EventStream eventStream) {
		return DigestUtils.sha256Hex(appConfig.getHostName() + "/" + eventStream.collection());
	}

	@Override
	public String generateCacheIdentifier(String collectionName) {
		return DigestUtils.sha256Hex(appConfig.getHostName() + "/" + collectionName);
	}

	@Override
	public String generateCacheIdentifier(TreeNode treeNode) {
		return DigestUtils.sha256Hex(
				treeNode.getFragmentId()
						+ treeNode.getRelations().stream()
								.map(TreeRelation::treeNode)
								.map(LdesFragmentIdentifier::asString)
								.collect(Collectors.joining(""))
						+ treeNode.getMembers().stream()
								.map(Member::getLdesMemberId)
								.collect(Collectors.joining("")));
	}
}
