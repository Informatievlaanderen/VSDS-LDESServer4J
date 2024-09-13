package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.config.GeospatialConfig;
import org.apache.jena.geosparql.implementation.GeometryWrapper;
import org.apache.jena.geosparql.implementation.vocabulary.SRS_URI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.DEFAULT_BUCKET_STRING;
import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising.CoordinateToTileStringConverter.calculateTiles;

public class GeospatialBucketiser {
	private final GeospatialConfig geospatialConfig;
	private static final Logger LOGGER = LoggerFactory.getLogger(GeospatialBucketiser.class);

	public GeospatialBucketiser(GeospatialConfig geospatialConfig) {
		this.geospatialConfig = geospatialConfig;
	}

	public Set<String> createTiles(String memberId, Model memberModel) {
		try {
            Set<String> tiles = getFragmentationObjects(memberModel, geospatialConfig.fragmenterSubjectFilter(), geospatialConfig.fragmentationPath())
					.flatMap(geometryWrapper -> calculateTiles(geometryWrapper.getXYGeometry().toText(), geospatialConfig.maxZoom()).stream())
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

	private Stream<GeometryWrapper> getFragmentationObjects(Model model, String subjectFilter, String fragmentationPath) {
		return model
				.listStatements(null, ResourceFactory.createProperty(fragmentationPath), (Resource) null)
				.toList()
				.stream()
				.filter(statement -> statement.getSubject().toString().matches(subjectFilter))
				.map(this::getGeometryWrapper)
				.filter(Optional::isPresent)
				.map(Optional::get);
	}

	private Optional<GeometryWrapper> getGeometryWrapper(Statement statement) {
		try {
			return Optional
					.of(statement.getObject().asLiteral().getValue())
					.flatMap(this::convertToGeometryWrapper);
		} catch (Exception exception) {
			LOGGER.warn("Could not extract literal from {} Reason: {}", statement, exception.getMessage());
			return Optional.empty();
		}
	}

	private Optional<GeometryWrapper> convertToGeometryWrapper(Object geoObject) {
		try {
			GeometryWrapper geometryWrapper = ((GeometryWrapper) geoObject).convertSRS(SRS_URI.WGS84_CRS);
			return Optional.of(geometryWrapper);
		} catch (Exception exception) {
			LOGGER.warn("Could not extract coordinates from statement Reason: {}", exception.getMessage());
			return Optional.empty();
		}
	}
}
