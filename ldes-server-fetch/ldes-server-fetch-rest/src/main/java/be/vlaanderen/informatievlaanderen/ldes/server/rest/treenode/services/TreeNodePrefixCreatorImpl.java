package be.vlaanderen.informatievlaanderen.ldes.server.rest.treenode.services;

import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.HOST_NAME_KEY;

@Component
public class TreeNodePrefixCreatorImpl implements TreeNodePrefixCreator {
	private final String hostName;

	public TreeNodePrefixCreatorImpl(@Value(HOST_NAME_KEY) String hostName) {
		this.hostName = hostName;
	}

	@Override
	public Map<String, String> createPrefixes(TreeNode treeNode) {
		String[] fragmentId = treeNode.getFragmentId().split("[/?]");
		return Map.of(
				fragmentId[1], hostName + "/" + treeNode.getCollectionName() + "/",
				fragmentId[2], hostName + treeNode.getFragmentId().split("\\?")[0] + "/"
		);
	}
}
