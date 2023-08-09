package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.caching;

import be.vlaanderen.informatievlaanderen.ldes.server.fetchapplication.entities.TreeNodeDto;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.HOST_NAME_KEY;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Component
public class EtagCachingStrategy implements CachingStrategy {

	private final String hostName;

	public EtagCachingStrategy(@Value(HOST_NAME_KEY) String hostName) {
		this.hostName = hostName;
	}

	@Override
	public String generateCacheIdentifier(String collectionName, String language) {
		return sha256Hex(hostName + "/" + collectionName + "?lang=" + language);
	}

	@Override
	public String generateCacheIdentifier(TreeNodeDto treeNodeDto, String language) {
		return sha256Hex(treeNodeDto.getFragmentId()
				+ String.join("", treeNodeDto.getTreeNodeIdsInRelations())
				+ treeNodeDto.getMembers().stream()
						.map(Member::getId)
						.collect(Collectors.joining(""))
				+ language);
	}
}
