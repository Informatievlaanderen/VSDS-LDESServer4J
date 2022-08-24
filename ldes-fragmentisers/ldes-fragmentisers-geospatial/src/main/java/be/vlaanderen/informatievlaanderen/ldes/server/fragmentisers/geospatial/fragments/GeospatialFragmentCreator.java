package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesFragmentNamingStrategy;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;

import java.util.List;
import java.util.Optional;

public class GeospatialFragmentCreator implements FragmentCreator {

	private final LdesConfig ldesConfig;
	protected final LdesFragmentNamingStrategy ldesFragmentNamingStrategy;

	public GeospatialFragmentCreator(final LdesConfig ldesConfig,
			LdesFragmentNamingStrategy ldesFragmentNamingStrategy) {
		this.ldesConfig = ldesConfig;
		this.ldesFragmentNamingStrategy = ldesFragmentNamingStrategy;
	}

	@Override
	public LdesFragment createNewFragment(Optional<LdesFragment> optionalExistingLdesFragment, List<FragmentPair> bucket) {
		return createNewFragment(bucket);
	}

	@Override
	public boolean needsToCreateNewFragment(LdesFragment fragment) {
		return true;
	}

	protected LdesFragment createNewFragment(List<FragmentPair> bucket) {
		FragmentInfo fragmentInfo = new FragmentInfo(
				ldesConfig.getCollectionName(),
				bucket);

		return new LdesFragment(ldesFragmentNamingStrategy.generateFragmentName(ldesConfig, fragmentInfo),
				fragmentInfo);
	}
}
