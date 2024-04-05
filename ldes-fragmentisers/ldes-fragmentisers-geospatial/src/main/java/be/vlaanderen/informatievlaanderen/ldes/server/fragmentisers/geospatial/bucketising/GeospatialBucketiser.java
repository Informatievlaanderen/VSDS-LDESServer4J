package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

			getFragmentationObjects(memberModel, geospatialConfig.fragmenterSubjectFilter(),
							geospatialConfig.fragmentationPath())
					.stream()
					.map(this::toCoordinates)
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

	private List<Coordinate> toCoordinates(Object geoObject) {
		try {
			GeometryWrapper geometryWrapper = ((GeometryWrapper) geoObject).convertSRS(SRS_URI.WGS84_CRS);
			return List.of(geometryWrapper.getXYGeometry().getCoordinates());
		} catch (Exception exception) {
			LOGGER.warn("Could not extract coordinates from statement Reason: {}", exception.getMessage());
			return List.of();
		}
	}

	private List<Object> getFragmentationObjects(Model model, String subjectFilter, String fragmentationPath) {
		return model
				.listStatements(null, ResourceFactory.createProperty(fragmentationPath), (Resource) null)
				.toList()
				.stream()
				.filter(statement -> statement.getSubject().toString().matches(subjectFilter))
				.map(this::getValue)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
	}

	private Optional<Object> getValue(Statement statement) {
		try {
			return Optional.of(statement.getObject().asLiteral().getValue());
		} catch (Exception exception) {
			LOGGER.warn("Could not extract literal from {} Reason: {}", statement, exception.getMessage());
			return Optional.empty();
		}
	}
}
