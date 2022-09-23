package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.util.List;
import java.util.Optional;

public class GeospatialFragmentCreator implements FragmentCreator {

	private final LdesConfig ldesConfig;

	public GeospatialFragmentCreator(final LdesConfig ldesConfig) {
		this.ldesConfig = ldesConfig;
	}

	@Override
	public LdesFragment createNewFragment(Optional<LdesFragment> optionalExistingLdesFragment,
			FragmentInfo parentFragmentInfo) {
		return createNewFragment(parentFragmentInfo);
	}

	@Override
	public boolean needsToCreateNewFragment(LdesFragment fragment) {
		return true;
	}

	protected LdesFragment createNewFragment(FragmentInfo parentFragmentInfo) {
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
