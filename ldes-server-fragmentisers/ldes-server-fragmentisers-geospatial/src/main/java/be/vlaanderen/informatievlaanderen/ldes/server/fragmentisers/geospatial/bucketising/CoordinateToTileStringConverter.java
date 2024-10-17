package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.Tile;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileGrid;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;

public class CoordinateToTileStringConverter {

    private static final Logger log = LoggerFactory.getLogger(CoordinateToTileStringConverter.class);

    private CoordinateToTileStringConverter() {
    }

    public static Set<String> calculateTiles(String wktString, final int zoom) {
        try {
            Geometry geometry = convertWktToGeometry(wktString);
            Envelope boundingBoxGeometry = geometry.getEnvelopeInternal();
            return new TileGrid(boundingBoxGeometry, zoom)
                    .findIntersectingTiles(geometry)
                    .stream()
                    .map(Tile::toTileString)
                    .collect(Collectors.toSet());
        } catch (ParseException ex) {
            log.error("Could not calculate tiles for wktString {}", wktString, ex);
        }

        return emptySet();
    }

    private static Geometry convertWktToGeometry(String geoFeature) throws ParseException {
        WKTReader reader = new WKTReader();
        return reader.read(geoFeature);
    }
}
