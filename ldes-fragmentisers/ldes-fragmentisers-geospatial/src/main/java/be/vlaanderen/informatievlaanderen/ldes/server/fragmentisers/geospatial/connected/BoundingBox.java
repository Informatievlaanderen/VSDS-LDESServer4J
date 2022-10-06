package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model.Tile;

public class BoundingBox {
	private final double north;
	private final double south;
	private final double east;
	private final double west;

	public BoundingBox(Tile tile) {
		north = tile2lat(tile.y(), tile.zoom());
		south = tile2lat(tile.y() + 1, tile.zoom());
		west = tile2lon(tile.x(), tile.zoom());
		east = tile2lon(tile.x() + 1, tile.zoom());
	}

	static double tile2lon(int x, int z) {
		return x / Math.pow(2.0, z) * 360.0 - 180;
	}

	static double tile2lat(int y, int z) {
		double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
		return Math.toDegrees(Math.atan(Math.sinh(n)));
	}

	public double getNorth() {
		return north;
	}

	public double getSouth() {
		return south;
	}

	public double getEast() {
		return east;
	}

	public double getWest() {
		return west;
	}
}
