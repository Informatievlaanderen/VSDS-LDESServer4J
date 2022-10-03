package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.fragments;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.repository.LdesFragmentRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.valueobjects.FragmentPair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class GeospatialFragmentCreatorTest {

	LdesFragmentRepository ldesFragmentRepository = mock(LdesFragmentRepository.class);
	GeospatialFragmentCreator geospatialFragmentCreator = new GeospatialFragmentCreator(ldesFragmentRepository);

	@Test
	void when_TileIsGiven_NewTileFragmentIsCreated() {
		LdesFragment ldesFragment = new LdesFragment(
				new FragmentInfo("view", List.of(new FragmentPair("substring", "a"))));
		LdesFragment childFragment = geospatialFragmentCreator.getOrCreateGeospatialFragment(ldesFragment,
				"15/101/202");
		assertEquals("/view?substring=a&tile=15/101/202", childFragment.getFragmentId());
	}
}