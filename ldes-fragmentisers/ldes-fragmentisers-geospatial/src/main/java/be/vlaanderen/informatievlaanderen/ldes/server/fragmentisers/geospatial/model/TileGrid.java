package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.BoundingBox;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import java.util.HashSet;
import java.util.Set;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter.BoundingBoxConverter.toPolygon;

public class TileGrid {
    private final double topTile;
    private final double leftTile;
    private final double bottomTile;
    private final double rightTile;
    private final int zoom;

    public TileGrid(Envelope envelope, int zoom) {
        this.topTile = lat2Tile(envelope.getMaxY(), zoom);
        this.leftTile = long2Tile(envelope.getMinX(), zoom);
        this.bottomTile = lat2Tile(envelope.getMinY(), zoom);
        this.rightTile = long2Tile(envelope.getMaxX(), zoom);
        this.zoom = zoom;
    }

    public Set<Tile> findIntersectingTiles(Geometry geometry) {
        Set<Tile> intersectingTiles = new HashSet<>();

        calculateTilesInGrid().forEach(tile -> {
            Geometry bboxPolygon = toPolygon(new BoundingBox(tile));

            if (geometry.intersects(bboxPolygon)) {
                intersectingTiles.add(tile);
            }
        });

        return intersectingTiles;
    }

    private Set<Tile> calculateTilesInGrid() {
        Set<Tile> tiles = new HashSet<>();

        double width = Math.abs(leftTile - rightTile) + 1;
        double height = Math.abs(topTile - bottomTile) + 1;

        for (var x = leftTile; x < leftTile + width; x++) {
            for (var y = topTile; y < topTile + height; y++) {
                tiles.add(new Tile(zoom, (int) x, (int) y));
            }
        }

        return tiles;
    }

    private double lat2Tile(double lat, int zoom) {
        return (Math.floor((1 - Math.log(Math.tan(lat * Math.PI / 180) + 1 / Math.cos(lat * Math.PI / 180)) / Math.PI) / 2 * Math.pow(2, zoom)));
    }

    private double long2Tile(double lon, int zoom) {
        return (Math.floor((lon + 180) / 360 * Math.pow(2, zoom)));
    }

}
