package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import java.util.ArrayList;
import java.util.List;

public class FragmentationConfigParser {

	private FragmentationConfigParser() {
	}

	public static List<FragmentationSpecification> getFragmentationSpecifications(
			FragmentationConfig fragmentationConfig) {
		List<FragmentationSpecification> fragmentations = new ArrayList<>();
		while (fragmentationConfig != null) {
			fragmentations.add(
					new FragmentationSpecification(fragmentationConfig.getName(),
							new FragmentationProperties(fragmentationConfig.getConfig())));
			fragmentationConfig = fragmentationConfig.getFragmentation();
		}
		return fragmentations;
	}
}
