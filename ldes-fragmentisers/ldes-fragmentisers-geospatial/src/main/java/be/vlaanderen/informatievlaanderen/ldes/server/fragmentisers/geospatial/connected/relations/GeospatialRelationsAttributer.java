package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.relations.RelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.BoundingBox;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.BoundingBoxConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.TileConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.Tile;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.*;

public class GeospatialRelationsAttributer implements RelationsAttributer {

	public TreeRelation getRelationToParentFragment(Fragment childFragment) {
		String targetWKT = getWKT(childFragment);

		return new TreeRelation(GEOSPARQL_AS_WKT, childFragment.getFragmentId(),
				WGS_84 + " " + targetWKT, WKT_DATA_TYPE, TREE_GEOSPATIALLY_CONTAINS_RELATION);

	}

	private String getWKT(Fragment currentFragment) {
		String fragmentWKT = currentFragment.getValueOfKey(FRAGMENT_KEY_TILE).orElseThrow(
				() -> new MissingFragmentValueException(currentFragment.getFragmentIdString(), FRAGMENT_KEY_TILE));
		Tile currentTile = TileConverter.fromString(fragmentWKT);
		BoundingBox currentBoundingBox = new BoundingBox(currentTile);
		return BoundingBoxConverter.toWKT(currentBoundingBox);
	}

}
