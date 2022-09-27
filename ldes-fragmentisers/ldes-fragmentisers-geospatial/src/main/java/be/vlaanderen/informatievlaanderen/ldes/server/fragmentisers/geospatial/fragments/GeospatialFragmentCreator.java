package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.util.List;

public class GeospatialFragmentCreator {

	private final LdesConfig ldesConfig;

	public GeospatialFragmentCreator(final LdesConfig ldesConfig) {
		this.ldesConfig = ldesConfig;
	}

	public LdesFragment createNewFragment(FragmentInfo parentFragmentInfo) {
		List<FragmentPair> fragmentPairs = parentFragmentInfo.getFragmentPairs();
		FragmentInfo fragmentInfo = new FragmentInfo(
				parentFragmentInfo.getViewName(),
				fragmentPairs);

		return new LdesFragment(
				LdesFragmentNamingStrategy.generateFragmentName(ldesConfig.getHostName(), fragmentInfo.getViewName(),
						fragmentInfo.getFragmentPairs()),
				fragmentInfo);
	}
}
