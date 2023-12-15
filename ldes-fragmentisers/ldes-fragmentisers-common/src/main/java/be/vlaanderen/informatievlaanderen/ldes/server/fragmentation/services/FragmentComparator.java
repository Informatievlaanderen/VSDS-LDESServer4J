package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FragmentComparator {
	public static Stream<Fragment> sortFragments(Stream<Fragment> fragmentStream) {
		List<Fragment> fragments = fragmentStream.toList();

		var firstElement = fragments.stream()
				.filter(fragment -> fragments.stream().noneMatch(fragment1 -> fragment1.isConnectedTo(fragment)))
				.findFirst()
				.orElseThrow();

		var map = fragments.stream()
				.filter(fragment -> !fragment.getRelations().isEmpty())
				.collect(Collectors.toMap(Fragment::getFragmentIdString, fragment -> fragment.getRelations()
						.stream()
						.findFirst()
						.map(TreeRelation::treeNode)
						.orElseThrow()));

		List<Fragment> fragmentList = new LinkedList<>(List.of(firstElement));
		String currentFragment = firstElement.getFragmentIdString();

		do {
			LdesFragmentIdentifier fragmentId = map.get(currentFragment);

			Optional<Fragment> foundFragment = fragments.stream()
					.filter(fragment -> fragment.getFragmentId().equals(fragmentId))
					.findFirst();

			if (foundFragment.isPresent()) {
				fragmentList.add(foundFragment.get());
				currentFragment = fragmentId.asString();
			} else {
				break;
			}

		} while (true);


		return fragmentList.stream();
	}
}
