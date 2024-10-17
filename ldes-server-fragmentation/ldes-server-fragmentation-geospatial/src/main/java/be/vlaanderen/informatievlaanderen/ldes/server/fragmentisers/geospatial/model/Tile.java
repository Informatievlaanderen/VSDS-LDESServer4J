package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.model;

public record Tile(int zoom, int x, int y) {
    public String toTileString() {
        return (zoom + "/" + x + "/" + y);
    }
}
