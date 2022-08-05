package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.bucketising;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Component;

@Component
public class CoordinateToTileStringConverter {

    public String convertCoordinate(final Coordinate coordinate, final int zoom) {
        int xtile = (int) Math.floor((coordinate.x + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(coordinate.y)) + 1 / Math.cos(Math.toRadians(coordinate.y))) / Math.PI) / 2 * (1 << zoom));
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
}
