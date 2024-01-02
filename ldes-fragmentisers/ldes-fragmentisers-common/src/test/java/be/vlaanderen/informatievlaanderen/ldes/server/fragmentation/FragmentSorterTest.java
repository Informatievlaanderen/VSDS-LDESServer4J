package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.model.LdesFragmentIdentifier.fromFragmentId;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.services.FragmentSorter.sortFragments;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FragmentSorterTest {

	private static final String MOBHIND_PAGE = "/mobility-hindrances/by-page?pageNumber=";

	private static final String SCENARIO_1 = """
			/mobility-hindrances/by-page?pageNumber=2 | /mobility-hindrances/by-page?pageNumber=3,/dummy/dummy
			/mobility-hindrances/by-page?pageNumber=3 | /mobility-hindrances/by-page?pageNumber=4/5
			/mobility-hindrances/by-page?pageNumber=1 | /mobility-hindrances/by-page?pageNumber=2
			""";

	private static final String SCENARIO_2 = """
			/mobility-hindrances/by-page?pageNumber=2 | /mobility-hindrances/by-page?pageNumber=3
			/mobility-hindrances/by-page?pageNumber=1 | /mobility-hindrances/by-page?pageNumber=2
			/mobility-hindrances/by-page?pageNumber=3 | /mobility-hindrances/by-page?pageNumber=4
			""";

	private static final String SCENARIO_3 = """
			/mobility-hindrances/by-page?pageNumber=2 | /mobility-hindrances/by-page?pageNumber=3,/dummy/dummy
			/mobility-hindrances/by-page?pageNumber=3 | /mobility-hindrances/by-page?pageNumber=4/5
			/mobility-hindrances/by-page?pageNumber=1 | /mobility-hindrances/by-page?pageNumber=2
			/mobility-hindrances/by-page?pageNumber=0 | /mobility-hindrances/by-page?pageNumber=1
			""";

	private static final String SCENARIO_4 = """
			/mobility-hindrances/by-page?pageNumber=2 | /mobility-hindrances/by-page?pageNumber=3,/dummy/dummy
			/mobility-hindrances/by-page?pageNumber=3 | /mobility-hindrances/by-page?pageNumber=4
			/mobility-hindrances/by-page?pageNumber=4 | /mobility-hindrances/by-page?pageNumber=5
			/mobility-hindrances/by-page?pageNumber=1 | /mobility-hindrances/by-page?pageNumber=2
			/mobility-hindrances/by-page?pageNumber=0 | /mobility-hindrances/by-page?pageNumber=1
			""";

	@ParameterizedTest
	@MethodSource("provideStringsForIsBlank")
	void compare(FragmentationComparerInput fragmentStream) {
		var orderedList = sortFragments(fragmentStream.fragments)
				.map(Fragment::getFragmentIdString)
				.toList();
		assertEquals(fragmentStream.expectedOrderedList(), orderedList);
	}

	private static Stream<Fragment> getFragmentsOfScenario(String scenario) {
		return Arrays.stream(scenario.split("\n"))
				.map(f -> {
					var fragmentString = f.split("\\|");
					String fragmentId = fragmentString[0].trim();
					var fragmentsPointingTo = Arrays.stream(fragmentString[1].trim()
									.split(","))
							.map(String::trim).toList();
					return createFragment(fragmentId, fragmentsPointingTo);
				});
	}

	private static Fragment createFragment(String fragmentId, List<String> fragmentsPointingTo) {
		return new Fragment(fromFragmentId(fragmentId), true, 10,
				fragmentsPointingTo.stream()
						.map(treeNode -> new TreeRelation(null, fromFragmentId(treeNode), null, null, null))
						.toList(), null);
	}

	private static Stream<FragmentationComparerInput> provideStringsForIsBlank() {
		return Stream.of(
				new FragmentationComparerInput(getFragmentsOfScenario(SCENARIO_1),
						List.of(MOBHIND_PAGE + "1", MOBHIND_PAGE + "2", MOBHIND_PAGE + "3")),
				new FragmentationComparerInput(getFragmentsOfScenario(SCENARIO_2),
						List.of(MOBHIND_PAGE + "1", MOBHIND_PAGE + "2", MOBHIND_PAGE + "3")),
				new FragmentationComparerInput(getFragmentsOfScenario(SCENARIO_3),
						List.of(MOBHIND_PAGE + "0", MOBHIND_PAGE + "1", MOBHIND_PAGE + "2", MOBHIND_PAGE + "3")),
				new FragmentationComparerInput(getFragmentsOfScenario(SCENARIO_4),
						List.of(MOBHIND_PAGE + "0", MOBHIND_PAGE + "1", MOBHIND_PAGE + "2", MOBHIND_PAGE + "3", MOBHIND_PAGE + "4"))
		);
	}

	record FragmentationComparerInput(Stream<Fragment> fragments, List<String> expectedOrderedList) {
	}
}
