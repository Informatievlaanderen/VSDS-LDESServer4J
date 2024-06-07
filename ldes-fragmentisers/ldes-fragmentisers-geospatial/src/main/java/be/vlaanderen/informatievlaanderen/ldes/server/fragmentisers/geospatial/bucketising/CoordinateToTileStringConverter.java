package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.Tile;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.TileGrid;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.Set;
import java.util.stream.Collectors;

public class CoordinateToTileStringConverter {

    private CoordinateToTileStringConverter() {
    }

    public static Set<String> calculateTiles(String wktString, final int zoom) throws ParseException {
        Geometry geometry = convertWktToGeometry(wktString);
        Envelope envelope = geometry.getEnvelopeInternal();
        return new TileGrid(envelope, zoom)
                .findIntersectingTiles(geometry)
                .stream()
                .map(Tile::toTileString)
                .collect(Collectors.toSet());
    }

    public static String convertCoordinate(final Coordinate coordinate, final int zoom) {
        int xtile = (int) Math.floor((coordinate.x + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor(
                (1 - Math.log(Math.tan(Math.toRadians(coordinate.y)) + 1 / Math.cos(Math.toRadians(coordinate.y)))
                        / Math.PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return (zoom + "/" + xtile + "/" + ytile);
    }

    private static Geometry convertWktToGeometry(String geoFeature) throws ParseException {
        WKTReader reader = new WKTReader();
        return reader.read(geoFeature);
    }
}
