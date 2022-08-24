package be.vlaanderen.informatievlaanderen.ldes.server.domain.config;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

import java.util.stream.Collectors;

public interface LdesFragmentNamingStrategy {
	FragmentPair getFragmentationValue();

	default String generateFragmentName(LdesConfig config, FragmentInfo fragmentInfo) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(config.getHostName()).append("/").append(fragmentInfo.getCollectionName());

		if (!fragmentInfo.getFragmentPairs().isEmpty()) {
			stringBuilder.append("?");
			stringBuilder.append(fragmentInfo.getFragmentPairs().stream().map(fragmentPair -> fragmentPair.fragmentKey()+
					"="+fragmentPair.fragmentValue()).collect(Collectors.joining("&")));
		}

		return stringBuilder.toString();
	}
}
