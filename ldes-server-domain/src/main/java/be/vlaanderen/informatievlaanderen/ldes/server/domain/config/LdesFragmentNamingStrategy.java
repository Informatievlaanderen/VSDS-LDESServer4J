package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;

import java.util.stream.Collectors;

public class LdesFragmentNamingStrategy {

	private LdesFragmentNamingStrategy() {
	}

	public static String generateFragmentName(LdesConfig config, FragmentInfo fragmentInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(config.getHostName()).append("/").append(fragmentInfo.getCollectionName())
				.append("/").append(fragmentInfo.getViewName());

		if (!fragmentInfo.getFragmentPairs().isEmpty()) {
			stringBuilder.append("?");
			stringBuilder
					.append(fragmentInfo.getFragmentPairs().stream().map(fragmentPair -> fragmentPair.fragmentKey() +
							"=" + fragmentPair.fragmentValue()).collect(Collectors.joining("&")));
		}

		return stringBuilder.toString();
	}
}
