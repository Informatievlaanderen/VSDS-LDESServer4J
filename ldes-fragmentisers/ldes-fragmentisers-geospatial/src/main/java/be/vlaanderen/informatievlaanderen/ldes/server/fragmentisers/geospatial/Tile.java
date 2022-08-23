package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial;

public class Tile {
	private final int zoom;
	private final int x;
	private final int y;

	public Tile(int zoom, int x, int y) {
		this.zoom = zoom;
		this.x = x;
		this.y = y;
	}

	public int getZoom() {
		return zoom;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
