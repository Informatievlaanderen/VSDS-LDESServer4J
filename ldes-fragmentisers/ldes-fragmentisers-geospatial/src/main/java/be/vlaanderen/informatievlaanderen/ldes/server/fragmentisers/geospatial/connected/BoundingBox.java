package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.Tile;

public class BoundingBox {
	private final double north;
	private final double south;
	private final double east;
	private final double west;

	public BoundingBox(Tile tile) {
		north = tile2lat(tile.getY(), tile.getZoom());
		south = tile2lat(tile.getY() + 1, tile.getZoom());
		west = tile2lon(tile.getX(), tile.getZoom());
		east = tile2lon(tile.getX() + 1, tile.getZoom());
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
