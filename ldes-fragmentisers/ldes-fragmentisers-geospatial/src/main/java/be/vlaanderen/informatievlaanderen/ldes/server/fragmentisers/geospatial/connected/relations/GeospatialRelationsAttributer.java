package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.BoundingBox;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.BoundingBoxConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.TileConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.Tile;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.*;

public class GeospatialRelationsAttributer {

	public TreeRelation getRelationToParentFragment(LdesFragment childFragment) {
		String targetWKT = getWKT(childFragment);

		return new TreeRelation(GEOSPARQL_AS_WKT, childFragment.getFragmentId(),
				WGS_84 + " " + targetWKT, WKT_DATA_TYPE, TREE_GEOSPATIALLY_CONTAINS_RELATION);

	}

	private String getWKT(LdesFragment currentFragment) {
		String fragmentWKT = currentFragment.getFragmentInfo().getValueOfKey(FRAGMENT_KEY_TILE).orElseThrow(
				() -> new MissingFragmentValueException(currentFragment.getFragmentId(), FRAGMENT_KEY_TILE));
		Tile currentTile = TileConverter.fromString(fragmentWKT);
		BoundingBox currentBoundingBox = new BoundingBox(currentTile);
		return BoundingBoxConverter.toWKT(currentBoundingBox);
	}
}
