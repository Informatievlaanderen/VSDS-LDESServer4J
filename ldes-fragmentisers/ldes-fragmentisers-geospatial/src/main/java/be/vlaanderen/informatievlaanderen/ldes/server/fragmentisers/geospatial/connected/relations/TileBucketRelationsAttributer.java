package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.relations;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Bucket;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.MissingFragmentValueException;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.relations.RelationsAttributer;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects.TreeRelation;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.BoundingBox;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.BoundingBoxConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.TileConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.Tile;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.constants.GeospatialConstants.*;

public class TileBucketRelationsAttributer implements RelationsAttributer {

	public void addRelationsFromRootToBottom(Bucket rootBucket, Bucket tileBucket) {
		boolean isDefaultBucket = tileBucket.getValueForKey(FRAGMENT_KEY_TILE).orElse("").equals(DEFAULT_BUCKET_STRING);
		TreeRelation treeRelation = isDefaultBucket ? TreeRelation.generic() : createGeospatialRelationToParent(tileBucket);
		rootBucket.addChildBucket(tileBucket.withRelation(treeRelation));
	}

	private TreeRelation createGeospatialRelationToParent(Bucket childBucket) {
		final String treeValue = WGS_84 + " " + getWKT(childBucket);
		return new TreeRelation(
				TREE_GEOSPATIALLY_CONTAINS_RELATION,
				treeValue,
				WKT_DATA_TYPE,
				GEOSPARQL_AS_WKT
		);
	}

	private String getWKT(Bucket currentBucket) {
		String fragmentWKT = currentBucket.getValueForKey(FRAGMENT_KEY_TILE).orElseThrow(
				() -> new MissingFragmentValueException(currentBucket.getBucketDescriptorAsString(), FRAGMENT_KEY_TILE));
		Tile currentTile = TileConverter.fromString(fragmentWKT);
		BoundingBox currentBoundingBox = new BoundingBox(currentTile);
		return BoundingBoxConverter.toWkt(currentBoundingBox);
	}
}
