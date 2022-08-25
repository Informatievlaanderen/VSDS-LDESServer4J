package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.Tile;

public class TileConverter {

	private TileConverter() {
	}

	public static Tile fromString(final String tileString) {
		String[] split = tileString.split("/");
		return new Tile(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
	}

	public static String toString(final Tile tile) {
		return tile.getZoom() + "/" + tile.getX() + "/" + tile.getY();
	}
}
