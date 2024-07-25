package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities.TreeNode;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FragmentSorter {
	private FragmentSorter() {}

	public static Stream<TreeNode> sortFragments(Stream<TreeNode> fragmentStream) {
//		List<TreeNode> fragments = fragmentStream.toList();
//
//		var firstElement = fragments.stream()
//				.filter(fragment -> hasNoConnections(fragment, fragments))
//				.findFirst()
//				.orElseThrow();
//
//		var map = fragments.stream()
//				.filter(fragment -> !fragment.getRelations().isEmpty())
//				.collect(Collectors.toMap(Fragment::getFragmentIdString, fragment -> fragment.getRelations()
//						.stream()
//						.findFirst()
//						.map(TreeRelation::treeNode)
//						.orElseThrow()));
//
//		List<Fragment> fragmentList = new LinkedList<>(List.of(firstElement));
//		String currentFragment = firstElement.getFragmentIdString();
//		Optional<Fragment> foundFragment;
//
//		do {
//			LdesFragmentIdentifier fragmentId = map.get(currentFragment);
//
//			foundFragment = fragments.stream()
//					.filter(fragment -> fragment.getFragmentId().equals(fragmentId))
//					.findFirst();
//
//			if (foundFragment.isPresent()) {
//				fragmentList.add(foundFragment.get());
//				currentFragment = fragmentId.asDecodedFragmentId();
//			}
//
//		} while (foundFragment.isPresent());


		return fragmentStream;//fragmentList.stream();
	}

	public static boolean hasNoConnections(TreeNode fragment, List<TreeNode> fragments) {
		return fragments.stream().noneMatch(fragment1 -> isConnectedTo(fragment1, fragment));
	}

	public static boolean isConnectedTo(TreeNode treeNode, TreeNode otherTreeNode) {
		return treeNode.getRelations()
				.stream()
				.anyMatch(treeRelation -> treeRelation.treeNode()
						.equals(LdesFragmentIdentifier.fromFragmentId(otherTreeNode.getFragmentId())));
	}
}
