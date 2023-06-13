package be.vlaanderen.informatievlaanderen.ldes.server.rest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.node.entities.TreeNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Component
public class EtagCachingStrategy implements CachingStrategy {

	private final String hostName;

	public EtagCachingStrategy(@Value("${ldes-server.host-name}") String hostName) {
		this.hostName = hostName;
	}

	@Override
	public String generateCacheIdentifier(String collectionName, String language) {
		return sha256Hex(hostName + "/" + collectionName + "?lang=" + language);
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
