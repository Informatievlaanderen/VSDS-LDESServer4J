package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;

import java.util.List;

public class GeospatialFragmentCreator {

	public LdesFragment createNewFragment(FragmentInfo parentFragmentInfo) {
		List<FragmentPair> fragmentPairs = parentFragmentInfo.getFragmentPairs();
		FragmentInfo fragmentInfo = new FragmentInfo(
				parentFragmentInfo.getViewName(),
				fragmentPairs);

		return new LdesFragment(

				fragmentInfo);
	}
}
