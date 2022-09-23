package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.util.List;
import java.util.stream.Collectors;

public class LdesFragmentNamingStrategy {

	private LdesFragmentNamingStrategy() {
	}

	public static String generateFragmentName(String hostname, String viewname, List<FragmentPair> fragmentPairs) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(hostname)
				.append("/").append(viewname);

		if (!fragmentPairs.isEmpty()) {
			stringBuilder.append("?");
			stringBuilder
					.append(fragmentPairs.stream().map(fragmentPair -> fragmentPair.fragmentKey() +
							"=" + fragmentPair.fragmentValue()).collect(Collectors.joining("&")));
		}

		return stringBuilder.toString();
	}
}
