package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.bucketising;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.HashSet;
import java.util.Set;

public class CoordinateToTileStringConverter {

    private CoordinateToTileStringConverter() {
    }

    public static Set<String> calculateTiles(String wktString, final int zoom) throws ParseException {
        Set<String> tiles = new HashSet<>();

        Geometry geometry = convertWktToGeometry(wktString);

        double topTile = calculateLatitudeToTile(geometry.getEnvelopeInternal().getMaxY(), zoom);
        double leftTile = calculateLongitudeToTile(geometry.getEnvelopeInternal().getMinX(), zoom);
        double bottomTile = calculateLatitudeToTile(geometry.getEnvelopeInternal().getMinY(), zoom);
        double rightTile = calculateLongitudeToTile(geometry.getEnvelopeInternal().getMaxX(), zoom);
        double width = Math.abs(leftTile - rightTile) + 1;
        double height = Math.abs(topTile - bottomTile) + 1;

        for (var x = leftTile; x < leftTile + width; x++) {
            for (var y = topTile; y < topTile + height; y++) {
                if (geometry.intersects(createPolygonFromBbox(tileToBox(x, y, zoom)))) {
                    tiles.add("%d/%d/%d".formatted(zoom, (int) x, (int) y));
                }
            }
        }

        return tiles;
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

    private static double calculateLatitudeToTile(double lat, int zoom) {
        return (Math.floor((1 - Math.log(Math.tan(lat * Math.PI / 180) + 1 / Math.cos(lat * Math.PI / 180)) / Math.PI) / 2 * Math.pow(2, zoom)));
    }

    private static double calculateLongitudeToTile(double lon, int zoom) {
        return (Math.floor((lon + 180) / 360 * Math.pow(2, zoom)));
    }

    private static double sinh(double arg) {
        return ((Math.exp(arg) - Math.exp(-arg)) / 2);
    }

    private static double tileToLng(double x, double z) {
        return (x * 360 / Math.pow(2, z) - 180);
    }

    private static double tileToLat(double y, double z) {
        return (Math.atan(sinh(Math.PI - y * 2 * Math.PI / Math.pow(2, z))) * (180 / Math.PI));
    }

    private static Double[] tileToBox(double x, double y, double z) {
        return new Double[] {
                tileToLng(x, z),
                tileToLat(y + 1, z),
                tileToLng(x + 1, z),
                tileToLat(y, z)
        };
    }

    private static Geometry createPolygonFromBbox(Double[] tile) {
        GeometryFactory geomFactory = new GeometryFactory();

        var west = tile[0];
        var south = tile[1];
        var east = tile[2];
        var north = tile[3];

        var lowLeft = new Coordinate(west, south);
        var topLeft = new Coordinate(west, north);
        var topRight = new Coordinate(east, north);
        var lowRight = new Coordinate(east, south);

        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = lowLeft;
        coordinates[1] = lowRight;
        coordinates[2] = topRight;
        coordinates[3] = topLeft;
        coordinates[4] = lowLeft;

        return geomFactory.createPolygon(coordinates);
    }
}
