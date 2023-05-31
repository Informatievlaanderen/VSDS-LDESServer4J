package be.vlaanderen.informatievlaanderen.ldes.server.rest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Component
public class EtagCachingStrategy implements CachingStrategy {

	private final AppConfig appConfig;

	public EtagCachingStrategy(final AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	@Override
	public String generateCacheIdentifier(String collectionName, String language) {
		return sha256Hex(appConfig.getHostName() + "/" + collectionName + "?lang=" + language);
	}

	@Override
	public String generateCacheIdentifier(TreeNode treeNode, String language) {
		return sha256Hex(treeNode.getFragmentId()
				+ treeNode.getRelations().stream()
				.map(TreeRelation::treeNode)
				.collect(Collectors.joining(""))
				+ treeNode.getMembers().stream()
				.map(Member::getLdesMemberId)
				.collect(Collectors.joining(""))
				+ language);
	}
}
