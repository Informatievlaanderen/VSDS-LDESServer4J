package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.ModelParser;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.apache.jena.rdf.model.Model;
import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;

public class GeospatialBucketiser {
	private final GeospatialConfig geospatialConfig;
	private static final Logger LOGGER = LoggerFactory.getLogger(GeospatialBucketiser.class);

	public GeospatialBucketiser(GeospatialConfig geospatialConfig) {
		this.geospatialConfig = geospatialConfig;
	}

	public Set<String> bucketise(String memberId, Model memberModel) {
		try {
			List<Coordinate> coordinates = new ArrayList<>();

			ModelParser.getFragmentationObjects(memberModel, geospatialConfig.fragmenterSubjectFilter(),
							geospatialConfig.fragmentationPath())
					.stream()
					.map(this::toCoordinate)
					.forEach(coordinates::addAll);
			Set<String> tiles = coordinates.stream()
					.map(coordinate -> CoordinateToTileStringConverter.convertCoordinate(coordinate,
							geospatialConfig.maxZoom()))
					.collect(Collectors.toSet());
			if(tiles.isEmpty()) {
				tiles.add(DEFAULT_BUCKET_STRING);
			}
			return tiles;
		} catch (Exception exception) {
			LOGGER.warn("Could not geospatialy fragment member {} Reason: {}", memberId, exception.getMessage());
			return Set.of(DEFAULT_BUCKET_STRING);
		}
	}

	private List<Coordinate> toCoordinate(Object geoObject) {
		try {
			GeometryWrapper geometryWrapper = ((GeometryWrapper) geoObject).convertSRS(SRS_URI.WGS84_CRS);
			return List.of(geometryWrapper.getXYGeometry().getCoordinates());
		} catch (Exception e) {
			return List.of();
		}
	}
}
