package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.Tile;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.BoundingBox;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.distance.AdjacentLdesFragmentFilter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.BoundingBoxConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.TileConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import org.springframework.stereotype.Component;

import java.util.List;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.*;

@Component
public class GeospatialRelationsAttributer {

	public void addGeospatialRelationsToNeighbouringFragments(LdesFragment ldesFragment,
			List<LdesFragment> ldesFragments) {
		AdjacentLdesFragmentFilter adjacentLdesFragmentFilter = new AdjacentLdesFragmentFilter(ldesFragment);
		ldesFragments
				.stream()
				.filter(adjacentLdesFragmentFilter)
				.forEach(
						neighbourFragment -> {
							addRelationToTargetFragment(ldesFragment, neighbourFragment);
							addRelationToTargetFragment(neighbourFragment, ldesFragment);
						});
	}

	private void addRelationToTargetFragment(LdesFragment sourceFragment, LdesFragment targetFragment) {
		String targetWKT = getWKT(targetFragment);

		TreeRelation relationToTargetFragment = new TreeRelation(GEOSPARQL_AS_WKT, targetFragment.getFragmentId(),
				WGS_84 + " " + targetWKT, WKT_DATA_TYPE, TREE_GEOSPATIALLY_CONTAINS_RELATION);
		if (!sourceFragment.getRelations().contains(relationToTargetFragment)) {
			sourceFragment.addRelation(relationToTargetFragment);
		}
	}

	private String getWKT(LdesFragment currentFragment) {
		Tile currentTile = TileConverter.fromString(currentFragment.getFragmentInfo().getFragmentPairs().stream()
				.filter(fragmentPair -> fragmentPair.fragmentKey().equals("tile")).findFirst().get().fragmentValue());
		BoundingBox currentBoundingBox = new BoundingBox(currentTile);
		return BoundingBoxConverter.toWKT(currentBoundingBox);
	}
}
